package io.github.lukegrahamlandry.inclusiveenchanting.events;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProjectileHandler {
    @SubscribeEvent
    public static void handleArrowShot(EntityJoinWorldEvent event){
        Entity entity = event.getEntity();
        if (entity.level.isClientSide()) return;
        if (!(entity instanceof AbstractArrowEntity)) return;
        AbstractArrowEntity arrow = (AbstractArrowEntity) entity;
        Entity shooter = arrow.getOwner();
        if (!(shooter instanceof LivingEntity)) return;

        if (arrow.shotFromCrossbow()){
            boolean hasFlame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, ((LivingEntity) shooter)) > 0;
            if (hasFlame){
                arrow.setRemainingFireTicks(100);
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
