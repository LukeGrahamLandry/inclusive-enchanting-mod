package io.github.lukegrahamlandry.inclusiveenchanting.events;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SmeltingLootModifier extends LootModifier {
    public SmeltingLootModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    public List<ItemStack> doApply(List<ItemStack> originalLoot, LootContext context) {
        List<ItemStack> newLoot = new ArrayList<>();
        for(ItemStack stack : originalLoot) {
            newLoot.add(smelt(stack, context.getWorld()));
        }

        return newLoot;
    }

    private static ItemStack smelt(ItemStack stack, World world) {
        return world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), world)
                .map(FurnaceRecipe::getRecipeOutput)
                .filter(itemStack -> !itemStack.isEmpty())
                .map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
                .orElse(stack);
    }

    // The conditions in the json file make sure that it only applies when the tool has fire aspect.
    public static class Serializer extends GlobalLootModifierSerializer<SmeltingLootModifier> {
        ILootCondition[] conditions;

        @Override
        public SmeltingLootModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
            this.conditions = conditionsIn;
            return new SmeltingLootModifier(conditionsIn);
        }

        @Override
        public JsonObject write(SmeltingLootModifier instance) {
            return makeConditions(this.conditions);
        }
    }
}