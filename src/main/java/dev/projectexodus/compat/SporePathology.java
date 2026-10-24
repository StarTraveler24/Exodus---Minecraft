package dev.projectexodus.compat;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * In-game fantasy biology integration.
 *
 * This only affects Spore creatures with an EXISTING PatientZero infection.
 * PatientZero handles sampling, genome research, strain creation and infection.
 * The combination of researched gene modules determines the anti-Spore phenotype.
 * Using reflection makes PatientZero optional at compile time. Never invent
 * a genome or infect a mob for free.
 */
@Mod(SporePathology.MOD_ID)
public final class SporePathology {
    public static final String MOD_ID = "projectexoduspathology";
    private static Method strainsMethod;
    private static boolean lookupAttempted;

    public SporePathology() { NeoForge.EVENT_BUS.register(this); }

    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity mob)) return;
        if (!(mob.level() instanceof ServerLevel world)) return;
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        if (id == null || !id.getNamespace().equals("spore")) return;
        if (mob.tickCount % 40 != 0) return;

        Collection<?> infections = infections(mob);
        if (infections.isEmpty()) return;

        float damage = 0.0f;
        boolean neurological = false;
        boolean hemorrhagic = false;
        boolean respiratory = false;

        for (Object infection : infections) {
            try {
                Object modulesRaw = infection.getClass().getMethod("modules").invoke(infection);
                if (!(modulesRaw instanceof Collection<?> modules)) continue;
                boolean cytotoxic = false, rapid = false;
                for (Object module : modules) {
                    String name = ((Enum<?>) module).name();
                    if ("CYTOTOXIN".equals(name)) cytotoxic = true;
                    if ("RAPID_REPLICATION".equals(name)) rapid = true;
                    if ("NEUROTROPISM".equals(name)) neurological = true;
                    if ("HEMORRHAGIC_FACTOR".equals(name)) hemorrhagic = true;
                    if ("RESPIRATORY_TROPISM".equals(name)) respiratory = true;
                }
                if (cytotoxic) damage += 2.0f;
                if (rapid) damage += 1.0f;
            } catch (ReflectiveOperationException | ClassCastException ignored) {
                // Unknown PatientZero strain variant: never damage a mob on a guess.
            }
        }
        // All extra effects are a fictional in-game combat mechanic.
        if (hemorrhagic) damage += 1.5f;
        if (neurological) mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1, false, true));
        if (respiratory) mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0, false, true));
        if (damage > 0) {
            DamageSource source = world.damageSources().magic();
            mob.hurt(source, Math.min(damage, 8f));
        }
    }

    private static Collection<?> infections(LivingEntity mob) {
        if (!lookupAttempted) {
            lookupAttempted = true;
            try {
                Class<?> manager = Class.forName("necrodwarf.patientzero.disease.DiseaseManager");
                strainsMethod = manager.getMethod("strains", LivingEntity.class);
            } catch (ReflectiveOperationException ignored) {
                strainsMethod = null;
            }
        }
        if (strainsMethod == null) return List.of();
        try {
            Object result = strainsMethod.invoke(null, mob);
            return result instanceof Collection<?> list ? list : List.of();
        } catch (ReflectiveOperationException ignored) {
            return List.of();
        }
    }
}
