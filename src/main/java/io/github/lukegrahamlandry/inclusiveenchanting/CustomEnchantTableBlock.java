package io.github.lukegrahamlandry.inclusiveenchanting;

import net.minecraft.block.*;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CustomEnchantTableBlock extends EnchantingTableBlock {
    public CustomEnchantTableBlock() {
        super(AbstractBlock.Properties.from(Blocks.ENCHANTING_TABLE));
    }

    @Override
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        InclusiveEnchanting.LOGGER.debug("get container");
        TileEntity tileentity = worldIn.getTileEntity(pos);
        ITextComponent itextcomponent = ((INameable)tileentity).getDisplayName();
        return new SimpleNamedContainerProvider((id, inventory, player) -> {
            return new CustomEnchantmentContainer(id, inventory, IWorldPosCallable.of(worldIn, pos));
        }, itextcomponent);
    }

    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new CustomEnchantTableTile();
    }

}
