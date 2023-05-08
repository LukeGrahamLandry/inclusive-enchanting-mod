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
        if (entity.getEntityWorld().isRemote()) return;
        if (!(entity instanceof AbstractArrowEntity)) return;
        AbstractArrowEntity arrow = (AbstractArrowEntity) entity;
        Entity shooter = arrow.func_234616_v_();  // change this to getShooter when mappings update
        if (!(shooter instanceof LivingEntity)) return;

        if (arrow.getShotFromCrossbow()){
            boolean hasFlame = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, (LivingEntity) shooter) > 0;
            if (hasFlame){
                arrow.setFire(100);
            }

            int punchLevel = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, (LivingEntity) shooter);
            arrow.setKnockbackStrength(punchLevel);
        } else {
            int piercingLevel = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PIERCING, (LivingEntity) shooter);
            if (piercingLevel > 0) {
                arrow.setPierceLevel((byte)piercingLevel);
            }
        }
    }
}
