package net.bmjo.brewery.entity;

import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.block.brew_event.BrewEvent;
import net.bmjo.brewery.block.brew_event.BrewEvents;
import net.bmjo.brewery.item.DrinkBlockItem;
import net.bmjo.brewery.registry.BlockEntityRegistry;
import net.bmjo.brewery.registry.RecipeRegistry;
import net.bmjo.brewery.util.BreweryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BrewstationEntity extends BlockEntity implements Container, BlockEntityTicker<BrewstationEntity> {
    @NotNull
    private Set<BlockPos> components = new HashSet<>(4);
    private static final int MAX_BREW_TIME = 30 * 20;
    private int brewTime;
    @Nullable
    private BrewEvent event; //TODO NBT
    private int solved;
    private NonNullList<ItemStack> ingredients = NonNullList.create();
    @Nullable
    private ItemStack beer;

    public BrewstationEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.BREWINGSTATION_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public void setComponents(BlockPos... components) {
        if (components.length != 4) {
            Brewery.LOGGER.debug("Cant add components to BrewingStation. Should have 4 but only have {} parts.", components.length);
            return;
        }
        this.components.addAll(Arrays.asList(components));
    }

    public @NotNull Set<BlockPos> getComponents() {
        return components;
    }

    public List<ItemStack> getIngredient() {
        return this.ingredients;
    }

    public void addIngredient(ItemStack itemStack) {
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
    private ItemStack getStack(Item item) {
        for (ItemStack itemStack : this.ingredients) {
            if (itemStack.getItem() == item) {
                return itemStack;
            }
        }
        return null;
    }

    @Nullable
    public ItemStack removeIngredient() {
        ItemStack itemStack = this.ingredients.isEmpty() ? null : this.ingredients.iterator().next();
        if (itemStack != null) {
            this.ingredients.remove(itemStack);
        }
        return itemStack;
    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState blockState, BrewstationEntity blockEntity) {
        if (level.isClientSide) return;
        Recipe<?> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.BREWING_RECIPE_TYPE.get(), this, level).orElse(null);
        if (recipe == null) {
            if (this.event != null) this.endEvent();
            return;
        }
        if (this.event != null) {
            if (this.event.isFinish(this.components, level)) {
                this.endEvent();
                this.solved++;
            }
        } else if (brewTime >= MAX_BREW_TIME) {
            this.brew(recipe);
            return;
        } else if (brewTime % (5 * 20) == 0) {
            this.event = getRdmEvent();
            this.event.start(this.components, level);
        }
        this.brewTime++;
    }

    private void brew(Recipe<?> recipe) {
        ItemStack resultSack = recipe.getResultItem();
        DrinkBlockItem.addQuality(resultSack, this.solved);
        if (resultSack.getItem() instanceof DrinkBlockItem drinkStack) {
            drinkStack.addCount(resultSack, this.solved);
        }
        this.beer = resultSack;
        this.solved = 0;
        this.brewTime = 0;
    }

    private void endEvent() {
        if (this.event == null) return;
        this.event.finish(this.components, level);
        this.event = null;
    }

    private BrewEvent getRdmEvent() {
        return BrewEvents.BREW_EVENTS.get(RandomSource.create().nextInt(BrewEvents.BREW_EVENTS.size())).get();
    }

    public boolean isPartOf(BlockPos blockPos) {
        return components.contains(blockPos);
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        if (!this.components.isEmpty()) BreweryUtil.putBlockPos(compoundTag, this.components);
        ContainerHelper.saveAllItems(compoundTag, this.ingredients);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.ingredients = NonNullList.create();
        this.components = BreweryUtil.readBlockPos(compoundTag);
        ContainerHelper.loadAllItems(compoundTag, this.ingredients);
    }

    //CONTAINER
    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    public boolean isEmpty() {
        return this.ingredients.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.ingredients.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.ingredients, slot, amount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.ingredients, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.ingredients.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double) this.worldPosition.getX() + 0.5, (double) this.worldPosition.getY() + 0.5, (double) this.worldPosition.getZ() + 0.5) <= 64.0;
        }
    }

    @Override
    public void clearContent() {
        this.ingredients.clear();
    }
}
