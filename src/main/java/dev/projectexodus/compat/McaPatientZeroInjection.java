package dev.projectexodus.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Give PatientZero syringes and virus doses precedence over MCA dialogue.
 * PatientZero still decides strain validity, target susceptibility and consumption.
 * No fabricated infections or direct modifications to MCA are needed.
 */
public final class McaPatientZeroInjection {
    private McaPatientZeroInjection() { }

    public static void register() { NeoForge.EVENT_BUS.register(McaPatientZeroInjection.class); }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.isCanceled() || !(event.getTarget() instanceof LivingEntity target)) return;
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
        if (id == null || (!id.getNamespace().equals("mca") && !id.getNamespace().equals("minecraftcomesalive"))) return;
        ItemStack held = event.getEntity().getItemInHand(event.getHand());
        ResourceLocation heldId = BuiltInRegistries.ITEM.getKey(held.getItem());
        if (heldId == null) return;
        String itemId = heldId.toString();
        if (!itemId.equals("patientzero:virus_dose") && !itemId.equals("patientzero:empty_syringe")) return;

        InteractionResult result = held.interactLivingEntity(event.getEntity(), target, event.getHand());
        // When a PatientZero tool is held, prevent MCA dialogue from swallowing this click.
        event.setCancellationResult(result.consumesAction() ? result : InteractionResult.FAIL);
        event.setCanceled(true);
    }
}
