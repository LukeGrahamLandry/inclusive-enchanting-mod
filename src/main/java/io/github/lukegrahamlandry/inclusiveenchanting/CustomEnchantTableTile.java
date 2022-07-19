package io.github.lukegrahamlandry.inclusiveenchanting;

import io.github.lukegrahamlandry.inclusiveenchanting.init.TileEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.Nameable;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Random;

public class CustomEnchantTableTile extends BlockEntity implements Nameable {
    public int ticks;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    public float nextPageAngle;
    public float pageAngle;
    public float tRot;
    private static final Random random = new Random();
    private Component customname;

    public CustomEnchantTableTile(BlockPos pPos, BlockState pBlockState) {
        super(TileEntityInit.ENCHANTING_TABLE.get(), pPos, pBlockState);
    }

    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (this.hasCustomName()) {
            compound.putString("CustomName", Component.Serializer.toJson(this.customname));
        }
    }

    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("CustomName", 8)) {
            this.customname = Component.Serializer.fromJson(nbt.getString("CustomName"));
        }

    }

    public static void bookAnimationTick(Level pLevel, BlockPos pPos, BlockState pState, CustomEnchantTableTile pBlockEntity) {
        pBlockEntity.pageTurningSpeed = pBlockEntity.nextPageTurningSpeed;
        pBlockEntity.pageAngle = pBlockEntity.nextPageAngle;
        Player playerentity = pBlockEntity.level.getNearestPlayer((double)pBlockEntity.worldPosition.getX() + 0.5D, (double)pBlockEntity.worldPosition.getY() + 0.5D, (double)pBlockEntity.worldPosition.getZ() + 0.5D, 3.0D, false);
        if (playerentity != null) {
            double d0 = playerentity.getX() - ((double)pBlockEntity.worldPosition.getX() + 0.5D);
            double d1 = playerentity.getZ() - ((double)pBlockEntity.worldPosition.getZ() + 0.5D);
            pBlockEntity.tRot = (float) Mth.atan2(d1, d0);
            pBlockEntity.nextPageTurningSpeed += 0.1F;
            if (pBlockEntity.nextPageTurningSpeed < 0.5F || random.nextInt(40) == 0) {
                float f1 = pBlockEntity.flipT;

                do {
                    pBlockEntity.flipT += (float)(random.nextInt(4) - random.nextInt(4));
                } while(f1 == pBlockEntity.flipT);
            }
        } else {
            pBlockEntity.tRot += 0.02F;
            pBlockEntity.nextPageTurningSpeed -= 0.1F;
        }

        while(pBlockEntity.nextPageAngle >= (float)Math.PI) {
            pBlockEntity.nextPageAngle -= ((float)Math.PI * 2F);
        }

        while(pBlockEntity.nextPageAngle < -(float)Math.PI) {
            pBlockEntity.nextPageAngle += ((float)Math.PI * 2F);
        }

        while(pBlockEntity.tRot >= (float)Math.PI) {
            pBlockEntity.tRot -= ((float)Math.PI * 2F);
        }

        while(pBlockEntity.tRot < -(float)Math.PI) {
            pBlockEntity.tRot += ((float)Math.PI * 2F);
        }

        float f2;
        for(f2 = pBlockEntity.tRot - pBlockEntity.nextPageAngle; f2 >= (float)Math.PI; f2 -= ((float)Math.PI * 2F)) {
        }

        while(f2 < -(float)Math.PI) {
            f2 += ((float)Math.PI * 2F);
        }

        pBlockEntity.nextPageAngle += f2 * 0.4F;
        pBlockEntity.nextPageTurningSpeed = Mth.clamp(pBlockEntity.nextPageTurningSpeed, 0.0F, 1.0F);
        ++pBlockEntity.ticks;
        pBlockEntity.oFlip = pBlockEntity.flip;
        float f = (pBlockEntity.flipT - pBlockEntity.flip) * 0.4F;
        float f3 = 0.2F;
        f = Mth.clamp(f, -0.2F, 0.2F);
        pBlockEntity.flipA += (f - pBlockEntity.flipA) * 0.9F;
        pBlockEntity.flip += pBlockEntity.flipA;
    }

    public Component getName() {
        return (Component)(this.customname != null ? this.customname : Component.translatable("container.enchant"));
    }

    public void setCustomName(@Nullable Component name) {
        this.customname = name;
    }

    @Nullable
    public Component getCustomName() {
        return this.customname;
    }
}
