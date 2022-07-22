package io.github.lukegrahamlandry.inclusiveenchanting.events;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import io.github.lukegrahamlandry.inclusiveenchanting.init.DataProvider;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SmeltingLootModifier extends LootModifier {
    public SmeltingLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    public static Codec<SmeltingLootModifier> createCodec() {
        return RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, SmeltingLootModifier::new));
    }

    @Override
    public Codec<SmeltingLootModifier> codec() {
        return DataProvider.SMELTING.get();
    }

    @Nonnull
    @Override
    public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> originalLoot, LootContext context) {
        ObjectArrayList<ItemStack> newLoot = new ObjectArrayList<>();
        for(ItemStack stack : originalLoot) {
            newLoot.add(smelt(stack, context.getLevel()));
        }

        return newLoot;
    }

    private static ItemStack smelt(ItemStack stack, Level world) {
        return world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), world)
                .map(SmeltingRecipe::getResultItem)
                .filter(itemStack -> !itemStack.isEmpty())
                .map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
                .orElse(stack);
    }
}