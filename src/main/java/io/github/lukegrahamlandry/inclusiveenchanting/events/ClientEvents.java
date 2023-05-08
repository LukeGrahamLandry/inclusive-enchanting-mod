package io.github.lukegrahamlandry.inclusiveenchanting.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.lukegrahamlandry.inclusiveenchanting.CustomTridentItem;
import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import io.github.lukegrahamlandry.inclusiveenchanting.init.EntityInit;
import io.github.lukegrahamlandry.inclusiveenchanting.init.ItemInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.TridentRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = InclusiveEnchanting.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEvent{
        @SubscribeEvent
        public static void replaceTrident(TickEvent.PlayerTickEvent event){
            if (!event.player.getEntityWorld().isRemote() && event.player.getHeldItem(Hand.MAIN_HAND).getItem() == Items.TRIDENT){
                ItemStack newTrident = new ItemStack(ItemInit.CUSTOM_TRIDENT.get());
                newTrident.setTag(event.player.getHeldItem(Hand.MAIN_HAND).getTag());
                event.player.setHeldItem(Hand.MAIN_HAND, newTrident);
            }
        }

        @SubscribeEvent
        public static void renderRaiseTrident(RenderHandEvent event){
            ClientPlayerEntity player = Minecraft.getInstance().player;
            boolean isUsing = player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == event.getHand();
            if (isUsing && event.getItemStack().getItem() instanceof CustomTridentItem){
                boolean flag = event.getHand() == Hand.MAIN_HAND;
                HandSide handside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
                boolean flag3 = handside == HandSide.RIGHT;
                int k = flag3 ? 1 : -1;

                MatrixStack matrixStackIn = event.getMatrixStack();
                int i = handside == HandSide.RIGHT ? 1 : -1;
                matrixStackIn.translate((double)((float)i * 0.56F), (double)(-0.52F + event.getEquipProgress() * -0.6F), (double)-0.72F);
                matrixStackIn.translate((double)((float)k * -0.5F), (double)0.7F, (double)0.1F);
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-55.0F));
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)k * 35.3F));
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)k * -9.785F));
                float f7 = (float)event.getItemStack().getUseDuration() - ((float)Minecraft.getInstance().player.getItemInUseCount() - event.getPartialTicks() + 1.0F);
                float f11 = f7 / CustomTridentItem.getDrawTime(event.getItemStack());
                if (f11 > 1.0F) {
                    f11 = 1.0F;
                }

                if (f11 > 0.1F) {
                    float f14 = MathHelper.sin((f7 - 0.1F) * 1.3F);
                    float f17 = f11 - 0.1F;
                    float f19 = f14 * f17;
                    matrixStackIn.translate((double)(f19 * 0.0F), (double)(f19 * 0.004F), (double)(f19 * 0.0F));
                }

                matrixStackIn.translate(0.0D, 0.0D, (double)(f11 * 0.2F));
                matrixStackIn.scale(1.0F, 1.0F, 1.0F + f11 * 0.2F);
                matrixStackIn.rotate(Vector3f.YN.rotationDegrees((float)k * 45.0F));


                Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(player, event.getItemStack(), flag3 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag3, matrixStackIn, event.getBuffers(), event.getLight());
                event.setCanceled(true);

            }
        }
    }

    @Mod.EventBusSubscriber(modid = InclusiveEnchanting.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvent{
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInit.CUSTOM_TRIDENT.get(), TridentRenderer::new);

            Minecraft.getInstance().getItemRenderer().getItemModelMesher().register(ItemInit.CUSTOM_TRIDENT.get(), new ModelResourceLocation("minecraft:trident_in_hand#inventory"));

            ItemModelsProperties.registerProperty(ItemInit.CUSTOM_TRIDENT.get(), new ResourceLocation("throwing"), (p_239419_0_, p_239419_1_, p_239419_2_) -> {
                return p_239419_2_ != null && p_239419_2_.isHandActive() && p_239419_2_.getActiveItemStack() == p_239419_0_ ? 1.0F : 0.0F;
            });
        }
    }
}
