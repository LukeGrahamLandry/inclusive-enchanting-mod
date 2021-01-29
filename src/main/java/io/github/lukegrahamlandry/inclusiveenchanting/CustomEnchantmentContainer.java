package io.github.lukegrahamlandry.inclusiveenchanting;

import com.google.common.collect.Lists;
import io.github.lukegrahamlandry.inclusiveenchanting.events.AnvilEnchantHandler;
import io.github.lukegrahamlandry.inclusiveenchanting.init.BlockInit;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.awt.event.InputEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CustomEnchantmentContainer extends EnchantmentContainer {
    private IWorldPosCallable worldPosCallable;

    public CustomEnchantmentContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, IWorldPosCallable.DUMMY);
    }

    public CustomEnchantmentContainer(int id, PlayerInventory inventory, IWorldPosCallable of) {
        super(id, inventory, of);
        this.worldPosCallable = of;
        InclusiveEnchanting.LOGGER.debug("init container");
    }

    public CustomEnchantmentContainer(int i, PlayerInventory playerInventory, PacketBuffer packetBuffer) {
        this(i, playerInventory);
    }

    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(this.worldPosCallable, playerIn, BlockInit.CUSTOM_ENCHANT_TABLE.get());
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        InclusiveEnchanting.LOGGER.debug("container closed");
        super.onContainerClosed(playerIn);
    }

    public void onCraftMatrixChanged(IInventory inventoryIn) {
        if (inventoryIn == this.tableInventory) {
            ItemStack itemstack = inventoryIn.getStackInSlot(0);
            if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
                this.worldPosCallable.consume((p_217002_2_, p_217002_3_) -> {
                    int power = 0;

                    for(int k = -1; k <= 1; ++k) {
                        for(int l = -1; l <= 1; ++l) {
                            if ((k != 0 || l != 0) && p_217002_2_.isAirBlock(p_217002_3_.add(l, 0, k)) && p_217002_2_.isAirBlock(p_217002_3_.add(l, 1, k))) {
                                power += getPower(p_217002_2_, p_217002_3_.add(l * 2, 0, k * 2));
                                power += getPower(p_217002_2_, p_217002_3_.add(l * 2, 1, k * 2));

                                if (l != 0 && k != 0) {
                                    power += getPower(p_217002_2_, p_217002_3_.add(l * 2, 0, k));
                                    power += getPower(p_217002_2_, p_217002_3_.add(l * 2, 1, k));
                                    power += getPower(p_217002_2_, p_217002_3_.add(l, 0, k * 2));
                                    power += getPower(p_217002_2_, p_217002_3_.add(l, 1, k * 2));
                                }
                            }
                        }
                    }

                    this.rand.setSeed((long)this.xpSeed.get());

                    for(int i1 = 0; i1 < 3; ++i1) {
                        this.enchantLevels[i1] = EnchantmentHelper.calcItemStackEnchantability(this.rand, i1, (int)power, itemstack);
                        this.enchantClue[i1] = -1;
                        this.worldClue[i1] = -1;
                        if (this.enchantLevels[i1] < i1 + 1) {
                            this.enchantLevels[i1] = 0;
                        }
                        this.enchantLevels[i1] = net.minecraftforge.event.ForgeEventFactory.onEnchantmentLevelSet(p_217002_2_, p_217002_3_, i1, (int)power, itemstack, enchantLevels[i1]);
                    }

                    for(int j1 = 0; j1 < 3; ++j1) {
                        if (this.enchantLevels[j1] > 0) {
                            List<EnchantmentData> list = this.getNewEnchantmentList(itemstack, j1, this.enchantLevels[j1]);
                            if (list != null && !list.isEmpty()) {
                                EnchantmentData enchantmentdata = list.get(this.rand.nextInt(list.size()));
                                this.enchantClue[j1] = Registry.ENCHANTMENT.getId(enchantmentdata.enchantment);
                                this.worldClue[j1] = enchantmentdata.enchantmentLevel;
                            }
                        }
                    }

                    this.detectAndSendChanges();
                });
            } else {
                for(int i = 0; i < 3; ++i) {
                    this.enchantLevels[i] = 0;
                    this.enchantClue[i] = -1;
                    this.worldClue[i] = -1;
                }
            }
        }

    }

    /**
     * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
     */
    public boolean enchantItem(PlayerEntity playerIn, int id) {
        ItemStack itemstack = this.tableInventory.getStackInSlot(0);
        ItemStack itemstack1 = this.tableInventory.getStackInSlot(1);
        int i = id + 1;
        if ((itemstack1.isEmpty() || itemstack1.getCount() < i) && !playerIn.abilities.isCreativeMode) {
            return false;
        } else if (this.enchantLevels[id] <= 0 || itemstack.isEmpty() || (playerIn.experienceLevel < i || playerIn.experienceLevel < this.enchantLevels[id]) && !playerIn.abilities.isCreativeMode) {
            return false;
        } else {
            this.worldPosCallable.consume((p_217003_6_, p_217003_7_) -> {
                ItemStack itemstack2 = itemstack;
                List<EnchantmentData> list = this.getNewEnchantmentList(itemstack, id, this.enchantLevels[id]);
                if (!list.isEmpty()) {
                    playerIn.onEnchant(itemstack, i);
                    boolean flag = itemstack.getItem() == Items.BOOK;
                    if (flag) {
                        itemstack2 = new ItemStack(Items.ENCHANTED_BOOK);
                        CompoundNBT compoundnbt = itemstack.getTag();
                        if (compoundnbt != null) {
                            itemstack2.setTag(compoundnbt.copy());
                        }

                        this.tableInventory.setInventorySlotContents(0, itemstack2);
                    }

                    for(int j = 0; j < list.size(); ++j) {
                        EnchantmentData enchantmentdata = list.get(j);
                        if (flag) {
                            EnchantedBookItem.addEnchantment(itemstack2, enchantmentdata);
                        } else {
                            itemstack2.addEnchantment(enchantmentdata.enchantment, enchantmentdata.enchantmentLevel);
                        }
                    }

                    if (!playerIn.abilities.isCreativeMode) {
                        itemstack1.shrink(i);
                        if (itemstack1.isEmpty()) {
                            this.tableInventory.setInventorySlotContents(1, ItemStack.EMPTY);
                        }
                    }

                    playerIn.addStat(Stats.ENCHANT_ITEM);
                    if (playerIn instanceof ServerPlayerEntity) {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayerEntity)playerIn, itemstack2, i);
                    }

                    this.tableInventory.markDirty();
                    this.xpSeed.set(playerIn.getXPSeed());
                    this.onCraftMatrixChanged(this.tableInventory);
                    p_217003_6_.playSound((PlayerEntity)null, p_217003_7_, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, p_217003_6_.rand.nextFloat() * 0.1F + 0.9F);
                }

            });
            return true;
        }
    }

    private float getPower(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos) {
        return world.getBlockState(pos).getEnchantPowerBonus(world, pos);
    }

    Random rand = new Random();
    private List<EnchantmentData> getNewEnchantmentList(ItemStack stack, int enchantSlot, int level) {
        this.rand.setSeed((long)(this.xpSeed.get() + enchantSlot));
        List<EnchantmentData> list = buildEnchantmentList(this.rand, stack, level, false);


        if (stack.getItem() == Items.BOOK && list.size() > 1) {
            list.remove(this.rand.nextInt(list.size()));
        }

        return list;
    }

    public static List<EnchantmentData> buildEnchantmentList(Random randomIn, ItemStack itemStackIn, int level, boolean allowTreasure) {
        List<EnchantmentData> list = Lists.newArrayList();
        Item item = itemStackIn.getItem();
        int i = itemStackIn.getItemEnchantability();
        if (i <= 0) {
            return list;
        } else {
            level = level + 1 + randomIn.nextInt(i / 4 + 1) + randomIn.nextInt(i / 4 + 1);
            float f = (randomIn.nextFloat() + randomIn.nextFloat() - 1.0F) * 0.15F;
            level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
            List<EnchantmentData> list1 = getEnchantmentDatas(level, itemStackIn, allowTreasure);
            if (!list1.isEmpty()) {
                list.add(WeightedRandom.getRandomItem(randomIn, list1));

                while(randomIn.nextInt(50) <= level) {
                    removeIncompatible(list1, Util.getLast(list));
                    if (list1.isEmpty()) {
                        break;
                    }

                    list.add(WeightedRandom.getRandomItem(randomIn, list1));
                    level /= 2;
                }
            }

            return list;
        }
    }

    public static List<EnchantmentData> getEnchantmentDatas(int level, ItemStack stack, boolean allowTreasure) {
        List<EnchantmentData> list = Lists.newArrayList();
        Item item = stack.getItem();
        boolean flag = stack.getItem() == Items.BOOK;

        for(Enchantment enchantment : Registry.ENCHANTMENT) {
            boolean isValidOnStack = enchantment.canApplyAtEnchantingTable(stack) || AnvilEnchantHandler.isNewValid(enchantment, stack);
            if ((!enchantment.isTreasureEnchantment() || allowTreasure) && enchantment.canGenerateInLoot() && (isValidOnStack || (flag && enchantment.isAllowedOnBooks()))) {
                for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                    if (level >= enchantment.getMinEnchantability(i) && level <= enchantment.getMaxEnchantability(i)) {
                        list.add(new EnchantmentData(enchantment, i));
                        break;
                    }
                }
            }
        }

        return list;
    }

    public static void removeIncompatible(List<EnchantmentData> dataList, EnchantmentData data) {
        Iterator<EnchantmentData> iterator = dataList.iterator();

        while(iterator.hasNext()) {
            Enchantment next = (iterator.next()).enchantment;
            if (AnvilEnchantHandler.areNewIncompateble(data.enchantment, next) || !data.enchantment.isCompatibleWith(next)) {
                iterator.remove();
            }
        }

    }
}
