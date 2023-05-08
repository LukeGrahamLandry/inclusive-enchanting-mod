package io.github.lukegrahamlandry.inclusiveenchanting.legacy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.world.World;

@Deprecated
class CustomTridentItem extends TridentItem {
    CustomTridentItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity player, int slot, boolean selected) {
        if (player instanceof PlayerEntity) {
            ItemStack trident = new ItemStack(Items.TRIDENT);
            trident.setTag(stack.getOrCreateTag());
            ((PlayerEntity) player).inventory.setInventorySlotContents(slot, trident);
        }
    }
}
