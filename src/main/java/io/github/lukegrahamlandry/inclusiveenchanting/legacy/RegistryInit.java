package io.github.lukegrahamlandry.inclusiveenchanting.legacy;

import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

// This was one of the first mods I ever made and I didn't know that mixins existed at the time.
// So instead I used a clunky form of registry replacement where I just swapped some blocks
// and items into my own versions the first time you interacted with them.
// Now that I'm rewriting it to use mixins, I'm keeping these objects around with the same registry names
// so that they can replace themselves back to their vanilla forms in case someone updates the mod in a preexisting world.
// I think forge might have something for this or maybe this is what vanilla data fixers are for, but I can't be bothered right now.
@Deprecated
public class RegistryInit {
    static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, InclusiveEnchanting.MOD_ID);
    static final RegistryObject<Block> CUSTOM_ENCHANT_TABLE = BLOCKS.register("custom_enchanting_table",
            CustomEnchantTable::new);

    static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, InclusiveEnchanting.MOD_ID);
    static final RegistryObject<TileEntityType<CustomEnchantTable.Tile>> ENCHANTING_TABLE
            = TILE_ENTITY_TYPES.register("custom_enchanting_table", () -> TileEntityType.Builder.of(CustomEnchantTable.Tile::new, CUSTOM_ENCHANT_TABLE.get()).build(null));

    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InclusiveEnchanting.MOD_ID);
    static final RegistryObject<Item> CUSTOM_TRIDENT = ITEMS.register("custom_trident",
            () -> new CustomTridentItem((new Item.Properties()).defaultDurability(250)));

    // No need to keep CustomTridentEntity because I was using the wrong constructor before so even if it was in entity form when someone
    // saved the world, the entity type would be stored as a vanilla trident.

    public static void init(){
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);
        TILE_ENTITY_TYPES.register(eventBus);
    }
}
