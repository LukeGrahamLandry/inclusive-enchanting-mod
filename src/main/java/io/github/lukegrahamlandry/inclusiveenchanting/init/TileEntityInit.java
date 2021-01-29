package io.github.lukegrahamlandry.inclusiveenchanting.init;

import io.github.lukegrahamlandry.inclusiveenchanting.CustomEnchantTableTile;
import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityInit {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, InclusiveEnchanting.MOD_ID);

    public static final RegistryObject<TileEntityType<CustomEnchantTableTile>> ENCHANTING_TABLE
            = TILE_ENTITY_TYPES.register("custom_enchanting_table", () -> TileEntityType.Builder.create(CustomEnchantTableTile::new, BlockInit.CUSTOM_ENCHANT_TABLE.get()).build(Util.attemptDataFix(TypeReferences.BLOCK_ENTITY, "enchanting_table")));
}
