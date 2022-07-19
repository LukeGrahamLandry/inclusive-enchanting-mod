package io.github.lukegrahamlandry.inclusiveenchanting.events;

import com.google.gson.JsonObject;
import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SmeltingLootModifier extends LootModifier {
    public SmeltingLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    public List<ItemStack> doApply(List<ItemStack> originalLoot, LootContext context) {
        List<ItemStack> newLoot = new ArrayList<>();
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

    public static class Serializer extends GlobalLootModifierSerializer<SmeltingLootModifier> {
        LootItemCondition[] conditions;

        @Override
        public SmeltingLootModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn) {
            this.conditions = conditionsIn;
            return new SmeltingLootModifier(conditionsIn);
        }

        @Override
        public JsonObject write(SmeltingLootModifier instance) {
            return makeConditions(this.conditions);
        }
    }
}