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

public class AnvilEnchantHandler {
    private static final HashMap<Enchantment, Function<ItemStack, Boolean>> validEnchants = new HashMap<>();
    private static final List<Set<Enchantment>> incompatibleEnchants = new ArrayList<>();

    public static void initNewValidEnchants(){
        validEnchants.put(Enchantments.FLAME, (item) -> item.getItem().isCrossbow(item));
        validEnchants.put(Enchantments.PUNCH, (item) -> item.getItem().isCrossbow(item));
        validEnchants.put(Enchantments.PIERCING, (item) -> item.getItem() instanceof BowItem || item.getItem() instanceof TridentItem);
        validEnchants.put(Enchantments.FIRE_ASPECT, (item) -> item.getItem() instanceof ToolItem);
        validEnchants.put(Enchantments.QUICK_CHARGE, (item) -> item.getItem() instanceof BowItem);
        validEnchants.put(Enchantments.KNOCKBACK, (item) -> item.getItem() instanceof ShieldItem);
        validEnchants.put(Enchantments.POWER, (item) -> item.getItem() instanceof TridentItem);

        incompatibleEnchants.add(Sets.newHashSet(Enchantments.FLAME, Enchantments.MULTISHOT, Enchantments.PIERCING));
        incompatibleEnchants.add(Sets.newHashSet(Enchantments.FIRE_ASPECT, Enchantments.SILK_TOUCH, Enchantments.FORTUNE));
        incompatibleEnchants.add(Sets.newHashSet(Enchantments.QUICK_CHARGE, Enchantments.POWER, Enchantments.IMPALING));
        incompatibleEnchants.add(Sets.newHashSet(Enchantments.CHANNELING, Enchantments.RIPTIDE, Enchantments.PIERCING));
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
}
