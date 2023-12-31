package net.satisfy.brewery.entity;

import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.block.brew_event.BrewEvent;
import net.satisfy.brewery.block.brew_event.BrewEvents;
import net.satisfy.brewery.block.brew_event.BrewHelper;
import net.satisfy.brewery.block.property.Heat;
import net.satisfy.brewery.block.property.Liquid;
import net.satisfy.brewery.item.DrinkBlockItem;
import net.satisfy.brewery.registry.BlockEntityRegistry;
import net.satisfy.brewery.registry.BlockStateRegistry;
import net.satisfy.brewery.registry.ObjectRegistry;
import net.satisfy.brewery.registry.RecipeRegistry;
import net.satisfy.brewery.util.BreweryMath;
import net.satisfy.brewery.util.BreweryUtil;
import net.satisfy.brewery.util.ImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
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

import java.util.*;

public class BrewstationBlockEntity extends BlockEntity implements ImplementedInventory, BlockEntityTicker<BrewstationBlockEntity> {
    @NotNull
    private Set<BlockPos> components = new HashSet<>(4);
    private static final int MAX_BREW_TIME = 60 * 20;
    private static final int MIN_TIME_FOR_EVENT = 5 * 20;
    private static final int MAX_TIME_FOR_EVENT = 15 * 20;
    private int brewTime;
    private int timeToNextEvent = Integer.MIN_VALUE;
    private final Set<BrewEvent> runningEvents = new HashSet<>();
    private int solved;
    private int totalEvents;
    private NonNullList<ItemStack> ingredients;
    private ItemStack beer = ItemStack.EMPTY;

    public BrewstationBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.BREWINGSTATION_BLOCK_ENTITY.get(), blockPos, blockState);
        ingredients = NonNullList.withSize(3, ItemStack.EMPTY);
    }

    public void updateInClientWorld() {
        if (this.level instanceof ServerLevel serverLevel)
            serverLevel.getChunkSource().blockChanged(this.getBlockPos());
    }

    public void setComponents(BlockPos... components) {
        if (components.length != 4) {
            Brewery.LOGGER.debug("Cant add components to BrewingStation. Should have 4 but only have {} parts.", components.length);
            return;
        }
        this.components.addAll(Arrays.asList(components));
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
    public void tick(Level level, BlockPos blockPos, BlockState blockState, BrewstationBlockEntity blockEntity) {
        if (level.isClientSide) return;
        if (!this.beer.isEmpty()) return;
        Recipe<?> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.BREWING_RECIPE_TYPE.get(), this, level).orElse(null);
        if (!canBrew(recipe)) {
            endBrewing();
            return;
        }
        if(timeToNextEvent == Integer.MIN_VALUE) setTimeToEvent();

        BrewHelper.checkRunningEvents(this);

        int timeLeft = MAX_BREW_TIME - brewTime;

        if (brewTime >= MAX_BREW_TIME) {
            this.brew(recipe);
            return;
        } else if (timeLeft >= MIN_TIME_FOR_EVENT && timeToNextEvent <= 0 && runningEvents.size() < BrewEvents.BREW_EVENTS.size()) {
            BrewEvent event = BrewHelper.getRdmEvent(this);
            Brewery.LOGGER.warn("Starting event!" + BrewEvents.getId(event).getPath());
            event.start(this.components, level);
            runningEvents.add(event);
            totalEvents++;
            setTimeToEvent();
        }
        brewTime++;
        timeToNextEvent--;
    }

    private void setTimeToEvent(){
        timeToNextEvent = BreweryMath.getRandomHighNumber(this.getLevel().getRandom(), MIN_TIME_FOR_EVENT, MAX_TIME_FOR_EVENT);
    }

    private boolean canBrew(Recipe<?> recipe) {
        return recipe != null && this.level != null &&
                this.level.getBlockState(this.getBlockPos()).getValue(BlockStateRegistry.LIQUID) != Liquid.EMPTY &&
                this.level.getBlockState(BrewHelper.getBlock(ObjectRegistry.BREW_OVEN.get(), this.components, this.level)).getValue(BlockStateRegistry.HEAT) != Heat.OFF;
    }

    private void brew(Recipe<?> recipe) {
        Brewery.LOGGER.warn("Brewing!!!");

        ItemStack resultSack = recipe.getResultItem();
        DrinkBlockItem.addQuality(resultSack, this.solved);
        if (resultSack.getItem() instanceof DrinkBlockItem drinkItem) {
            drinkItem.addCount(resultSack, this.solved);
        }
        this.beer = resultSack;

        endBrewing();

        if (this.level != null) {
            BlockState blockState = this.level.getBlockState(this.getBlockPos());
            this.level.setBlockAndUpdate(this.getBlockPos(), blockState.setValue(BlockStateRegistry.LIQUID, Liquid.BEER));

            //Todo:
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

    }

    public void endBrewing(){
        BrewHelper.finishEvents(this);
        this.solved = 0;
        this.brewTime = 0;
        this.totalEvents = 0;
        this.timeToNextEvent = Integer.MIN_VALUE;
    }


    public boolean isPartOf(BlockPos blockPos) {
        return components.contains(blockPos);
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        if (!this.components.isEmpty()) BreweryUtil.putBlockPoses(compoundTag, this.components);
        ContainerHelper.saveAllItems(compoundTag, this.ingredients);
        compoundTag.put("beer", this.beer.save(new CompoundTag()));
        compoundTag.putInt("solved", solved);
        compoundTag.putInt("brewTime", brewTime);
        compoundTag.putInt("totalEvents", totalEvents);
        compoundTag.putInt("timeToNextEvent", timeToNextEvent);
        BrewHelper.saveAdditional(this, compoundTag);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        this.components = BreweryUtil.readBlockPoses(compoundTag);
        this.ingredients = NonNullList.withSize(3, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, this.ingredients);
        if (compoundTag.contains("beer")) this.beer = ItemStack.of(compoundTag.getCompound("beer"));
        this.solved = compoundTag.getInt("solved");
        this.brewTime = compoundTag.getInt("brewTime");
        this.totalEvents = compoundTag.getInt("totalEvents");
        this.timeToNextEvent = compoundTag.getInt("timeToNextEvent");
        BrewHelper.load(this, compoundTag);
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

    public void growSolved() {
        this.solved++;
    }

    public Set<BrewEvent> getRunningEvents() {
        return runningEvents;
    }

    public @NotNull Set<BlockPos> getComponents() {
        return components;
    }

    public List<ItemStack> getIngredient() {
        return this.ingredients;
    }


    //CONTAINER
    @Override
    public NonNullList<ItemStack> getItems() {
        return ingredients;
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
}
