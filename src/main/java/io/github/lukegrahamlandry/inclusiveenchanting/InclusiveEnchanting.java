package io.github.lukegrahamlandry.inclusiveenchanting;

import io.github.lukegrahamlandry.inclusiveenchanting.events.AnvilEnchantHandler;
import io.github.lukegrahamlandry.inclusiveenchanting.init.DataProvider;
import io.github.lukegrahamlandry.inclusiveenchanting.init.EntityInit;
import io.github.lukegrahamlandry.inclusiveenchanting.init.ItemInit;
import io.github.lukegrahamlandry.inclusiveenchanting.legacy.RegistryInit;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("inclusiveenchanting")
public class InclusiveEnchanting {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "inclusiveenchanting";

    public InclusiveEnchanting() {
        AnvilEnchantHandler.initNewValidEnchants();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        DataProvider.LOOT_MODIFIERS.register(eventBus);
        EntityInit.ENTITY_TYPES.register(eventBus);
        ItemInit.ITEMS.register(eventBus);
        RegistryInit.BLOCKS.register(eventBus);
        RegistryInit.TILE_ENTITY_TYPES.register(eventBus);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvent{
        @SubscribeEvent
        public static void runData(GatherDataEvent event){
            event.getGenerator().addProvider(new DataProvider(event.getGenerator(), MOD_ID));
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvent {
        @SubscribeEvent
        public static void replaceTrident(TickEvent.PlayerTickEvent event){
            if (!event.player.getEntityWorld().isRemote() && event.player.getHeldItem(Hand.MAIN_HAND).getItem() == Items.TRIDENT){
                ItemStack newTrident = new ItemStack(ItemInit.CUSTOM_TRIDENT.get());
                newTrident.setTag(event.player.getHeldItem(Hand.MAIN_HAND).getTag());
                event.player.setHeldItem(Hand.MAIN_HAND, newTrident);
            }
        }
    }
}
