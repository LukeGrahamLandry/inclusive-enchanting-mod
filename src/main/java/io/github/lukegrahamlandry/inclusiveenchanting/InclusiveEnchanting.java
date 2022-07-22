package io.github.lukegrahamlandry.inclusiveenchanting;

import io.github.lukegrahamlandry.inclusiveenchanting.events.AnvilEnchantHandler;
import io.github.lukegrahamlandry.inclusiveenchanting.init.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public static class ModEvent {
        @SubscribeEvent
        public static void runData(GatherDataEvent event){
            event.getGenerator().addProvider(event.includeServer(), new DataProvider(event.getGenerator(), MOD_ID));
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvent {
        @SubscribeEvent
        public static void replaceEnchantTable(BlockEvent.EntityPlaceEvent event){
            if (!event.getEntity().getCommandSenderWorld().isClientSide() && event.getPlacedBlock().getBlock() == Blocks.ENCHANTING_TABLE){
                event.getBlockSnapshot().getLevel().setBlock(event.getBlockSnapshot().getPos(), BlockInit.CUSTOM_ENCHANT_TABLE.get().defaultBlockState(), 2);
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
    }
}
