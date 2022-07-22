package io.github.lukegrahamlandry.inclusiveenchanting;

import com.google.common.collect.Lists;
import io.github.lukegrahamlandry.inclusiveenchanting.events.AnvilEnchantHandler;
import io.github.lukegrahamlandry.inclusiveenchanting.init.BlockInit;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.core.Registry;

import java.awt.event.InputEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.Util;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class CustomEnchantmentContainer extends EnchantmentMenu {
    private ContainerLevelAccess worldPosCallable;

    public CustomEnchantmentContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, ContainerLevelAccess.NULL);
    }

    public CustomEnchantmentContainer(int id, Inventory inventory, ContainerLevelAccess of) {
        super(id, inventory, of);
        this.worldPosCallable = of;
        InclusiveEnchanting.LOGGER.debug("init container");
    }

    public CustomEnchantmentContainer(int i, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        this(i, playerInventory);
    }

    public boolean stillValid(Player playerIn) {
        return stillValid(this.worldPosCallable, playerIn, BlockInit.CUSTOM_ENCHANT_TABLE.get());
    }

    @Override
    public void removed(Player playerIn) {
        InclusiveEnchanting.LOGGER.debug("container closed");
        super.removed(playerIn);
    }

    public void slotsChanged(Container inventoryIn) {
        if (inventoryIn == this.enchantSlots) {
            ItemStack itemstack = inventoryIn.getItem(0);
            if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
                this.worldPosCallable.execute((p_217002_2_, p_217002_3_) -> {
                    int power = 0;

                    for(int k = -1; k <= 1; ++k) {
                        for(int l = -1; l <= 1; ++l) {
                            if ((k != 0 || l != 0) && p_217002_2_.isEmptyBlock(p_217002_3_.offset(l, 0, k)) && p_217002_2_.isEmptyBlock(p_217002_3_.offset(l, 1, k))) {
                                power += getPower(p_217002_2_, p_217002_3_.offset(l * 2, 0, k * 2));
                                power += getPower(p_217002_2_, p_217002_3_.offset(l * 2, 1, k * 2));

                                if (l != 0 && k != 0) {
                                    power += getPower(p_217002_2_, p_217002_3_.offset(l * 2, 0, k));
                                    power += getPower(p_217002_2_, p_217002_3_.offset(l * 2, 1, k));
                                    power += getPower(p_217002_2_, p_217002_3_.offset(l, 0, k * 2));
                                    power += getPower(p_217002_2_, p_217002_3_.offset(l, 1, k * 2));
                                }
                            }
                        }
                    }

                    this.rand.setSeed((long)this.enchantmentSeed.get());

                    for(int i1 = 0; i1 < 3; ++i1) {
                        this.costs[i1] = EnchantmentHelper.getEnchantmentCost(this.rand, i1, (int)power, itemstack);
                        this.enchantClue[i1] = -1;
                        this.levelClue[i1] = -1;
                        if (this.costs[i1] < i1 + 1) {
                            this.costs[i1] = 0;
                        }
                        this.costs[i1] = net.minecraftforge.event.ForgeEventFactory.onEnchantmentLevelSet(p_217002_2_, p_217002_3_, i1, (int)power, itemstack, costs[i1]);
                    }

                    for(int j1 = 0; j1 < 3; ++j1) {
                        if (this.costs[j1] > 0) {
                            List<EnchantmentInstance> list = this.getNewEnchantmentList(itemstack, j1, this.costs[j1]);
                            if (list != null && !list.isEmpty()) {
                                EnchantmentInstance enchantmentdata = list.get(this.rand.nextInt(list.size()));
                                this.enchantClue[j1] = Registry.ENCHANTMENT.getId(enchantmentdata.enchantment);
                                this.levelClue[j1] = enchantmentdata.level;
                            }
                        }
                    }

                    this.broadcastChanges();
                });
            } else {
                for(int i = 0; i < 3; ++i) {
                    this.costs[i] = 0;
                    this.enchantClue[i] = -1;
                    this.levelClue[i] = -1;
                }
            }
        }

    }

    /**
     * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
     */
    public boolean clickMenuButton(Player playerIn, int id) {
        ItemStack itemstack = this.enchantSlots.getItem(0);
        ItemStack itemstack1 = this.enchantSlots.getItem(1);
        int i = id + 1;
        if ((itemstack1.isEmpty() || itemstack1.getCount() < i) && !playerIn.getAbilities().instabuild) {
            return false;
        } else if (this.costs[id] <= 0 || itemstack.isEmpty() || (playerIn.experienceLevel < i || playerIn.experienceLevel < this.costs[id]) && !playerIn.getAbilities().instabuild) {
            return false;
        } else {
            this.worldPosCallable.execute((p_217003_6_, p_217003_7_) -> {
                ItemStack itemstack2 = itemstack;
                List<EnchantmentInstance> list = this.getNewEnchantmentList(itemstack, id, this.costs[id]);
                if (!list.isEmpty()) {
                    playerIn.onEnchantmentPerformed(itemstack, i);
                    boolean flag = itemstack.getItem() == Items.BOOK;
                    if (flag) {
                        itemstack2 = new ItemStack(Items.ENCHANTED_BOOK);
                        CompoundTag compoundnbt = itemstack.getTag();
                        if (compoundnbt != null) {
                            itemstack2.setTag(compoundnbt.copy());
                        }

                        this.enchantSlots.setItem(0, itemstack2);
                    }

                    for(int j = 0; j < list.size(); ++j) {
                        EnchantmentInstance enchantmentdata = list.get(j);
                        if (flag) {
                            EnchantedBookItem.addEnchantment(itemstack2, enchantmentdata);
                        } else {
                            itemstack2.enchant(enchantmentdata.enchantment, enchantmentdata.level);
                        }
                    }

                    if (!playerIn.getAbilities().instabuild) {
                        itemstack1.shrink(i);
                        if (itemstack1.isEmpty()) {
                            this.enchantSlots.setItem(1, ItemStack.EMPTY);
                        }
                    }

                    playerIn.awardStat(Stats.ENCHANT_ITEM);
                    if (playerIn instanceof ServerPlayer) {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)playerIn, itemstack2, i);
                    }

                    this.enchantSlots.setChanged();
                    this.enchantmentSeed.set(playerIn.getEnchantmentSeed());
                    this.slotsChanged(this.enchantSlots);
                    p_217003_6_.playSound((Player)null, p_217003_7_, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, p_217003_6_.random.nextFloat() * 0.1F + 0.9F);
                }

            });
            return true;
        }
    }

    private float getPower(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos) {
        return world.getBlockState(pos).getEnchantPowerBonus(world, pos);
    }

    RandomSource rand = RandomSource.create();
    private List<EnchantmentInstance> getNewEnchantmentList(ItemStack stack, int enchantSlot, int level) {
        this.rand.setSeed((long)(this.enchantmentSeed.get() + enchantSlot));
        List<EnchantmentInstance> list = buildEnchantmentList(this.rand, stack, level, false);


        if (stack.getItem() == Items.BOOK && list.size() > 1) {
            list.remove(this.rand.nextInt(list.size()));
        }

        return list;
    }

    public static List<EnchantmentInstance> buildEnchantmentList(RandomSource randomIn, ItemStack itemStackIn, int level, boolean allowTreasure) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        Item item = itemStackIn.getItem();
        int i = itemStackIn.getEnchantmentValue();
        if (i <= 0) {
            return list;
        } else {
            level = level + 1 + randomIn.nextInt(i / 4 + 1) + randomIn.nextInt(i / 4 + 1);
            float f = (randomIn.nextFloat() + randomIn.nextFloat() - 1.0F) * 0.15F;
            level = Mth.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
            List<EnchantmentInstance> list1 = getEnchantmentDatas(level, itemStackIn, allowTreasure);
            if (!list1.isEmpty()) {
                WeightedRandom.getRandomItem(randomIn, list1).ifPresent(list::add);

                while(randomIn.nextInt(50) <= level) {
                    removeIncompatible(list1, Util.lastOf(list));
                    if (list1.isEmpty()) {
                        break;
                    }

                    WeightedRandom.getRandomItem(randomIn, list1).ifPresent(list::add);
                    level /= 2;
                }
            }

            return list;
        }
    }

    public static List<EnchantmentInstance> getEnchantmentDatas(int level, ItemStack stack, boolean allowTreasure) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        Item item = stack.getItem();
        boolean flag = stack.getItem() == Items.BOOK;

        for(Enchantment enchantment : Registry.ENCHANTMENT) {
            boolean isValidOnStack = enchantment.canApplyAtEnchantingTable(stack) || AnvilEnchantHandler.isNewValid(enchantment, stack);
            if ((!enchantment.isTreasureOnly() || allowTreasure) && enchantment.isDiscoverable() && (isValidOnStack || (flag && enchantment.isAllowedOnBooks()))) {
                for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                    if (level >= enchantment.getMinCost(i) && level <= enchantment.getMaxCost(i)) {
                        list.add(new EnchantmentInstance(enchantment, i));
                        break;
                    }
                }
            }
        }

        return list;
    }

    public static void removeIncompatible(List<EnchantmentInstance> dataList, EnchantmentInstance data) {
        Iterator<EnchantmentInstance> iterator = dataList.iterator();

        while(iterator.hasNext()) {
            Enchantment next = (iterator.next()).enchantment;
            if (AnvilEnchantHandler.areNewIncompateble(data.enchantment, next) || !data.enchantment.isCompatibleWith(next)) {
                iterator.remove();
            }
        }

    }
}
