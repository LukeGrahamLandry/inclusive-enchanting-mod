package io.github.lukegrahamlandry.inclusiveenchanting;

import io.github.lukegrahamlandry.inclusiveenchanting.events.AnvilEnchantHandler;
import io.github.lukegrahamlandry.inclusiveenchanting.init.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(InclusiveEnchanting.MOD_ID)
public class InclusiveEnchanting{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "inclusiveenchanting";
    private static final HashMap<Enchantment, Function<ItemStack, Boolean>> validEnchants = new HashMap<>();
    private static final List<Set<Enchantment>> incompatibleEnchants = new ArrayList<>();

    public InclusiveEnchanting() {
        validEnchants.put(Enchantments.FLAMING_ARROWS, (item) -> item.getItem() instanceof CrossbowItem);
        validEnchants.put(Enchantments.PUNCH_ARROWS, (item) -> item.getItem() instanceof CrossbowItem);
        validEnchants.put(Enchantments.PIERCING, (item) -> item.getItem() instanceof BowItem || item.getItem() instanceof TridentItem);
        validEnchants.put(Enchantments.FIRE_ASPECT, (item) -> item.getItem() instanceof ToolItem);
        validEnchants.put(Enchantments.QUICK_CHARGE, (item) -> item.getItem() instanceof BowItem);
        validEnchants.put(Enchantments.KNOCKBACK, (item) -> item.getItem() instanceof ShieldItem);
        validEnchants.put(Enchantments.POWER_ARROWS, (item) -> item.getItem() instanceof TridentItem);

        incompatibleEnchants.add(Sets.newHashSet(Enchantments.FLAMING_ARROWS, Enchantments.MULTISHOT, Enchantments.PIERCING));
        incompatibleEnchants.add(Sets.newHashSet(Enchantments.FIRE_ASPECT, Enchantments.SILK_TOUCH, Enchantments.BLOCK_FORTUNE));
        incompatibleEnchants.add(Sets.newHashSet(Enchantments.QUICK_CHARGE, Enchantments.POWER_ARROWS, Enchantments.IMPALING));
        incompatibleEnchants.add(Sets.newHashSet(Enchantments.CHANNELING, Enchantments.RIPTIDE, Enchantments.PIERCING));

        RegistryInit.init();
        initLootModifier();
    }

    private void initLootModifier(){
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        DeferredRegister<Codec<? extends IGlobalLootModifier>>LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, InclusiveEnchanting.MOD_ID);
        LOOT_MODIFIERS.register("smelting", SmeltingLootModifier::createCodec);
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
