package net.bmjo.brewery.entity;

import net.bmjo.brewery.block.brew_event.BrewEvent;
import net.bmjo.brewery.block.brew_event.BrewEvents;
import net.bmjo.brewery.block.multiblockparts.WaterBasinBlock;
import net.bmjo.brewery.registry.BlockEntityRegistry;
import net.bmjo.brewery.util.BreweryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class BrewKettleEntity extends BlockEntity implements BlockEntityTicker<BrewKettleEntity> {
    private static final int MAX_BREW_TIME = 30 * 20;
    private int brewTime;
    @Nullable
    private BrewEvent event; //TODO NBT
    private NonNullList<ItemStack> ingredients = NonNullList.create();
    @Nullable
    private Set<BlockPos> components;

    public BrewKettleEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.BREW_KETTLE_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public boolean isActive() {
        return this.components != null;
    }

    public List<ItemStack> getIngredients() {
        return this.ingredients;
    }

    public void addIngredient(ItemStack itemStack) {
        if (this.ingredients.size() >= 3) {
            return;
        }
        ItemStack ingredient = getStack(itemStack.getItem());
        if (ingredient != null) {
            int stackCount = itemStack.getCount();
            int count = ingredient.getCount() + stackCount;
            if (count > Item.MAX_STACK_SIZE) {
                ingredient.setCount(Item.MAX_STACK_SIZE);
                itemStack.shrink(stackCount - (count - Item.MAX_STACK_SIZE));
            } else {
                ingredient.setCount(count);
                itemStack.shrink(stackCount);
            }
        } else {
            this.ingredients.add(itemStack.copy());
            itemStack.setCount(0);
        }
    }

    @Nullable
    public ItemStack removeIngredient() {
        ItemStack itemStack = !this.ingredients.isEmpty() ? this.ingredients.get(0) : null;
        if (itemStack != null) {
            this.ingredients.remove(itemStack);
        }
        return itemStack;
    }

    @Nullable
    private ItemStack getStack(Item item) {
        for (ItemStack itemStack : this.ingredients) {
            if (itemStack.getItem() == item) {
                return itemStack;
            }
        }
        return null;
    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState blockState, BrewKettleEntity blockEntity) {
        if (!isActive()) return;
        if (!hasRecipe()) {
            if (this.event != null) this.endEvent();
            return;
        }
        if (this.event != null) {
            if (this.event.isFinish(this.components, level)) this.endEvent();
        } else if (brewTime >= MAX_BREW_TIME) {
            this.brew();
            return;
        } else if (brewTime % (5 * 20) == 0) {
            this.event = BrewEvents.BREW_EVENTS.get(RandomSource.create().nextInt(BrewEvents.BREW_EVENTS.size())).get();
            this.event.start(this.components, level);
        }
        this.brewTime++;
    }

    private boolean hasRecipe() {
        return true;
    }

    private void brew() {

    }

    private void endEvent() {
        if (this.event == null) return;
        this.event.finish(this.components, level);
        this.event = null;
    }

    @Nullable
    public BlockPos getController() {
        return level != null && this.components != null ? this.components.stream().filter(pos -> level.getBlockState(pos).getBlock() instanceof WaterBasinBlock).findFirst().orElse(null) : null;
    }

    public boolean isPartOf(BlockPos blockPos) {
        return this.components != null && this.components.contains(blockPos);
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        if (this.components != null) BreweryUtil.putBlockPos(compoundTag, this.components);
        ContainerHelper.saveAllItems(compoundTag, this.ingredients);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.ingredients = NonNullList.create();
        this.components = BreweryUtil.readBlockPos(compoundTag);
        ContainerHelper.loadAllItems(compoundTag, this.ingredients);
    }
}
