package io.github.lukegrahamlandry.inclusiveenchanting.events;

import com.google.common.collect.Sets;
import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.item.*;
import net.minecraft.network.chat.TextComponent;
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

import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TridentItem;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilEnchantHandler {
    private static HashMap<Enchantment, Function<ItemStack, Boolean>> validEnchants = new HashMap<>();
    private static List<Set<Enchantment>> incompatibleEnchants = new ArrayList<>();

    public static void initNewValidEnchants(){
        validEnchants.put(Enchantments.FLAMING_ARROWS, (item) -> item.getItem().useOnRelease(item));
        validEnchants.put(Enchantments.PUNCH_ARROWS, (item) -> item.getItem().useOnRelease(item));
        validEnchants.put(Enchantments.PIERCING, (item) -> item.getItem() instanceof BowItem || item.getItem() instanceof TridentItem);
        validEnchants.put(Enchantments.FIRE_ASPECT, (item) -> item.getItem() instanceof DiggerItem);
        validEnchants.put(Enchantments.QUICK_CHARGE, (item) -> item.getItem() instanceof BowItem);
        validEnchants.put(Enchantments.KNOCKBACK, (item) -> item.getItem() instanceof ShieldItem);
        validEnchants.put(Enchantments.POWER_ARROWS, (item) -> item.getItem() instanceof TridentItem);

        incompatibleEnchants.add(Sets.newHashSet(Enchantments.FLAMING_ARROWS, Enchantments.MULTISHOT, Enchantments.PIERCING));
        incompatibleEnchants.add(Sets.newHashSet(Enchantments.FIRE_ASPECT, Enchantments.SILK_TOUCH, Enchantments.BLOCK_FORTUNE));
        incompatibleEnchants.add(Sets.newHashSet(Enchantments.QUICK_CHARGE, Enchantments.POWER_ARROWS, Enchantments.IMPALING));
        incompatibleEnchants.add(Sets.newHashSet(Enchantments.CHANNELING, Enchantments.RIPTIDE, Enchantments.PIERCING));
    }

    @SubscribeEvent
    public static void handleAnvil(AnvilUpdateEvent event){
        if (isWrongItem(event)) return;
        doNewEnchants(event);
        doRepairRename(event);
    }

    private static boolean isWrongItem(AnvilUpdateEvent event) {
        return !event.getRight().isEmpty() && event.getRight().isDamageableItem() && event.getLeft().getItem() != event.getRight().getItem();
    }

    public static boolean isNewValid(Enchantment enchant, ItemStack stack){
        return validEnchants.containsKey(enchant) && validEnchants.get(enchant).apply(stack);
    }

    public static boolean areNewIncompateble(Enchantment enchant, Enchantment enchant2){
        for (Set<Enchantment> enchantGroup : incompatibleEnchants){
            if (!enchant.equals(enchant2) && enchantGroup.contains(enchant) && enchantGroup.contains(enchant2)){
                return true;
            }
        }

        return false;
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
                boolean oldValid = enchant.canEnchant(tool);
                boolean newValid = isNewValid(enchant, tool); // validEnchants.containsKey(enchant) && validEnchants.get(enchant).apply(tool);
                if (!oldValid && !newValid) return;

                // check compatibility. ie no infinity and mending

                AtomicBoolean alreadyHas = new AtomicBoolean(false);
                toolEnchants.forEach(((enchantToTest, lvl) -> {
                    // check vanilla compatibility
                    if (!enchantToTest.isCompatibleWith(enchant)){
                        compatable.set(false);
                    }

                    // check new compatibility
                    if (areNewIncompateble(enchant, enchantToTest)){
                        compatable.set(false);
                    }

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
        j = j + itemstack.getBaseRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getBaseRepairCost());
        int materialCost = 0;
        int levelCost = 0;
        boolean flag = false;

        if (!itemstack2.isEmpty()) {
            flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
            if (itemstack1.isDamageableItem() && itemstack1.getItem().isValidRepairItem(itemstack, itemstack2)) {
                int l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                if (l2 <= 0) {
                    // this.resultSlots.setInventorySlotContents(0, ItemStack.EMPTY);
                    levelCost = 0;
                    return;
                }

                int i3;
                for(i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
                    int j3 = itemstack1.getDamageValue() - l2;
                    itemstack1.setDamageValue(j3);
                    ++i;
                    l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                }

                materialCost = i3;
            } else {
                if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isDamageableItem())) {
                    // this.resultSlots.setInventorySlotContents(0, ItemStack.EMPTY);
                    levelCost = 0;
                    return;
                }

                if (itemstack1.isDamageableItem() && !flag) {
                    int l = itemstack.getMaxDamage() - itemstack.getDamageValue();
                    int i1 = itemstack2.getMaxDamage() - itemstack2.getDamageValue();
                    int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                    int k1 = l + j1;
                    int l1 = itemstack1.getMaxDamage() - k1;
                    if (l1 < 0) {
                        l1 = 0;
                    }

                    if (l1 < itemstack1.getDamageValue()) {
                        itemstack1.setDamageValue(l1);
                        i += 2;
                    }
                }
            }
        }

        if (StringUtils.isBlank(event.getName())) {
            if (itemstack.hasCustomHoverName()) {
                k = 1;
                i += k;
                itemstack1.resetHoverName();
            }
        } else if (!event.getName().equals(itemstack.getHoverName().getString())) {
            k = 1;
            i += k;
            itemstack1.setHoverName(new TextComponent(event.getName()));
        }
        if (flag && !itemstack1.isBookEnchantable(itemstack2)) itemstack1 = ItemStack.EMPTY;

        levelCost = j + i;
        if (i <= 0) {
            itemstack1 = ItemStack.EMPTY;
        }

        if (k == i && k > 0 && levelCost >= 40) {
            levelCost = 39;
        }

        if (levelCost >= 40 && !event.getPlayer().abilities.instabuild) {
            itemstack1 = ItemStack.EMPTY;
        }

        if (!itemstack1.isEmpty()) {
            int k2 = itemstack1.getBaseRepairCost();
            if (!itemstack2.isEmpty() && k2 < itemstack2.getBaseRepairCost()) {
                k2 = itemstack2.getBaseRepairCost();
            }

            if (k != i || k == 0) {
                k2 = AnvilMenu.calculateIncreasedRepairCost(k2);
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
