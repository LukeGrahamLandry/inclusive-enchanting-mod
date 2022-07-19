package io.github.lukegrahamlandry.inclusiveenchanting;

import io.github.lukegrahamlandry.inclusiveenchanting.init.TileEntityInit;
import net.minecraft.world.level.block.*;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.Nameable;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class CustomEnchantTableBlock extends EnchantmentTableBlock {
    public CustomEnchantTableBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE));
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
        InclusiveEnchanting.LOGGER.debug("get container");
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        Component itextcomponent = ((Nameable)tileentity).getDisplayName();
        return new SimpleMenuProvider((id, inventory, player) -> {
            return new CustomEnchantmentContainer(id, inventory, ContainerLevelAccess.create(worldIn, pos));
        }, itextcomponent);
    }

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CustomEnchantTableTile(pPos, pState);
    }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, TileEntityInit.ENCHANTING_TABLE.get(), CustomEnchantTableTile::bookAnimationTick) : null;
   }

}
