package io.github.lukegrahamlandry.inclusiveenchanting.events;

import io.github.lukegrahamlandry.inclusiveenchanting.CustomTridentEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TridentHandler {
    @SubscribeEvent
    public static void replaceTrident(EntityJoinWorldEvent event){
        Entity entity = event.getEntity();
        if (entity.getEntityWorld().isRemote()) return;
        if (!(entity instanceof TridentEntity && !(entity instanceof CustomTridentEntity))) return;
        TridentEntity oldTrident = (TridentEntity) entity;
        Entity shooter = oldTrident.func_234616_v_();  // change this to getShooter when mappings update
        if (!(shooter instanceof LivingEntity)) return;

        // this isn't actually an error. intellij just doesnt know about access transformers
        CustomTridentEntity newTrident = new CustomTridentEntity(oldTrident.getEntityWorld(), (LivingEntity) shooter, oldTrident.getArrowStack());
        newTrident.setPosition(oldTrident.getPosX(), oldTrident.getPosY(), oldTrident.getPosZ());
        newTrident.setMotion(oldTrident.getMotion());
        newTrident.velocityChanged = true;
        newTrident.setFire(100); // test

        oldTrident.remove();
        event.getWorld().addEntity(newTrident);
    }
}
