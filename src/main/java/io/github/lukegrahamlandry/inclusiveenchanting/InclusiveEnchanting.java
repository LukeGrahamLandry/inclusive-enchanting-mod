package io.github.lukegrahamlandry.inclusiveenchanting;

import io.github.lukegrahamlandry.inclusiveenchanting.events.AnvilEnchantHandler;
import io.github.lukegrahamlandry.inclusiveenchanting.init.DataProvider;
import io.github.lukegrahamlandry.inclusiveenchanting.init.EntityInit;
import net.minecraft.client.renderer.entity.TridentRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
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
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegisterThings{
        @SubscribeEvent
        public static void runData(GatherDataEvent event){
            event.getGenerator().addProvider(new DataProvider(event.getGenerator(), MOD_ID));
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class RegisterClientThings{
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInit.CUSTOM_TRIDENT.get(), TridentRenderer::new);
        }
    }
}
