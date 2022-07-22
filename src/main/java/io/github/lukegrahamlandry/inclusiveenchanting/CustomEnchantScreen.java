package io.github.lukegrahamlandry.inclusiveenchanting;

import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.network.chat.Component;

public class CustomEnchantScreen extends EnchantmentScreen {
    public CustomEnchantScreen(CustomEnchantmentContainer container, Inventory playerInventory, Component textComponent) {
        super(container, playerInventory, textComponent);
    }
}
