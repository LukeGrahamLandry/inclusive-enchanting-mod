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
public class FlameHandler {
    @SubscribeEvent
    public static void flameCrossbow(EntityJoinWorldEvent event){
        Entity entity = event.getEntity();
        if (entity.getEntityWorld().isRemote()) return;
        if (!(entity instanceof AbstractArrowEntity)) return;
        AbstractArrowEntity arrow = (AbstractArrowEntity) entity;
        if (!arrow.getShotFromCrossbow()) return;

        Entity shooter = arrow.func_234616_v_();  // change this to getShooter when mappings update
        if (!(shooter instanceof LivingEntity)) return;

        boolean hasFlame = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, (LivingEntity) shooter) > 0;
        if (hasFlame){
            arrow.setFire(100);
        }
    }
}
