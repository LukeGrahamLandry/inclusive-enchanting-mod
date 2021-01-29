package io.github.lukegrahamlandry.inclusiveenchanting;

import net.minecraft.client.gui.screen.EnchantmentScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.util.text.ITextComponent;

public class CustomEnchantScreen extends EnchantmentScreen {
    public CustomEnchantScreen(CustomEnchantmentContainer container, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(container, playerInventory, textComponent);
    }
}
