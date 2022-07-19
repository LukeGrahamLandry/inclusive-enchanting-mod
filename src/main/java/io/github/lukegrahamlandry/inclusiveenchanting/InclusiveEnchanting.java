package io.github.lukegrahamlandry.inclusiveenchanting;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.lukegrahamlandry.inclusiveenchanting.events.AnvilEnchantHandler;
import io.github.lukegrahamlandry.inclusiveenchanting.init.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("inclusiveenchanting")
public class InclusiveEnchanting{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "inclusiveenchanting";

    public InclusiveEnchanting() {
        AnvilEnchantHandler.initNewValidEnchants();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        DataProvider.LOOT_MODIFIERS.register(eventBus);
        EntityInit.ENTITY_TYPES.register(eventBus);
        ItemInit.ITEMS.register(eventBus);
        BlockInit.BLOCKS.register(eventBus);
        TileEntityInit.TILE_ENTITY_TYPES.register(eventBus);
        ContainerInit.CONTAINER_TYPES.register(eventBus);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvent{
        @SubscribeEvent
        public static void runData(GatherDataEvent event){
            event.getGenerator().addProvider(new DataProvider(event.getGenerator(), MOD_ID));
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvent{
        static Random rand = new Random();

        @SubscribeEvent
        public static void replaceEnchantTable(BlockEvent.EntityPlaceEvent event){
            if (!event.getEntity().getCommandSenderWorld().isClientSide() && event.getPlacedBlock().getBlock() == Blocks.ENCHANTING_TABLE){
                event.getBlockSnapshot().getWorld().setBlock(event.getBlockSnapshot().getPos(), BlockInit.CUSTOM_ENCHANT_TABLE.get().defaultBlockState(), 2);
                // event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void replaceTrident(TickEvent.PlayerTickEvent event){
            if (!event.player.getCommandSenderWorld().isClientSide() && event.player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.TRIDENT){
                ItemStack newTrident = new ItemStack(ItemInit.CUSTOM_TRIDENT.get());
                newTrident.setTag(event.player.getItemInHand(InteractionHand.MAIN_HAND).getTag());
                event.player.setItemInHand(InteractionHand.MAIN_HAND, newTrident);
            }
        }

        @SubscribeEvent
        public static void renderRaiseTrident(RenderHandEvent event){
            LocalPlayer player = Minecraft.getInstance().player;
            boolean isUsing = player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == event.getHand();
            if (isUsing && event.getItemStack().getItem() instanceof CustomTridentItem){
                boolean flag = event.getHand() == InteractionHand.MAIN_HAND;
                HumanoidArm handside = flag ? player.getMainArm() : player.getMainArm().getOpposite();
                boolean flag3 = handside == HumanoidArm.RIGHT;
                int k = flag3 ? 1 : -1;

                PoseStack matrixStackIn = event.getMatrixStack();
                int i = handside == HumanoidArm.RIGHT ? 1 : -1;
                matrixStackIn.translate((double)((float)i * 0.56F), (double)(-0.52F + event.getEquipProgress() * -0.6F), (double)-0.72F);
                matrixStackIn.translate((double)((float)k * -0.5F), (double)0.7F, (double)0.1F);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-55.0F));
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((float)k * 35.3F));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((float)k * -9.785F));
                float f7 = (float)event.getItemStack().getUseDuration() - ((float)Minecraft.getInstance().player.getUseItemRemainingTicks() - event.getPartialTicks() + 1.0F);
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


                Minecraft.getInstance().getItemInHandRenderer().renderItem(player, event.getItemStack(), flag3 ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag3, matrixStackIn, event.getBuffers(), event.getLight());
                event.setCanceled(true);

            }
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvent{
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInit.CUSTOM_TRIDENT.get(), ThrownTridentRenderer::new);

            // this renders properly in hand but too long in inventory
            // Minecraft.getInstance().getItemRenderer().getItemModelMesher().register(ItemInit.CUSTOM_TRIDENT.get(), new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
            Minecraft.getInstance().getItemRenderer().getItemModelShaper().register(ItemInit.CUSTOM_TRIDENT.get(), new ModelResourceLocation("minecraft:trident_in_hand#inventory"));

            ClientRegistry.bindTileEntityRenderer(TileEntityInit.ENCHANTING_TABLE.get(), CustomEnchantTableRenderer::new);
            MenuScreens.register(ContainerInit.ENCHANT_TABLE.get(), EnchantmentScreen::new);
        }
    }
}
