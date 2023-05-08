package io.github.lukegrahamlandry.inclusiveenchanting.legacy;

import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import io.github.lukegrahamlandry.inclusiveenchanting.init.DataProvider;
import io.github.lukegrahamlandry.inclusiveenchanting.init.EntityInit;
import io.github.lukegrahamlandry.inclusiveenchanting.init.ItemInit;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
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
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, InclusiveEnchanting.MOD_ID);
    public static final RegistryObject<Block> CUSTOM_ENCHANT_TABLE = BLOCKS.register("custom_enchanting_table",
            CustomEnchantTable::new);

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, InclusiveEnchanting.MOD_ID);
    public static final RegistryObject<TileEntityType<CustomEnchantTable.Tile>> ENCHANTING_TABLE
            = TILE_ENTITY_TYPES.register("custom_enchanting_table", () -> TileEntityType.Builder.create(CustomEnchantTable.Tile::new, CUSTOM_ENCHANT_TABLE.get()).build(Util.attemptDataFix(TypeReferences.BLOCK_ENTITY, "enchanting_table")));

    public static void init(){
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        DataProvider.LOOT_MODIFIERS.register(eventBus);
        EntityInit.ENTITY_TYPES.register(eventBus);
        ItemInit.ITEMS.register(eventBus);
        RegistryInit.BLOCKS.register(eventBus);
        RegistryInit.TILE_ENTITY_TYPES.register(eventBus);
    }
}
