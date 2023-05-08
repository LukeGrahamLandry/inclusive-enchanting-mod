package io.github.lukegrahamlandry.inclusiveenchanting.legacy;

import net.minecraft.block.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class CustomEnchantTableBlock extends EnchantingTableBlock {
    public CustomEnchantTableBlock() {
        super(AbstractBlock.Properties.from(Blocks.ENCHANTING_TABLE));
    }

    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new CustomEnchantTableTile();
    }
}
