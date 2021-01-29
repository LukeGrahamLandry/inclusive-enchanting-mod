package io.github.lukegrahamlandry.inclusiveenchanting.init;

import io.github.lukegrahamlandry.inclusiveenchanting.CustomEnchantmentContainer;
import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerInit {
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(
            ForgeRegistries.CONTAINERS, InclusiveEnchanting.MOD_ID);

    public static final RegistryObject<ContainerType<CustomEnchantmentContainer>> ENCHANT_TABLE = CONTAINER_TYPES
            .register("custom_enchant_table", () -> IForgeContainerType.create(CustomEnchantmentContainer::new));
}
