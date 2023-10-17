package net.bmjo.brewery.entity;

import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.block.brew_event.BrewEvent;
import net.bmjo.brewery.block.brew_event.BrewEvents;
import net.bmjo.brewery.block.brew_event.BrewHelper;
import net.bmjo.brewery.block.property.Heat;
import net.bmjo.brewery.block.property.Liquid;
import net.bmjo.brewery.item.DrinkBlockItem;
import net.bmjo.brewery.registry.BlockEntityRegistry;
import net.bmjo.brewery.registry.BlockStateRegistry;
import net.bmjo.brewery.registry.ObjectRegistry;
import net.bmjo.brewery.registry.RecipeRegistry;
import net.bmjo.brewery.util.BreweryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
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
import java.util.function.Supplier;

public class BrewstationEntity extends BlockEntity implements Container, BlockEntityTicker<BrewstationEntity> {
    @NotNull
    private Set<BlockPos> components = new HashSet<>(4);
    private static final int MAX_BREW_TIME = 40 * 20;
    private int brewTime;
    @Nullable
    private BrewEvent event;
    private int solved;
    private NonNullList<ItemStack> ingredients;
    private ItemStack beer = ItemStack.EMPTY;

    public BrewstationEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.BREWINGSTATION_BLOCK_ENTITY.get(), blockPos, blockState);
        ingredients = NonNullList.withSize(3, ItemStack.EMPTY);
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

    public InteractionResult addIngredient(ItemStack itemStack) {
        for (int i = 0; i < 3; i++) {
            ItemStack stack = this.ingredients.get(i);
            if (stack.isEmpty()) {
                this.setItem(i, itemStack.getItem().getDefaultInstance());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    public ItemStack getBeer() {
        if (this.beer.isEmpty()) return null;
        ItemStack beerStack = this.beer.copy();
        beerStack.setCount(1);
        this.beer.shrink(1);
        if (this.beer.isEmpty() && this.level != null) {
            this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(BlockStateRegistry.LIQUID, Liquid.EMPTY));
        }
        return beerStack;
    }

    @Nullable
    public ItemStack removeIngredient() {
        for (int i = 0; i < 3; i++) {
            ItemStack itemStack = this.ingredients.get(i);
            if (!itemStack.isEmpty()) {
                this.ingredients.set(i, ItemStack.EMPTY);
                return itemStack;
            }
        }
        return null;
    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState blockState, BrewstationEntity blockEntity) {
        if (level.isClientSide) return;
        if (!this.beer.isEmpty()) return;
        Recipe<?> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.BREWING_RECIPE_TYPE.get(), this, level).orElse(null);
        if (!canBrew(recipe)) {
            if (this.event != null) this.endEvent();
            return;
        }
        if (this.event != null) {
            if (this.event.isFinish(this.components, level)) {
                this.endEvent();
                this.solved++;
            }
        } else {
            if (brewTime >= MAX_BREW_TIME) {
                this.brew(recipe);
                return;
            } else if (brewTime % (10 * 20) == 100) {
                this.event = getRdmEvent();
                this.event.start(this.components, level);
            }
            this.brewTime++;
        }
    }

    private boolean canBrew(Recipe<?> recipe) {
        return recipe != null && this.level != null &&
                this.level.getBlockState(this.getBlockPos()).getValue(BlockStateRegistry.LIQUID) != Liquid.EMPTY &&
                this.level.getBlockState(BrewHelper.getBlock(ObjectRegistry.BREW_OVEN.get(), this.components, this.level)).getValue(BlockStateRegistry.HEAT) != Heat.OFF;
    }

    private void brew(Recipe<?> recipe) {
        ItemStack resultSack = recipe.getResultItem();
        DrinkBlockItem.addQuality(resultSack, this.solved);
        if (resultSack.getItem() instanceof DrinkBlockItem drinkItem) {
            drinkItem.addCount(resultSack, this.solved);
        }
        this.beer = resultSack;
        if (this.level != null) {
            BlockState blockState = this.level.getBlockState(this.getBlockPos());
            this.level.setBlockAndUpdate(this.getBlockPos(), blockState.setValue(BlockStateRegistry.LIQUID, Liquid.BEER));
            BlockPos ovenPos = BrewHelper.getBlock(ObjectRegistry.BREW_OVEN.get(), this.components, level);
            BlockState ovenState = this.level.getBlockState(ovenPos);
            this.level.setBlockAndUpdate(ovenPos, ovenState.setValue(BlockStateRegistry.HEAT, Heat.OFF));
        }
        for (Ingredient ingredient : recipe.getIngredients()) {
            for (int i = 0; i < 3; i++) {
                ItemStack itemStack = this.ingredients.get(i);
                if (ingredient.test(itemStack)) {
                    this.removeItem(i, 1);
                    break;
                }
            }
        }
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

    public void updateInClientWorld() {
        if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().blockChanged(this.getBlockPos());
        }
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        if (!this.components.isEmpty()) BreweryUtil.putBlockPos(compoundTag, this.components);
        ContainerHelper.saveAllItems(compoundTag, this.ingredients);
        compoundTag.put("beer", this.beer.save(new CompoundTag()));
        if (this.event != null) compoundTag.putString("brew_event", this.event.id().toString());
    }

    @Override
    public void load(CompoundTag compoundTag) {
        this.components = BreweryUtil.readBlockPos(compoundTag);
        this.ingredients = NonNullList.withSize(3, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, this.ingredients);
        if (compoundTag.contains("beer")) this.beer = ItemStack.of(compoundTag.getCompound("beer"));
        if (compoundTag.contains("brew_event")) {
            Supplier<BrewEvent> brewEvent = BrewEvents.byId(new ResourceLocation(compoundTag.getString("brew_event")));
            this.event = brewEvent != null ? brewEvent.get() : null;
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
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
        assert this.level != null;
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
