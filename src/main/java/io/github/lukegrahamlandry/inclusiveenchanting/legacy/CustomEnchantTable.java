package io.github.lukegrahamlandry.inclusiveenchanting.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@Deprecated
class CustomEnchantTable extends Block implements EntityBlock {
    CustomEnchantTable() {
        super(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new Tile(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof Tile tile) {
                tile.tick();
            }
        };
    }

    @Deprecated
    static class Tile extends BlockEntity {
        Tile(BlockPos pPos, BlockState pState) {
            super(RegistryInit.ENCHANTING_TABLE.get(), pPos, pState);
        }

        void tick() {
            if (this.hasLevel() && !this.getLevel().isClientSide()){
                this.getLevel().setBlockAndUpdate(this.getBlockPos(), Blocks.ENCHANTING_TABLE.defaultBlockState());
            }
        }
    }
}
