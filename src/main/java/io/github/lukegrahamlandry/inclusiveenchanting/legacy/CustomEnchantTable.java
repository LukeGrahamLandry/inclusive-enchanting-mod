package io.github.lukegrahamlandry.inclusiveenchanting.legacy;

import net.minecraft.block.*;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

@Deprecated
class CustomEnchantTable extends EnchantingTableBlock {
    CustomEnchantTable() {
        super(AbstractBlock.Properties.from(Blocks.ENCHANTING_TABLE));
    }

    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new Tile();
    }

    @Deprecated
    static class Tile extends TileEntity implements ITickableTileEntity {
        Tile() {
            super(RegistryInit.ENCHANTING_TABLE.get());
        }

        public void tick() {
            if (this.hasWorld() && !this.getWorld().isRemote()){
                this.getWorld().setBlockState(this.getPos(), Blocks.ENCHANTING_TABLE.getDefaultState());
            }
        }
    }
}
