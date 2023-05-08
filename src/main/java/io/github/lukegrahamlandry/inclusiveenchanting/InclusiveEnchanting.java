package io.github.lukegrahamlandry.inclusiveenchanting;

import com.google.common.collect.Sets;
import io.github.lukegrahamlandry.inclusiveenchanting.init.DataProvider;
import io.github.lukegrahamlandry.inclusiveenchanting.init.EntityInit;
import io.github.lukegrahamlandry.inclusiveenchanting.init.ItemInit;
import io.github.lukegrahamlandry.inclusiveenchanting.legacy.RegistryInit;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Mod(InclusiveEnchanting.MOD_ID)
public class InclusiveEnchanting {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "inclusiveenchanting";
    private static final HashMap<Enchantment, Function<ItemStack, Boolean>> validEnchants = new HashMap<>();
    private static final List<Set<Enchantment>> incompatibleEnchants = new ArrayList<>();

    public InclusiveEnchanting() {
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

        RegistryInit.init();
    }

    public static boolean isNewValid(Enchantment enchant, ItemStack stack){
        return validEnchants.containsKey(enchant) && validEnchants.get(enchant).apply(stack);
    }

    public static boolean areNewIncompatible(Enchantment enchant, Enchantment enchant2){
        for (Set<Enchantment> enchantGroup : incompatibleEnchants){
            if (!enchant.equals(enchant2) && enchantGroup.contains(enchant) && enchantGroup.contains(enchant2)){
                return true;
            }
        }

        return false;
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvent{
        @SubscribeEvent
        public static void runData(GatherDataEvent event){
            event.getGenerator().addProvider(new DataProvider(event.getGenerator(), MOD_ID));
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvent {
        @SubscribeEvent
        public static void replaceTrident(TickEvent.PlayerTickEvent event){
            if (!event.player.getEntityWorld().isRemote() && event.player.getHeldItem(Hand.MAIN_HAND).getItem() == Items.TRIDENT){
                ItemStack newTrident = new ItemStack(ItemInit.CUSTOM_TRIDENT.get());
                newTrident.setTag(event.player.getHeldItem(Hand.MAIN_HAND).getTag());
                event.player.setHeldItem(Hand.MAIN_HAND, newTrident);
            }
        }
    }
}
