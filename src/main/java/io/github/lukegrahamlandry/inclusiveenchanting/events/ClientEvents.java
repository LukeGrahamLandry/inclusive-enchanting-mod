package io.github.lukegrahamlandry.inclusiveenchanting.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import io.github.lukegrahamlandry.inclusiveenchanting.CustomEnchantTableRenderer;
import io.github.lukegrahamlandry.inclusiveenchanting.CustomTridentItem;
import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import io.github.lukegrahamlandry.inclusiveenchanting.init.ContainerInit;
import io.github.lukegrahamlandry.inclusiveenchanting.init.EntityInit;
import io.github.lukegrahamlandry.inclusiveenchanting.init.ItemInit;
import io.github.lukegrahamlandry.inclusiveenchanting.init.TileEntityInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = InclusiveEnchanting.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value= Dist.CLIENT)
public class ClientEvents {
    @Mod.EventBusSubscriber(modid = InclusiveEnchanting.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModClientSubscriber {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // this renders properly in hand but too long in inventory
            // Minecraft.getInstance().getItemRenderer().getItemModelMesher().register(ItemInit.CUSTOM_TRIDENT.get(), new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
            Minecraft.getInstance().getItemRenderer().getItemModelShaper().register(ItemInit.CUSTOM_TRIDENT.get(), new ModelResourceLocation("minecraft:trident_in_hand#inventory"));

            MenuScreens.register(ContainerInit.ENCHANT_TABLE.get(), EnchantmentScreen::new);
        }

        @SubscribeEvent
        public static void onClientSetup(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityInit.CUSTOM_TRIDENT.get(), ThrownTridentRenderer::new);

            event.registerBlockEntityRenderer(TileEntityInit.ENCHANTING_TABLE.get(), CustomEnchantTableRenderer::new);
        }
    }

    public static class ForgeClientSubscriber{
        @SubscribeEvent
        public static void renderRaiseTrident(RenderHandEvent event){
            LocalPlayer player = Minecraft.getInstance().player;
            boolean isUsing = player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == event.getHand();
            if (isUsing && event.getItemStack().getItem() instanceof CustomTridentItem){
                boolean flag = event.getHand() == InteractionHand.MAIN_HAND;
                HumanoidArm handside = flag ? player.getMainArm() : player.getMainArm().getOpposite();
                boolean flag3 = handside == HumanoidArm.RIGHT;
                int k = flag3 ? 1 : -1;

                PoseStack matrixStackIn = event.getPoseStack();
                int i = handside == HumanoidArm.RIGHT ? 1 : -1;
                matrixStackIn.translate((double)((float)i * 0.56F), (double)(-0.52F + event.getEquipProgress() * -0.6F), (double)-0.72F);
                matrixStackIn.translate((double)((float)k * -0.5F), (double)0.7F, (double)0.1F);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-55.0F));
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((float)k * 35.3F));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((float)k * -9.785F));
                float f7 = (float)event.getItemStack().getUseDuration() - ((float)Minecraft.getInstance().player.getUseItemRemainingTicks() - event.getPartialTick() + 1.0F);
                float f11 = f7 / CustomTridentItem.getDrawTime(event.getItemStack());
                if (f11 > 1.0F) {
                    f11 = 1.0F;
                }

                if (f11 > 0.1F) {
                    float f14 = Mth.sin((f7 - 0.1F) * 1.3F);
                    float f17 = f11 - 0.1F;
                    float f19 = f14 * f17;
                    matrixStackIn.translate((double)(f19 * 0.0F), (double)(f19 * 0.004F), (double)(f19 * 0.0F));
                }

                matrixStackIn.translate(0.0D, 0.0D, (double)(f11 * 0.2F));
                matrixStackIn.scale(1.0F, 1.0F, 1.0F + f11 * 0.2F);
                matrixStackIn.mulPose(Vector3f.YN.rotationDegrees((float)k * 45.0F));


                Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(player, event.getItemStack(), flag3 ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag3, matrixStackIn, event.getMultiBufferSource(), event.getPackedLight());
                event.setCanceled(true);

            }
        }
    }
}
