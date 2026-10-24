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

    public SporePathology() { NeoForge.EVENT_BUS.register(this); McaPatientZeroInjection.register(); }

    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity mob)) return;
        if (!(mob.level() instanceof ServerLevel world)) return;
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        if (id == null || !id.getNamespace().equals("spore")) return;
        if (mob.tickCount % 40 != 0) return;
        // Do not affect players, wildlife, or living, uninfected Spore organisms.

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
                boolean evasive = false;
                for (Object module : modules) {
                    String name = ((Enum<?>) module).name();
                    if ("CYTOTOXIN".equals(name)) cytotoxic = true;
                    if ("IMMUNE_EVASION".equals(name)) evasive = true;
                    if ("RAPID_REPLICATION".equals(name)) rapid = true;
                    if ("NEUROTROPISM".equals(name)) neurological = true;
                    if ("HEMORRHAGIC_FACTOR".equals(name)) hemorrhagic = true;
                    if ("RESPIRATORY_TROPISM".equals(name)) respiratory = true;
                }
                if (cytotoxic) damage += evasive ? 2.75f : 2.0f;
                if (rapid) damage += evasive ? 1.5f : 1.0f;
                // Existing PatientZero gene only: a modest fantasy bonus for overcoming Spore adaptation.
                // Evasion alone never causes damage and each tick remains capped at 8 HP.
            } catch (ReflectiveOperationException | ClassCastException ignored) {
                // Unknown PatientZero strain variant: never damage a mob on a guess.
            }
        }
        // All extra effects are a fictional in-game combat mechanic.
        if (hemorrhagic) damage += 1.5f;
        if (neurological) mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1, false, true));
        if (respiratory) mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0, false, true));
        if (damage > 0) {
            // Fictional adaptation meter advances with repeated applications.
            // IMMUNE_EVASION affects how strongly the monster can adapt.
            var memory = mob.getPersistentData();
            int adaptation = memory.getInt("exodusSporeAdaptation");
            int maxAdaptation = 100;
            boolean evasiveInAnyStrain = hasGene(infections, "IMMUNE_EVASION");
            int growth = evasiveInAnyStrain ? 1 : 4;
            // High adaptation reduces the bonus damage from repeated exposures.
            float protection = (adaptation / 100.0f) * 0.7f;
            float adjusted = Math.max(0.25f, Math.min(damage, 8f) * (1.0f - protection));
            mob.hurt(world.damageSources().magic(), adjusted);
            memory.putInt("exodusSporeAdaptation", Math.min(maxAdaptation, adaptation + growth));
        }
    }

    private static boolean hasGene(Collection<?> strains, String gene) {
        for (Object strain : strains) {
            try {
                Object modules = strain.getClass().getMethod("modules").invoke(strain);
                if (modules instanceof Collection<?> collection) {
                    for (Object module : collection) {
                        if (module instanceof Enum<?> value && gene.equals(value.name())) return true;
                    }
                }
            } catch (ReflectiveOperationException ignored) { }
        }
        return false;
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
