package io.github.lukegrahamlandry.inclusiveenchanting;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;

import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class CustomTridentItem extends TridentItem {
    public CustomTridentItem(Item.Properties properties) {
        super(properties);

        ItemProperties.register(this, new ResourceLocation("throwing"), (p_239419_0_, p_239419_1_, p_239419_2_, seed) -> {
            return p_239419_2_ != null && p_239419_2_.isUsingItem() && p_239419_2_.getUseItem() == p_239419_0_ ? 1.0F : 0.0F;
        });
    }

    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player) {
            Player playerentity = (Player)entityLiving;
            int i = this.getUseDuration(stack) - timeLeft;
            if (i >= getDrawTime(stack)) {
                int j = EnchantmentHelper.getRiptide(stack);
                if (j <= 0 || playerentity.isInWaterOrRain()) {
                    if (!worldIn.isClientSide) {
                        stack.hurtAndBreak(1, playerentity, (player) -> {
                            player.broadcastBreakEvent(entityLiving.getUsedItemHand());
                        });
                        if (j == 0) {
                            ThrownTrident tridententity = new CustomTridentEntity(worldIn, playerentity, stack);
                            tridententity.shootFromRotation(playerentity, playerentity.getXRot(), playerentity.getYRot(), 0.0F, 2.5F + (float)j * 0.5F, 1.0F);
                            if (playerentity.getAbilities().instabuild) {
                                tridententity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                            }

                            worldIn.addFreshEntity(tridententity);
                            worldIn.playSound((Player)null, tridententity, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                            if (!playerentity.getAbilities().instabuild) {
                                playerentity.getInventory().removeItem(stack);
                            }
                        }
                    }

                    playerentity.awardStat(Stats.ITEM_USED.get(this));
                    if (j > 0) {
                        float f7 = playerentity.getYRot();
                        float f = playerentity.getXRot();
                        float f1 = -Mth.sin(f7 * ((float)Math.PI / 180F)) * Mth.cos(f * ((float)Math.PI / 180F));
                        float f2 = -Mth.sin(f * ((float)Math.PI / 180F));
                        float f3 = Mth.cos(f7 * ((float)Math.PI / 180F)) * Mth.cos(f * ((float)Math.PI / 180F));
                        float f4 = Mth.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
                        float f5 = 3.0F * ((1.0F + (float)j) / 4.0F);
                        f1 = f1 * (f5 / f4);
                        f2 = f2 * (f5 / f4);
                        f3 = f3 * (f5 / f4);
                        playerentity.push((double)f1, (double)f2, (double)f3);
                        playerentity.startAutoSpinAttack(20);
                        if (playerentity.isOnGround()) {
                            float f6 = 1.1999999F;
                            playerentity.move(MoverType.SELF, new Vec3(0.0D, (double)1.1999999F, 0.0D));
                        }

                        SoundEvent soundevent;
                        if (j >= 3) {
                            soundevent = SoundEvents.TRIDENT_RIPTIDE_3;
                        } else if (j == 2) {
                            soundevent = SoundEvents.TRIDENT_RIPTIDE_2;
                        } else {
                            soundevent = SoundEvents.TRIDENT_RIPTIDE_1;
                        }

                        worldIn.playSound((Player)null, playerentity, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }

                }
            }
        }
    }

    public static int getDrawTime(ItemStack stack) {
        return 10 - (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.QUICK_CHARGE, stack) * 2);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new ISTER();
            }
        });
    }

    static class ISTER extends BlockEntityWithoutLevelRenderer {
        TridentModel trident;
        ISTER() {
            super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
            trident = new TridentModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.TRIDENT));
        }
        public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformTypeIn, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
            boolean flag = transformTypeIn == ItemTransforms.TransformType.GUI || transformTypeIn == ItemTransforms.TransformType.GROUND || transformTypeIn == ItemTransforms.TransformType.FIXED;
            if (flag){
                BakedModel modelIn = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
                modelIn = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, modelIn, transformTypeIn, false);
                matrixStack.translate(-0.60D, -0.75D, -0.75D);
                if (modelIn.isCustomRenderer()) { IClientItemExtensions.of(stack).getCustomRenderer().renderByItem(stack, transformTypeIn, matrixStack, buffer, combinedLight, combinedOverlay); }
                else {
                    matrixStack.scale(1.6f,1.6f,1.6f);
                    VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(buffer,  ItemBlockRenderTypes.getRenderType(stack, true), true, stack.hasFoil());
                    Minecraft.getInstance().getItemRenderer().renderModelLists(modelIn, stack, combinedLight, combinedOverlay, matrixStack, ivertexbuilder);
                }
                matrixStack.popPose();
            } else {
                matrixStack.pushPose();
                matrixStack.scale(1.0F, -1.0F, -1.0F);
                VertexConsumer ivertexbuilder1 = ItemRenderer.getFoilBufferDirect(buffer, this.trident.renderType(TridentModel.TEXTURE), false, stack.hasFoil());
                this.trident.renderToBuffer(matrixStack, ivertexbuilder1, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStack.popPose();
            }

        }
    }
}
