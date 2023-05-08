package io.github.lukegrahamlandry.inclusiveenchanting.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

public class SmeltingLootModifier extends LootModifier {
    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, InclusiveEnchanting.MOD_ID);
    private static final RegistryObject<Codec<SmeltingLootModifier>> SMELTING = LOOT_MODIFIERS.register("smelting", SmeltingLootModifier::createCodec);

    public SmeltingLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    public static void init() {
        LOOT_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static Codec<SmeltingLootModifier> createCodec() {
        return RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, SmeltingLootModifier::new));
    }

    @Override
    public Codec<SmeltingLootModifier> codec() {
        return SMELTING.get();
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