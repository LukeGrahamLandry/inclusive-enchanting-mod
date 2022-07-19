package io.github.lukegrahamlandry.inclusiveenchanting.init;

import io.github.lukegrahamlandry.inclusiveenchanting.CustomEnchantTableBlock;
import io.github.lukegrahamlandry.inclusiveenchanting.CustomTridentItem;
import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, InclusiveEnchanting.MOD_ID);

    public static final RegistryObject<Block> CUSTOM_ENCHANT_TABLE = BLOCKS.register("custom_enchanting_table",
            CustomEnchantTableBlock::new);

}
