package io.github.lukegrahamlandry.inclusiveenchanting.legacy;

import net.minecraft.block.*;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

@Deprecated
class CustomEnchantTable extends EnchantingTableBlock {
    CustomEnchantTable() {
        super(AbstractBlock.Properties.copy(Blocks.ENCHANTING_TABLE));
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader level) {
        return new Tile();
    }

    @Deprecated
    static class Tile extends TileEntity implements ITickableTileEntity {
        Tile() {
            super(RegistryInit.ENCHANTING_TABLE.get());
        }

        @Override
        public void tick() {
            if (this.hasLevel() && !this.getLevel().isClientSide()){
                this.getLevel().setBlockAndUpdate(this.getBlockPos(), Blocks.ENCHANTING_TABLE.defaultBlockState());
            }
        }
    }
}
