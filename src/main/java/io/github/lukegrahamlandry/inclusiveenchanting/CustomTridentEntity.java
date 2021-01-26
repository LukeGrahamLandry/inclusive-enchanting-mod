package io.github.lukegrahamlandry.inclusiveenchanting;

import io.github.lukegrahamlandry.inclusiveenchanting.init.EntityInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class CustomTridentEntity extends TridentEntity {
    public CustomTridentEntity(EntityType<? extends TridentEntity> type, World worldIn) {
        super(EntityInit.CUSTOM_TRIDENT.get(), worldIn);
    }

    public CustomTridentEntity(World worldIn, LivingEntity thrower, ItemStack thrownStackIn) {
        super(worldIn, thrower, thrownStackIn);
    }

    public CustomTridentEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }
}
