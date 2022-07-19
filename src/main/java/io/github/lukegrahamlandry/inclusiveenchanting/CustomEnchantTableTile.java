package io.github.lukegrahamlandry.inclusiveenchanting;

import io.github.lukegrahamlandry.inclusiveenchanting.init.TileEntityInit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.Nameable;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;
import java.util.Random;

public class CustomEnchantTableTile extends BlockEntity implements Nameable, TickableBlockEntity {
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

    public CustomEnchantTableTile() {
        super(TileEntityInit.ENCHANTING_TABLE.get());
    }

    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        if (this.hasCustomName()) {
            compound.putString("CustomName", Component.Serializer.toJson(this.customname));
        }

        return compound;
    }

    public void load(BlockState state, CompoundTag nbt) {
        super.load(state, nbt);
        if (nbt.contains("CustomName", 8)) {
            this.customname = Component.Serializer.fromJson(nbt.getString("CustomName"));
        }

    }

    public void tick() {
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        this.pageAngle = this.nextPageAngle;
        Player playerentity = this.level.getNearestPlayer((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D, 3.0D, false);
        if (playerentity != null) {
            double d0 = playerentity.getX() - ((double)this.worldPosition.getX() + 0.5D);
            double d1 = playerentity.getZ() - ((double)this.worldPosition.getZ() + 0.5D);
            this.tRot = (float) Mth.atan2(d1, d0);
            this.nextPageTurningSpeed += 0.1F;
            if (this.nextPageTurningSpeed < 0.5F || random.nextInt(40) == 0) {
                float f1 = this.flipT;

                do {
                    this.flipT += (float)(random.nextInt(4) - random.nextInt(4));
                } while(f1 == this.flipT);
            }
        } else {
            this.tRot += 0.02F;
            this.nextPageTurningSpeed -= 0.1F;
        }

        while(this.nextPageAngle >= (float)Math.PI) {
            this.nextPageAngle -= ((float)Math.PI * 2F);
        }

        while(this.nextPageAngle < -(float)Math.PI) {
            this.nextPageAngle += ((float)Math.PI * 2F);
        }

        while(this.tRot >= (float)Math.PI) {
            this.tRot -= ((float)Math.PI * 2F);
        }

        while(this.tRot < -(float)Math.PI) {
            this.tRot += ((float)Math.PI * 2F);
        }

        float f2;
        for(f2 = this.tRot - this.nextPageAngle; f2 >= (float)Math.PI; f2 -= ((float)Math.PI * 2F)) {
        }

        while(f2 < -(float)Math.PI) {
            f2 += ((float)Math.PI * 2F);
        }

        this.nextPageAngle += f2 * 0.4F;
        this.nextPageTurningSpeed = Mth.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
        ++this.ticks;
        this.oFlip = this.flip;
        float f = (this.flipT - this.flip) * 0.4F;
        float f3 = 0.2F;
        f = Mth.clamp(f, -0.2F, 0.2F);
        this.flipA += (f - this.flipA) * 0.9F;
        this.flip += this.flipA;
    }

    public Component getName() {
        return (Component)(this.customname != null ? this.customname : new TranslatableComponent("container.enchant"));
    }

    public void setCustomName(@Nullable Component name) {
        this.customname = name;
    }

    @Nullable
    public Component getCustomName() {
        return this.customname;
    }
}
