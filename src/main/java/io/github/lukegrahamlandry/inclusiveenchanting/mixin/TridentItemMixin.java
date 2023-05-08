package io.github.lukegrahamlandry.inclusiveenchanting.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(TridentItem.class)
public class TridentItemMixin {
    // When you release a trident, it checks how long you've been using it to see if it should be allowed to shoot.
    // So I just replace the hard coded value it compares to with one calculated based on what level of quick charge you have.
    // This is made easier since tridents don't even change the entity's speed based on draw time like bows do.
    @ModifyConstant(method = "releaseUsing", constant = @Constant(intValue = 10))
    private int onPlayerStoppedUsing(int value, ItemStack stack, World world, LivingEntity player, int timeLeft) {
        return 10 - (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, stack) * 2);
    }
}
