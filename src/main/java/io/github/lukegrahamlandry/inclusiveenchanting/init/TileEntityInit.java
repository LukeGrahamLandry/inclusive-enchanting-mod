package io.github.lukegrahamlandry.inclusiveenchanting.init;

import io.github.lukegrahamlandry.inclusiveenchanting.CustomEnchantTableTile;
import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, InclusiveEnchanting.MOD_ID);

    public static final RegistryObject<BlockEntityType<CustomEnchantTableTile>> ENCHANTING_TABLE
            = TILE_ENTITY_TYPES.register("custom_enchanting_table", () -> BlockEntityType.Builder.of(CustomEnchantTableTile::new, BlockInit.CUSTOM_ENCHANT_TABLE.get()).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "enchanting_table")));
}
