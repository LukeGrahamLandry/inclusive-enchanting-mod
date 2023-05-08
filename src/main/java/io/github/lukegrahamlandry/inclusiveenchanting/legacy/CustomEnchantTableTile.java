package io.github.lukegrahamlandry.inclusiveenchanting.legacy;

import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class CustomEnchantTableTile extends TileEntity implements ITickableTileEntity {
    public CustomEnchantTableTile() {
        super(RegistryInit.ENCHANTING_TABLE.get());
    }

    public void tick() {
        if (this.hasWorld() && !this.getWorld().isRemote()){
            this.getWorld().setBlockState(this.getPos(), Blocks.ENCHANTING_TABLE.getDefaultState());
        }
    }
}
