package io.github.lukegrahamlandry.inclusiveenchanting;

import io.github.lukegrahamlandry.inclusiveenchanting.events.SmeltingLootModifier;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.data.DataGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DataProvider extends GlobalLootModifierProvider {
    public DataProvider(DataGenerator gen, String modid){
        super(gen, modid);
    }

    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, InclusiveEnchanting.MOD_ID);
    private static final RegistryObject<SmeltingLootModifier.Serializer> SMELTING = LOOT_MODIFIERS.register("smelting", SmeltingLootModifier.Serializer::new);

    @Override
    protected void start() {
        // it seems like this replaces the json but im afraid to get rid of it just incase
        add("smelting", SMELTING.get(), new SmeltingLootModifier(
                new ILootCondition[]{
                        MatchTool.builder(
                                ItemPredicate.Builder.create().enchantment(
                                        new EnchantmentPredicate(Enchantments.FIRE_ASPECT, MinMaxBounds.IntBound.atLeast(1))))
                                .build()
                })
        );
    }
}