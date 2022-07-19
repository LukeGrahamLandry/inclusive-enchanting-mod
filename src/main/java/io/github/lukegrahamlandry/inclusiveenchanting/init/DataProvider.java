package io.github.lukegrahamlandry.inclusiveenchanting.init;

import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import io.github.lukegrahamlandry.inclusiveenchanting.events.SmeltingLootModifier;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
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
                new LootItemCondition[]{
                        MatchTool.toolMatches(
                                ItemPredicate.Builder.item().hasEnchantment(
                                        new EnchantmentPredicate(Enchantments.FIRE_ASPECT, MinMaxBounds.Ints.atLeast(1))))
                                .build()
                })
        );
    }
}