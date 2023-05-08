package io.github.lukegrahamlandry.inclusiveenchanting.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemInHandRenderer.class)
public class FirstPersonRendererMixin {
    // The value 10.0F appears twice in the method but only gets executed when you're holding a trident.
    @ModifyConstant(constant = @Constant(floatValue = 10.0F), method = "renderArmWithItem")
    private float renderQuickCharge(float value, AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand handIn, float swingProgress, ItemStack stack, float equippedProgress, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
        if (stack.getItem() instanceof TridentItem) {
            return 10 - (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.QUICK_CHARGE, stack) * 2);
        } else {
            return value;
        }
    }
}
