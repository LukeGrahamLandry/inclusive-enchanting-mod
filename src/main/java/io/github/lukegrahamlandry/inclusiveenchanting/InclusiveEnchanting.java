package io.github.lukegrahamlandry.inclusiveenchanting;

import com.google.common.collect.Sets;
import io.github.lukegrahamlandry.inclusiveenchanting.events.SmeltingLootModifier;
import io.github.lukegrahamlandry.inclusiveenchanting.legacy.RegistryInit;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
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
        initLootModifier();
    }

    private void initLootModifier(){
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, InclusiveEnchanting.MOD_ID);
        LOOT_MODIFIERS.register("smelting", SmeltingLootModifier.Serializer::new);
        LOOT_MODIFIERS.register(eventBus);
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
}
