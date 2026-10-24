package dev.projectexodus.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;

@Mod(ProjectExodusCompat.MODID)
public final class ProjectExodusCompat {
    public static final String MODID = "projectexoduscompat";
    public ProjectExodusCompat() { NeoForge.EVENT_BUS.register(this); }

    private static final TagKey<net.minecraft.world.entity.EntityType<?>> SPORE_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("spore", "fungus_entities"));

    private static boolean isSpore(LivingEntity entity) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return entity.getType().is(SPORE_ENTITIES) || (id != null && id.getNamespace().equals("spore"));
    }
    private static boolean isProtectedWorld(LivingEntity entity) {
        ResourceLocation dimension = entity.level().dimension().location();
        return dimension.getNamespace().equals("rocketnautics")
            || dimension.getNamespace().equals("rocketnautics_addon")
            || dimension.equals(ResourceLocation.fromNamespaceAndPath("minecraft", "the_nether"))
            || dimension.equals(ResourceLocation.fromNamespaceAndPath("projectexodus", "asteroid_belt"));
    }
    @SubscribeEvent
    public void tick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity mob) || mob.level().isClientSide()) return;
        if (!isSpore(mob) || !isProtectedWorld(mob)) return;
        mob.setAirSupply(mob.getMaxAirSupply());
        if (mob.level().dimension().location().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "the_nether"))) {
            mob.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 45, 0, true, false));
        }
    }
    @SubscribeEvent
    public void incomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity mob = event.getEntity();
        if (!isSpore(mob) || !isProtectedWorld(mob)) return;
        if (!mob.level().dimension().location().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "the_nether")) && (event.getSource().is(DamageTypes.DROWN) || event.getSource().is(DamageTypes.IN_WALL) || event.getSource().is(DamageTypes.FREEZE))) {
            event.setCanceled(true);
        }
        if (mob.level().dimension().location().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "the_nether"))
                && (event.getSource().is(DamageTypes.LAVA)
                 || event.getSource().is(DamageTypes.IN_FIRE)
                 || event.getSource().is(DamageTypes.ON_FIRE)
                 || event.getSource().is(DamageTypes.HOT_FLOOR))) {
            event.setCanceled(true);
        }
    }
}
