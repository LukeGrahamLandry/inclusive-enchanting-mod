package io.github.lukegrahamlandry.inclusiveenchanting.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin {
    // Could inject in EnchantmentHelper#getModifierForCreature, but then I'd need to check that you're on a trident,
    // and it would annoy me to force every attack ever to do that check.
    @ModifyConstant(method = "onHitEntity", constant = @Constant(floatValue = 8.0F))
    private float adjustBaseDamageByPower(float f, EntityRayTraceResult p_213868_1_) {
        TridentEntity self = (TridentEntity) (Object) this;
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
    @Redirect(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/TridentEntity;setDeltaMovement(Lnet/minecraft/util/math/vector/Vector3d;)V"))
    private void handlePiercing(TridentEntity instance, Vector3d vector3d) {
        TridentEntity self = (TridentEntity) (Object) this;
        if (self.getPierceLevel() <= 0){
            self.dealtDamage = true;
            self.setDeltaMovement(self.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        } else {
            self.setPierceLevel((byte) (self.getPierceLevel() - 1));
        }
    }

    // Normally it always sets dealtDamage to true, but instead want to only maybe do it in handlePiercing.
    // 181 = jdk.internal.org.objectweb.asm.Opcodes.PUTFIELD
    @Redirect(method = "onHitEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/projectile/TridentEntity;dealtDamage:Z", opcode=181))
    private void preventSetDealtDamage(TridentEntity instance, boolean value) {
        // NO-OP;
    }
}
