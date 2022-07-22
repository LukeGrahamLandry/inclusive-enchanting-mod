package io.github.lukegrahamlandry.inclusiveenchanting.init;

import io.github.lukegrahamlandry.inclusiveenchanting.CustomEnchantmentContainer;
import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerInit {
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(
            ForgeRegistries.MENU_TYPES, InclusiveEnchanting.MOD_ID);

    public static final RegistryObject<MenuType<CustomEnchantmentContainer>> ENCHANT_TABLE = CONTAINER_TYPES
            .register("custom_enchant_table", () -> IForgeMenuType.create(CustomEnchantmentContainer::new));
}
