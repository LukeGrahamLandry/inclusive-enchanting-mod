package io.github.lukegrahamlandry.inclusiveenchanting.mixin;

import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    // Vanilla's EnchantmentHelper#buildEnchantmentList picks enchants by iterating over all of them and calling this method for each (then removing incompatible ones later).
    // This method also works on anvils because the default `canApply` delegates to `canApplyAtEnchantingTable`.
    // All the vanilla enchantments that override canApplyAtEnchantingTable forward to the super method if they don't return true themselves.
    // This method is added by a forge patch so has no remapping. https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/enchantment/Enchantment.java.patch
    @Inject(at = @At("HEAD"), method = "canApplyAtEnchantingTable", cancellable = true, remap = false)
    private void canApplyAtEnchantingTable(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        Enchantment self = (Enchantment) (Object) this;
        if (InclusiveEnchanting.isNewValid(self, stack)) {
            cir.setReturnValue(true);
        }
    }

    // This is a final method in Enchantment, so it gets called regardless of how subclasses implement canApplyTogether.
    // We know nothing will directly call canApplyTogether because it's protected.
    @Inject(at = @At("HEAD"), method = "isCompatibleWith", cancellable = true)
    private void isCompatibleWith(Enchantment other, CallbackInfoReturnable<Boolean> cir){
        Enchantment self = (Enchantment) (Object) this;
        if (InclusiveEnchanting.areNewIncompatible(self, other)) {
            cir.setReturnValue(false);
        }
    }
}
