package io.github.lukegrahamlandry.inclusiveenchanting.events;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProjectileHandler {
    @SubscribeEvent
    public static void handleArrowShot(EntityJoinLevelEvent event){
        Entity entity = event.getEntity();
        if (entity.getCommandSenderWorld().isClientSide()) return;
        if (!(entity instanceof AbstractArrow)) return;
        AbstractArrow arrow = (AbstractArrow) entity;
        Entity shooter = arrow.getOwner();  // change this to getShooter when mappings update
        if (!(shooter instanceof LivingEntity)) return;

        if (arrow.shotFromCrossbow()){
            boolean hasFlame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, (LivingEntity) shooter) > 0;
            if (hasFlame){
                arrow.setSecondsOnFire(100);
            }

            int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH_ARROWS, (LivingEntity) shooter);
            arrow.setKnockback(punchLevel);
        } else {
            int piercingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PIERCING, (LivingEntity) shooter);
            if (piercingLevel > 0) {
                arrow.setPierceLevel((byte)piercingLevel);
            }
        }
    }
}
