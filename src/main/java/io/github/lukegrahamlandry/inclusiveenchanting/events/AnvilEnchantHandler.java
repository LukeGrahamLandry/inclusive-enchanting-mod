package io.github.lukegrahamlandry.inclusiveenchanting.events;

import com.google.common.collect.Sets;
import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.*;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;

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
        validEnchants.put(Enchantments.KNOCKBACK, (item) -> item.getItem() instanceof ShieldItem);
        validEnchants.put(Enchantments.POWER, (item) -> item.getItem() instanceof TridentItem);

        incompatibleEnchants.add(Sets.newHashSet(Enchantments.FLAME, Enchantments.MULTISHOT, Enchantments.PIERCING));
        incompatibleEnchants.add(Sets.newHashSet(Enchantments.FIRE_ASPECT, Enchantments.SILK_TOUCH, Enchantments.FORTUNE));
        incompatibleEnchants.add(Sets.newHashSet(Enchantments.QUICK_CHARGE, Enchantments.POWER));
    }

    @SubscribeEvent
    public static void handleAnvil(AnvilUpdateEvent event){
        if (isWrongItem(event)) return;
        doNewEnchants(event);
        doRepairRename(event);
    }

    private static boolean isWrongItem(AnvilUpdateEvent event) {
        return !event.getRight().isEmpty() && event.getRight().isDamageable() && event.getLeft().getItem() != event.getRight().getItem();
    }

    public static void doNewEnchants(AnvilUpdateEvent event){
        ItemStack tool = event.getLeft();
        ItemStack add = event.getRight();
        Map<Enchantment, Integer> addEnchants = EnchantmentHelper.getEnchantments(add);
        Map<Enchantment, Integer> toolEnchants = EnchantmentHelper.getEnchantments(tool);

        Map<Enchantment, Integer> newEnchantments = new HashMap<>();

        AtomicInteger totalCost = new AtomicInteger(0);

        boolean isBook = add.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(add).isEmpty();

        if (tool.getItem().isEnchantable(tool) && !addEnchants.isEmpty()){
            AtomicBoolean compatable = new AtomicBoolean(true);

            addEnchants.forEach((enchant, level) -> {
                // check if it can be applied on this tool
                boolean oldValid = enchant.canApply(tool);
                boolean newValid = validEnchants.containsKey(enchant) && validEnchants.get(enchant).apply(tool);
                if (!oldValid && !newValid) return;

                // check compatibility. ie no infinity and mending

                AtomicBoolean alreadyHas = new AtomicBoolean(false);
                toolEnchants.forEach(((enchantToTest, lvl) -> {
                    // check vanilla compatibility
                    if (!enchantToTest.isCompatibleWith(enchant)){
                        compatable.set(false);
                    }

                    // check new compatibility
                    incompatibleEnchants.forEach((enchantGroup) -> {
                        if (!enchant.equals(enchantToTest) && enchantGroup.contains(enchant) && enchantGroup.contains(enchantToTest)){
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
                                totalCost.addAndGet(getXPCost(enchant, level, isBook));
                            }

                            // if the new one is larger than the old
                        } else if (level > toolEnchants.get(enchant)){
                            newEnchantments.put(enchant, level);
                            totalCost.addAndGet(getXPCost(enchant, level, isBook));
                        }

                        // if is new enchantment
                    } else {
                        newEnchantments.put(enchant, level);
                        totalCost.addAndGet(getXPCost(enchant, level, isBook));
                    }
                }
            });

            if (compatable.get()){
                newEnchantments.forEach(toolEnchants::put);
                ItemStack out = tool.copy();
                EnchantmentHelper.setEnchantments(toolEnchants, out);
                event.setOutput(out);
                event.setCost(totalCost.get());
                event.setMaterialCost(1);
            } else {
                event.setCanceled(true);
            }

        }
    }

    public static void doRepairRename(AnvilUpdateEvent event){
        ItemStack itemstack = event.getOutput();
        int i = event.getCost();
        int j = 0;
        int k = 0;
        ItemStack itemstack1 = itemstack.copy();
        ItemStack itemstack2 = event.getRight();
        j = j + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());
        int materialCost = 0;
        int levelCost = 0;
        boolean flag = false;

        if (!itemstack2.isEmpty()) {
            flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
            if (itemstack1.isDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2)) {
                int l2 = Math.min(itemstack1.getDamage(), itemstack1.getMaxDamage() / 4);
                if (l2 <= 0) {
                    // this.field_234642_c_.setInventorySlotContents(0, ItemStack.EMPTY);
                    levelCost = 0;
                    return;
                }

                int i3;
                for(i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
                    int j3 = itemstack1.getDamage() - l2;
                    itemstack1.setDamage(j3);
                    ++i;
                    l2 = Math.min(itemstack1.getDamage(), itemstack1.getMaxDamage() / 4);
                }

                materialCost = i3;
            } else {
                if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isDamageable())) {
                    // this.field_234642_c_.setInventorySlotContents(0, ItemStack.EMPTY);
                    levelCost = 0;
                    return;
                }

                if (itemstack1.isDamageable() && !flag) {
                    int l = itemstack.getMaxDamage() - itemstack.getDamage();
                    int i1 = itemstack2.getMaxDamage() - itemstack2.getDamage();
                    int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                    int k1 = l + j1;
                    int l1 = itemstack1.getMaxDamage() - k1;
                    if (l1 < 0) {
                        l1 = 0;
                    }

                    if (l1 < itemstack1.getDamage()) {
                        itemstack1.setDamage(l1);
                        i += 2;
                    }
                }
            }
        }

        if (StringUtils.isBlank(event.getName())) {
            if (itemstack.hasDisplayName()) {
                k = 1;
                i += k;
                itemstack1.clearCustomName();
            }
        } else if (!event.getName().equals(itemstack.getDisplayName().getString())) {
            k = 1;
            i += k;
            itemstack1.setDisplayName(new StringTextComponent(event.getName()));
        }
        if (flag && !itemstack1.isBookEnchantable(itemstack2)) itemstack1 = ItemStack.EMPTY;

        levelCost = j + i;
        if (i <= 0) {
            itemstack1 = ItemStack.EMPTY;
        }

        if (k == i && k > 0 && levelCost >= 40) {
            levelCost = 39;
        }

        if (levelCost >= 40 && !event.getPlayer().abilities.isCreativeMode) {
            itemstack1 = ItemStack.EMPTY;
        }

        if (!itemstack1.isEmpty()) {
            int k2 = itemstack1.getRepairCost();
            if (!itemstack2.isEmpty() && k2 < itemstack2.getRepairCost()) {
                k2 = itemstack2.getRepairCost();
            }

            if (k != i || k == 0) {
                k2 = RepairContainer.getNewRepairCost(k2);
            }

            itemstack1.setRepairCost(k2);
        }


        event.setOutput(itemstack1);
        event.setCost(levelCost);
        if (materialCost > 0) event.setMaterialCost(materialCost);
    }


    private static int getXPCost(Enchantment enchant, int level, boolean isBook){
        int cost = 0;
        switch(enchant.getRarity()) {
            case COMMON:
                cost =  1;
                break;
            case UNCOMMON:
                cost =  2;
                break;
            case RARE:
                cost =  4;
                break;
            case VERY_RARE:
                cost = 8;
        }

        if (isBook) {
            cost = Math.max(1, cost / 2);
        }

        return cost * level;
    }
}
