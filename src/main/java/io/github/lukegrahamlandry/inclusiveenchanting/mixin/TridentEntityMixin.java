package io.github.lukegrahamlandry.inclusiveenchanting.mixin;

import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThrownTrident.class)
public abstract class TridentEntityMixin {
    // Could inject in EnchantmentHelper#getModifierForCreature, but then I'd need to check that you're on a trident,
    // and it would annoy me to force every attack ever to do that check.
    @ModifyConstant(method = "onHitEntity", constant = @Constant(floatValue = 8.0F))
    private float adjustBaseDamageByPower(float f, EntityHitResult p_213868_1_) {
        ThrownTrident self = (ThrownTrident) (Object) this;
        int power = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, self.getPickupItem());
        if (power > 0){
            f += (f * 0.25) * (1 + power);
        }
        return f;
    }

    // Normally it changes its direction to bounce off whatever it hit.
    // When it tries to do that, first check if it has piercing, and if so just keep going (but with less piercing).
    // Only once its pierced the maximum number of targets should it bounce off and stop dealing damage.
    // TODO: doesn't seem to work for multiple levels of piercing. It bounces off the second entity. Do arrows check that it doesn't hit the same thing multiple times?
    @Redirect(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownTrident;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void handlePiercing(ThrownTrident instance, Vec3 vector3d) {
        ThrownTrident self = (ThrownTrident) (Object) this;
        if (self.getPierceLevel() <= 0){
            self.dealtDamage = true;
            self.setDeltaMovement(self.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        } else {
            self.setPierceLevel((byte) (self.getPierceLevel() - 1));
        }
    }

    // Normally it always sets dealtDamage to true, but instead want to only maybe do it in handlePiercing.
    // 181 = jdk.internal.org.objectweb.asm.Opcodes.PUTFIELD
    @Redirect(method = "onHitEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/projectile/ThrownTrident;dealtDamage:Z", opcode=181))
    private void preventSetDealtDamage(ThrownTrident instance, boolean value) {
        // NO-OP;
    }
}
