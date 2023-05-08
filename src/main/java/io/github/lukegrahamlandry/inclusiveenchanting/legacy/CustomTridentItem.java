package io.github.lukegrahamlandry.inclusiveenchanting.legacy;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;

@Deprecated
class CustomTridentItem extends TridentItem {
    CustomTridentItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity player, int slot, boolean selected) {
        if (player instanceof Player p) {
            ItemStack trident = new ItemStack(Items.TRIDENT);
            trident.setTag(stack.getOrCreateTag());
            p.getInventory().setItem(slot, trident);
        }
    }
}
