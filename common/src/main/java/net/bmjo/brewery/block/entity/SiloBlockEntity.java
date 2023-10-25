package net.bmjo.brewery.block.entity;

import net.bmjo.brewery.block.SiloBlock;
import net.bmjo.brewery.registry.BlockEntityRegistry;
import net.bmjo.brewery.util.BreweryUtil;
import net.bmjo.brewery.util.ImplementedInventory;
import net.bmjo.brewery.util.silo.ConnectivityHandler;
import net.bmjo.brewery.util.silo.IMultiBlockEntityContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SiloBlockEntity extends BlockEntity implements IMultiBlockEntityContainer.Inventory, ImplementedInventory, MenuProvider, BlockEntityTicker<SiloBlockEntity> {
    private static final int MAX_WIDTH = 3;
    private static final int MAX_HEIGHT = 9;
    public static final int MAX_CAPACITY = MAX_WIDTH * MAX_WIDTH * MAX_HEIGHT; //STACKS
    private static final int DRY_TIME = 10 * 20;
    protected BlockPos controller;
    protected boolean updateConnectivity;
    protected int width;
    protected int height;
    private NonNullList<ItemStack> items = NonNullList.withSize(MAX_CAPACITY * 2, ItemStack.EMPTY);
    private int[] times = new int[MAX_CAPACITY];

    public SiloBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.SILO.get(), pos, state);
        updateConnectivity = false;
        height = 1;
        width = 1;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public boolean isController() {
        return controller == null || this.worldPosition.getX() == controller.getX()
                && this.worldPosition.getY() == controller.getY() && this.worldPosition.getZ() == controller.getZ();
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @Override
    public void setController(BlockPos controller) {
        if (level.isClientSide || controller.equals(this.controller))
            return;
        this.controller = controller;
    }

    @Override
    public SiloBlockEntity getControllerBE() {
        if (this.isController())
            return this;
        return level.getBlockEntity(controller) instanceof SiloBlockEntity siloBE ? siloBE : null;
    }


    @Override
    public void removeController(boolean keepContents) {
        if (level == null || level.isClientSide)
            return;
        updateConnectivity = true;
        controller = null;
        width = 1;
        height = 1;

        BlockState state = getBlockState();
        if (SiloBlock.isSilo(state)) {
            state = state.setValue(SiloBlock.BOTTOM, true);
            state = state.setValue(SiloBlock.TOP, true);
            state = state.setValue(SiloBlock.SHAPE, SiloBlock.Shape.NONE);
            level.setBlock(worldPosition, state, 23);
        }
    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState blockState, SiloBlockEntity blockEntity) {
        if (this.level == null)
            this.level = level;
        if (updateConnectivity)
            updateConnectivity();
        dry();
        //tryDropFinish();
    }

    public void updateConnectivity() {
        updateConnectivity = false;
        if (level == null || level.isClientSide)
            return;
        if (!isController())
            return;
        ConnectivityHandler.formMulti(this);
    }

    public boolean tryAddItem(ItemStack itemStack) {
        for (int slot = 0; slot < this.getCapacity(); slot++)
            if (this.getItem(slot).isEmpty()) {
                this.setItem(slot, itemStack.copy());
                itemStack.setCount(0);
                return true;
            }/*
            else if (BreweryUtil.canMergeItems(freshStack, itemStack)) {
                int free = itemStack.getMaxStackSize() - freshStack.getCount();
                int count = Math.min(itemStack.getCount(), free);
                itemStack.shrink(count);
                freshStack.grow(count);
            }
             */
        return false;
    }

    public ItemStack tryRemoveItem() {
        for (int slot = MAX_CAPACITY + this.getCapacity(); slot > MAX_CAPACITY; --slot) {
            ItemStack stack = this.getItem(slot);
            if (!stack.isEmpty())
                return this.removeItem(slot, stack.getCount());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (SiloBlock.isSilo(state)) { // safety
            state = state.setValue(SiloBlock.BOTTOM, getController().getY() == getBlockPos().getY());
            state = state.setValue(SiloBlock.TOP, getController().getY() + height - 1 == getBlockPos().getY());
            level.setBlock(worldPosition, state, 6);
        }
        if (isController())
            updateShape();
    }

    public void updateShape() {
        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    BlockState blockState = this.level.getBlockState(pos);
                    if (!SiloBlock.isSilo(blockState))
                        continue;
                    SiloBlock.Shape shape = SiloBlock.Shape.NONE;
                    // SIZE 2:
                    if (width == 2)
                        shape = xOffset == 0 ? zOffset == 0 ? SiloBlock.Shape.NORTH_WEST : SiloBlock.Shape.SOUTH_WEST
                                : zOffset == 0 ? SiloBlock.Shape.NORTH_EAST : SiloBlock.Shape.SOUTH_EAST;
                    // SIZE 3:
                    if (width == 3)
                        shape = switch (xOffset) {
                            case 0 ->
                                    zOffset == 0 ? SiloBlock.Shape.NORTH_WEST : zOffset == 2 ? SiloBlock.Shape.SOUTH_WEST : SiloBlock.Shape.WEST;
                            case 1 ->
                                    zOffset == 0 ? SiloBlock.Shape.NORTH : zOffset == 2 ? SiloBlock.Shape.SOUTH : SiloBlock.Shape.NONE;
                            case 2 ->
                                    zOffset == 0 ? SiloBlock.Shape.NORTH_EAST : zOffset == 2 ? SiloBlock.Shape.SOUTH_EAST : SiloBlock.Shape.EAST;
                            default -> SiloBlock.Shape.NONE;
                        };
                    level.setBlock(pos, blockState.setValue(SiloBlock.SHAPE, shape), 23);
                }
            }
        }
    }

    private void dry() {
        for (int fresh = 0; fresh < this.getCapacity(); fresh++) {
            ItemStack freshStack = this.getItem(fresh);
            if (!freshStack.isEmpty()) {
                int dryTime = this.times[fresh];
                dryTime++;
                if (dryTime >= DRY_TIME)
                    for (int finish = MAX_CAPACITY; finish < MAX_CAPACITY + this.getCapacity(); finish++)
                        if (this.getItem(finish).isEmpty()) {
                            ItemStack finishStack = this.removeItem(fresh, freshStack.getCount());
                            this.setItem(finish, SiloBlock.isDryItem(finishStack) ? new ItemStack(SiloBlock.DRYERS.get(finishStack.getItem()), finishStack.getCount()) : finishStack);
                            dryTime = 0;
                            break;
                        }
                this.times[fresh] = dryTime;
            }
        }
    }

    private void tryDropFinish() {
        if (!this.isOpen() || this.level == null)
            return;
        for (int finish = MAX_CAPACITY; finish < MAX_CAPACITY + this.getCapacity(); finish++) {
            ItemStack finishStack = this.getItem(finish);
            if (!finishStack.isEmpty()) {
                RandomSource rdm = RandomSource.create();
                Direction direction = BreweryUtil.HORIZONTAL_DIRECTIONS[rdm.nextInt(BreweryUtil.HORIZONTAL_DIRECTIONS.length)];
                double x = switch (direction) {
                    case EAST -> 0;
                    case WEST -> width;
                    default -> width / 2.0;
                };
                double z = switch (direction) {
                    case NORTH -> 0;
                    case SOUTH -> width;
                    default -> width / 2.0;
                };
                dropStack(this.level, this.worldPosition.getX() + x, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + z, finishStack, direction);
                return;
            }
        }
    }

    private boolean isOpen() {
        return true; //TODO weg
    }

    public static void dropStack(Level level, double x, double y, double z, ItemStack itemStack, Direction direction) {
        double g = EntityType.ITEM.getWidth();
        double h = 1.0 - g;
        double i = g / 2.0;
        double j = Math.floor(x) + level.random.nextDouble() * h + i;
        double k = Math.floor(y) + level.random.nextDouble() * h;
        double l = Math.floor(z) + level.random.nextDouble() * h + i;

        while (!itemStack.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(level, j, k, l, itemStack.split(level.random.nextInt(21) + 10));
            itemEntity.setDeltaMovement(level.random.triangle(direction.getStepX() * 0.4, 0.11485000171139836), level.random.triangle(-0.2, 0.11485000171139836), level.random.triangle(direction.getStepZ() * 0.4, 0.11485000171139836));
            level.addFreshEntity(itemEntity);
        }
    }


    // MultiBlockEntity

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        return longAxis.isHorizontal() ? this.getMaxWidth() : MAX_HEIGHT;
    }

    @Override
    public int getMaxWidth() {
        return MAX_WIDTH;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    public int getCapacity() {
        return this.width * this.width * this.height;
    }

    // Inventory

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public boolean hasInventory() {
        return isController();
    }

    // SidedInventory

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        int[] result = new int[getCapacity() * 2];
        for (int slot = 0; slot < result.length; slot++)
            result[slot] = slot < getCapacity() ? slot : MAX_CAPACITY + slot - getCapacity();
        return result;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
        return direction == Direction.UP && SiloBlock.isDryItem(itemStack) && slot < this.getCapacity() && this.getItem(slot).isEmpty();
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
        return direction != Direction.UP && slot >= MAX_CAPACITY && slot - MAX_CAPACITY < this.getCapacity();
    }

    // NBT

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        if (this.controller != null)
            BreweryUtil.putBlockPos(compoundTag, this.controller);
        compoundTag.putBoolean("Update", this.updateConnectivity);
        compoundTag.putInt("Width", this.width);
        compoundTag.putInt("Height", this.height);
        ContainerHelper.saveAllItems(compoundTag, this.items);
        compoundTag.putIntArray("Times", this.times);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.controller = BreweryUtil.readBlockPos(compoundTag);
        this.updateConnectivity = !compoundTag.contains("Update") || compoundTag.getBoolean("Update");
        this.width = compoundTag.contains("Width") ? compoundTag.getInt("Width") : 1;
        this.height = compoundTag.contains("Height") ? compoundTag.getInt("Height") : 1;
        this.items = NonNullList.withSize(MAX_CAPACITY * 2, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, this.items);
        this.times = compoundTag.contains("Times") ? compoundTag.getIntArray("Times") : new int[MAX_CAPACITY];
    }

    // Menu
    @Override
    public @NotNull Component getDisplayName() {
        return Component.empty();
    }


    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, net.minecraft.world.entity.player.Inventory inventory, Player player) {
        return null;
    }

}