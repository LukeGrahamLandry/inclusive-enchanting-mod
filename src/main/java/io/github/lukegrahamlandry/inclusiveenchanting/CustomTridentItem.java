package io.github.lukegrahamlandry.inclusiveenchanting;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.model.TridentModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class CustomTridentItem extends TridentItem {
    public CustomTridentItem(Item.Properties properties) {
        super(properties.setISTER(() -> ISTER::new));

        ItemModelsProperties.registerProperty(this, new ResourceLocation("throwing"), (p_239419_0_, p_239419_1_, p_239419_2_) -> {
            return p_239419_2_ != null && p_239419_2_.isHandActive() && p_239419_2_.getActiveItemStack() == p_239419_0_ ? 1.0F : 0.0F;
        });
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)entityLiving;
            int i = this.getUseDuration(stack) - timeLeft;
            if (i >= getDrawTime(stack)) {
                int j = EnchantmentHelper.getRiptideModifier(stack);
                if (j <= 0 || playerentity.isWet()) {
                    if (!worldIn.isRemote) {
                        stack.damageItem(1, playerentity, (player) -> {
                            player.sendBreakAnimation(entityLiving.getActiveHand());
                        });
                        if (j == 0) {
                            TridentEntity tridententity = new CustomTridentEntity(worldIn, playerentity, stack);
                            tridententity.func_234612_a_(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0F, 2.5F + (float)j * 0.5F, 1.0F);
                            if (playerentity.abilities.isCreativeMode) {
                                tridententity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                            }

                            worldIn.addEntity(tridententity);
                            worldIn.playMovingSound((PlayerEntity)null, tridententity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            if (!playerentity.abilities.isCreativeMode) {
                                playerentity.inventory.deleteStack(stack);
                            }
                        }
                    }

                    playerentity.addStat(Stats.ITEM_USED.get(this));
                    if (j > 0) {
                        float f7 = playerentity.rotationYaw;
                        float f = playerentity.rotationPitch;
                        float f1 = -MathHelper.sin(f7 * ((float)Math.PI / 180F)) * MathHelper.cos(f * ((float)Math.PI / 180F));
                        float f2 = -MathHelper.sin(f * ((float)Math.PI / 180F));
                        float f3 = MathHelper.cos(f7 * ((float)Math.PI / 180F)) * MathHelper.cos(f * ((float)Math.PI / 180F));
                        float f4 = MathHelper.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
                        float f5 = 3.0F * ((1.0F + (float)j) / 4.0F);
                        f1 = f1 * (f5 / f4);
                        f2 = f2 * (f5 / f4);
                        f3 = f3 * (f5 / f4);
                        playerentity.addVelocity((double)f1, (double)f2, (double)f3);
                        playerentity.startSpinAttack(20);
                        if (playerentity.isOnGround()) {
                            float f6 = 1.1999999F;
                            playerentity.move(MoverType.SELF, new Vector3d(0.0D, (double)1.1999999F, 0.0D));
                        }

                        SoundEvent soundevent;
                        if (j >= 3) {
                            soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
                        } else if (j == 2) {
                            soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
                        } else {
                            soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
                        }

                        worldIn.playMovingSound((PlayerEntity)null, playerentity, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }

                }
            }
        }
    }

    public static int getDrawTime(ItemStack stack) {
        return 10 - (EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, stack) * 2);
    }

    static class ISTER extends ItemStackTileEntityRenderer {
        TridentModel trident = new TridentModel();
        public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transformTypeIn, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
            boolean flag = transformTypeIn == ItemCameraTransforms.TransformType.GUI || transformTypeIn == ItemCameraTransforms.TransformType.GROUND || transformTypeIn == ItemCameraTransforms.TransformType.FIXED;
            if (flag){
                IBakedModel modelIn = Minecraft.getInstance().getItemRenderer().getItemModelMesher().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
                modelIn = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, modelIn, transformTypeIn, false);
                matrixStack.translate(-0.60D, -0.75D, -0.75D);
                if (modelIn.isLayered()) { net.minecraftforge.client.ForgeHooksClient.drawItemLayered(Minecraft.getInstance().getItemRenderer(), modelIn, stack, matrixStack, buffer, combinedLight, combinedOverlay, true); }
                else {
                    matrixStack.scale(1.6f,1.6f,1.6f);
                    IVertexBuilder ivertexbuilder = ItemRenderer.getEntityGlintVertexBuilder(buffer,  RenderTypeLookup.func_239219_a_(stack, true), true, stack.hasEffect());
                    Minecraft.getInstance().getItemRenderer().renderModel(modelIn, stack, combinedLight, combinedOverlay, matrixStack, ivertexbuilder);
                }
                matrixStack.pop();
            } else {
                matrixStack.push();
                matrixStack.scale(1.0F, -1.0F, -1.0F);
                IVertexBuilder ivertexbuilder1 = ItemRenderer.getEntityGlintVertexBuilder(buffer, this.trident.getRenderType(TridentModel.TEXTURE_LOCATION), false, stack.hasEffect());
                this.trident.render(matrixStack, ivertexbuilder1, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStack.pop();
            }

        }
    }
}
