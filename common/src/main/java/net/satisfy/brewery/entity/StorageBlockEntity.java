package net.satisfy.brewery.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.satisfy.brewery.registry.BlockEntityRegistry;

public class StorageBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> inventory;
    private ContainerOpenersCounter stateManager;

    public StorageBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, SoundEvents.CHEST_OPEN, SoundEvents.CHEST_CLOSE);
    }

    public StorageBlockEntity(BlockPos pos, BlockState state, SoundEvent openSound, SoundEvent closeSound) {
        super(BlockEntityRegistry.STORAGE_BLOCK_ENTITY.get(), pos, state);
        this.inventory = NonNullList.withSize(18, ItemStack.EMPTY);
        this.stateManager = new ContainerOpenersCounter() {
            @Override
            protected void onOpen(Level world, BlockPos pos, BlockState state) {
                world.setBlock(pos, state.setValue(BlockStateProperties.OPEN, true), 3);
                playSound(StorageBlockEntity.this.level, pos, openSound);
            }

            @Override
            protected void onClose(Level world, BlockPos pos, BlockState state) {
                world.setBlock(pos, state.setValue(BlockStateProperties.OPEN, false), 3);
                playSound(StorageBlockEntity.this.level, pos, closeSound);
            }

            static void playSound(Level level, BlockPos blockPos, SoundEvent soundEvent) {
                double d = (double) blockPos.getX() + 0.5;
                double e = (double) blockPos.getY() + 0.5;
                double f = (double) blockPos.getZ() + 0.5;
                level.playSound(null, d, e, f, soundEvent, SoundSource.BLOCKS, 0.7f, level.random.nextFloat() * 0.1f + 0.9f);
            }

            @Override
            protected void openerCountChanged(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
            }

            @Override
            protected boolean isOwnContainer(Player player) {
                if (player.containerMenu instanceof ChestMenu) {
                    Container inventory = ((ChestMenu) player.containerMenu).getContainer();
                    return inventory == StorageBlockEntity.this;
                } else {
                    return false;
                }
            }

        };
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (!this.trySaveLootTable(nbt)) {
            ContainerHelper.saveAllItems(nbt, this.inventory);
        }

    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(nbt)) {
            ContainerHelper.loadAllItems(nbt, this.inventory);
        }
    }

    @Override
    public int getContainerSize() {
        return 18;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> list) {
        this.inventory = list;
    }

    @Override
    protected Component getDefaultName() {
        return Component.empty();
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new ChestMenu(MenuType.GENERIC_9x2, syncId, playerInventory, this, 2);
    }

    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.stateManager.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.stateManager.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void tick() {
        if (!this.remove) {
            this.stateManager.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void setOpen(BlockState state, boolean open) {
        this.level.setBlock(this.getBlockPos(), state.setValue(BlockStateProperties.OPEN, open), 3);
    }

}