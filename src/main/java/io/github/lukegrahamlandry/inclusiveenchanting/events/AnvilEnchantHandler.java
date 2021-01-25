package io.github.lukegrahamlandry.inclusiveenchanting.events;

import com.google.common.collect.Sets;
import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

// in vanilla any enchant can be applied if in creative or if right item is a book
// I can change that if requested

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilEnchantHandler {
    private static HashMap<Enchantment, Function<ItemStack, Boolean>> validEnchants = new HashMap<>();
    private static List<Set<Enchantment>> incompatibleEnchants = new ArrayList<>();

    public static void initNewValidEnchants(){
        validEnchants.put(Enchantments.FLAME, (item) -> item.getItem().isCrossbow(item));
        validEnchants.put(Enchantments.PUNCH, (item) -> item.getItem().isCrossbow(item));
        validEnchants.put(Enchantments.PIERCING, (item) -> item.getItem() instanceof BowItem);
        validEnchants.put(Enchantments.FIRE_ASPECT, (item) -> item.getItem() instanceof ToolItem);
        validEnchants.put(Enchantments.QUICK_CHARGE, (item) -> item.getItem() instanceof BowItem);

        incompatibleEnchants.add(Sets.newHashSet(Enchantments.FLAME, Enchantments.MULTISHOT, Enchantments.PIERCING));
        incompatibleEnchants.add(Sets.newHashSet(Enchantments.FIRE_ASPECT, Enchantments.SILK_TOUCH, Enchantments.FORTUNE));
        incompatibleEnchants.add(Sets.newHashSet(Enchantments.QUICK_CHARGE, Enchantments.POWER));
    }

    @SubscribeEvent
    public static void handleNewEnchants(AnvilUpdateEvent event){
        ItemStack tool = event.getLeft();
        ItemStack add = event.getRight();
        Map<Enchantment, Integer> addEnchants = EnchantmentHelper.getEnchantments(add);
        Map<Enchantment, Integer> toolEnchants = EnchantmentHelper.getEnchantments(tool);

        Map<Enchantment, Integer> newEnchantments = new HashMap<>();

        AtomicInteger totalCost = new AtomicInteger(0);

        if (tool.getItem().isEnchantable(tool) && !addEnchants.isEmpty()){
            addEnchants.forEach((enchant, level) -> {
                // check if it can be applied on this tool
                boolean oldValid = enchant.canApply(tool);
                boolean newValid = validEnchants.containsKey(enchant) && validEnchants.get(enchant).apply(tool);
                if (!oldValid && !newValid) return;

                // check compatibility. ie no infinity and mending
                AtomicBoolean compatable = new AtomicBoolean(true);
                AtomicBoolean alreadyHas = new AtomicBoolean(false);
                toolEnchants.forEach(((enchantToTest, lvl) -> {
                    // check vanilla compatibility
                    if (!enchantToTest.isCompatibleWith(enchant)){
                        compatable.set(false);
                    }

                    // check new compatibility
                    incompatibleEnchants.forEach((enchantGroup) -> {
                        if (enchantGroup.contains(enchant) && enchantGroup.contains(enchantToTest)){
                            compatable.set(false);
                        }
                    });

                    if (enchantToTest == enchant){
                        alreadyHas.set(true);
                    }
                }));

                if (compatable.get()){
                    // if it already has that enchantment
                    if (alreadyHas.get()){

                        // if the levels are the same and can be added together
                        if (level.equals(toolEnchants.get(enchant))){
                            if (level + 1 <= enchant.getMaxLevel()){
                                newEnchantments.put(enchant, level + 1);
                                totalCost.addAndGet(getXPCost(enchant));
                            }

                        // if the new one is larger than the old
                        } else if (level > toolEnchants.get(enchant)){
                            newEnchantments.put(enchant, level);
                            totalCost.addAndGet(getXPCost(enchant));
                        }

                    // if is new enchantment
                    } else {
                        newEnchantments.put(enchant, level);
                        totalCost.addAndGet(getXPCost(enchant));
                    }
                }
            });

            newEnchantments.forEach(toolEnchants::put);
            ItemStack out = tool.copy();
            EnchantmentHelper.setEnchantments(toolEnchants, out);
            event.setOutput(out);
            event.setCost(totalCost.get());
            event.setMaterialCost(1);

            // TODO: repairing items wont work. reimpliemnt that logic here
        }
    }

    private static int getXPCost(Enchantment enchant){
        switch(enchant.getRarity()) {
            case COMMON:
                return 1;
            case UNCOMMON:
                return 2;
            case RARE:
                return 4;
            case VERY_RARE:
                return 8;
        }

        return 0;
    }
}
