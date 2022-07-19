package io.github.lukegrahamlandry.inclusiveenchanting;

import io.github.lukegrahamlandry.inclusiveenchanting.init.EntityInit;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class CustomTridentEntity extends ThrownTrident {
    public CustomTridentEntity(EntityType<? extends ThrownTrident> type, Level worldIn) {
        super(EntityInit.CUSTOM_TRIDENT.get(), worldIn);
    }

    public CustomTridentEntity(Level worldIn, LivingEntity thrower, ItemStack thrownStackIn) {
        super(worldIn, thrower, thrownStackIn);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        float f = 8.0F;
        if (entity instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entity;
            f += EnchantmentHelper.getDamageBonus(this.getPickupItem(), livingentity.getMobType());
            int power = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, this.getPickupItem());
            if (power > 0){
                f += (f * 0.25) * (1 + power);
            }
        }

        Entity entity1 = this.getOwner();
        DamageSource damagesource = DamageSource.trident(this, (Entity)(entity1 == null ? this : entity1));
        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
        if (entity.hurt(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity1 = (LivingEntity)entity;
                if (entity1 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity1, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)entity1, livingentity1);
                }

                this.doPostHurtEffects(livingentity1);
            }
        }

        if (this.getPierceLevel() <= 0){
            this.dealtDamage = true;
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        } else {
            this.setPierceLevel((byte) (this.getPierceLevel() - 1));
        }

        float f1 = 1.0F;
        if (this.level instanceof ServerLevel && this.level.isThundering() && EnchantmentHelper.hasChanneling(this.getPickupItem())) {
            BlockPos blockpos = entity.blockPosition();
            if (this.level.canSeeSky(blockpos)) {
                LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(this.level);
                lightningboltentity.moveTo(Vec3.atBottomCenterOf(blockpos));
                lightningboltentity.setCause(entity1 instanceof ServerPlayer ? (ServerPlayer)entity1 : null);
                this.level.addFreshEntity(lightningboltentity);
                soundevent = SoundEvents.TRIDENT_THUNDER;
                f1 = 5.0F;
            }
        }

        this.playSound(soundevent, f1, 1.0F);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }
}
