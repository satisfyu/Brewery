package net.satisfy.brewery.block.entity;

import de.cristelknight.doapi.common.registry.DoApiSoundEventRegistry;
import de.cristelknight.doapi.common.util.GeneralUtil;
import de.cristelknight.doapi.common.world.ImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.block.brew_event.BrewEvent;
import net.satisfy.brewery.block.brew_event.BrewEvents;
import net.satisfy.brewery.block.brew_event.BrewHelper;
import net.satisfy.brewery.block.property.BrewMaterial;
import net.satisfy.brewery.block.property.Heat;
import net.satisfy.brewery.block.property.Liquid;
import net.satisfy.brewery.entity.BeerElementalEntity;
import net.satisfy.brewery.item.DrinkBlockItem;
import net.satisfy.brewery.recipe.BrewingRecipe;
import net.satisfy.brewery.registry.*;
import net.satisfy.brewery.util.BreweryMath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BrewstationBlockEntity extends BlockEntity implements ImplementedInventory, BlockEntityTicker<BrewstationBlockEntity> {
    @NotNull
    private Set<BlockPos> components = new HashSet<>(4);
    private static final int MAX_BREW_TIME = 60 * 20;
    private static final int MIN_TIME_FOR_EVENT = 5 * 20;
    private static final int MAX_TIME_FOR_EVENT = 15 * 20;
    private static final int SOUND_DURATION = 3 * 20;
    private int soundTime;
    private int brewTime;
    private int timeToNextEvent = Integer.MIN_VALUE;
    private final Set<BrewEvent> runningEvents = new HashSet<>();
    private int solved;
    private int totalEvents;
    private NonNullList<ItemStack> ingredients;
    private ItemStack beer = ItemStack.EMPTY;
    private final SoundEvent spawnEntitySound = DoApiSoundEventRegistry.BREWSTATION_PROCESS_FAILED.get();

    public BrewstationBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.BREWINGSTATION_BLOCK_ENTITY.get(), blockPos, blockState);
        ingredients = NonNullList.withSize(3, ItemStack.EMPTY);
    }

    public void setComponents(BlockPos... components) {
        if (components.length != 4) {
            return;
        }
        this.components.addAll(Arrays.asList(components));
    }

    public InteractionResult addIngredient(ItemStack itemStack) {
        for (int i = 0; i < 3; i++) {
            ItemStack stack = this.ingredients.get(i);
            if (stack.isEmpty()) {
                this.setItem(i, itemStack.split(1));
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

        RecipeManager recipeManager = level.getRecipeManager();
        List<RecipeHolder<BrewingRecipe>> recipes = recipeManager.getAllRecipesFor(RecipeTypeRegistry.BREWING_RECIPE_TYPE.get());
        Optional<BrewingRecipe> recipe = Optional.ofNullable(getRecipe(recipes, ingredients));


        if (recipe.isEmpty() || !canBrew(recipe.get())) {
            endBrewing();
            return;
        }

        if (soundTime >= SOUND_DURATION) {
            assert this.level != null;
            level.playSound(null, blockPos, DoApiSoundEventRegistry.BREWSTATION_AMBIENT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            soundTime = 0;
        }
        soundTime++;
        if(timeToNextEvent == Integer.MIN_VALUE) setTimeToEvent();

        BrewHelper.checkRunningEvents(this);

        int timeLeft = MAX_BREW_TIME - brewTime;

        if (brewTime >= MAX_BREW_TIME) {
            RegistryAccess access = level.registryAccess();
            this.brew(recipe.get(), access);
        } else if (timeLeft >= MIN_TIME_FOR_EVENT && timeToNextEvent <= 0 && runningEvents.size() < BrewEvents.BREW_EVENTS.size()) {
            BrewEvent event = BrewHelper.getRdmEvent(this);
            if (event != null) {
                ResourceLocation eventId = BrewEvents.getId(event);
                if (eventId != null) {
                    event.start(this.components, level);
                    runningEvents.add(event);
                    totalEvents++;
                }
            }
            setTimeToEvent();
        }
        brewTime++;
        timeToNextEvent--;
    }

    private BrewingRecipe getRecipe(List<RecipeHolder<BrewingRecipe>> recipes, NonNullList<ItemStack> inventory) {
        recipeLoop:
        for (RecipeHolder<BrewingRecipe> recipeHolder : recipes) {
            BrewingRecipe recipe = recipeHolder.value();
            for (Ingredient ingredient : recipe.getIngredients()) {
                boolean ingredientFound = false;
                for (int slotIndex = 1; slotIndex < inventory.size(); slotIndex++) {
                    ItemStack slotItem = inventory.get(slotIndex);
                    if (ingredient.test(slotItem)) {
                        ingredientFound = true;
                        break;
                    }
                }
                if (!ingredientFound) {
                    continue recipeLoop;
                }
            }
            return recipe;
        }
        return null;
    }

    private void setTimeToEvent() {
        if (this.level != null) {
            timeToNextEvent = BreweryMath.getRandomHighNumber(this.level.getRandom(), MIN_TIME_FOR_EVENT, MAX_TIME_FOR_EVENT);
        }
    }

    private boolean canBrew(@Nullable Recipe<?> recipe) {
        if (recipe == null || this.level == null)
            return false;
        BlockState blockState = this.level.getBlockState(this.getBlockPos());
        return recipe instanceof BrewingRecipe brewingRecipe &&
                blockState.getValue(BlockStateRegistry.MATERIAL).getLevel() >= brewingRecipe.getMaterial().getLevel() &&
                blockState.getValue(BlockStateRegistry.LIQUID) != Liquid.EMPTY &&
                this.level.getBlockState(BrewHelper.getBlock(ObjectRegistry.BREW_OVEN.get(), this.components, this.level)).getValue(BlockStateRegistry.HEAT) != Heat.OFF;
    }

    private void brew(Recipe<?> recipe, RegistryAccess access) {
        ItemStack resultStack = recipe.getResultItem(access);
        if (resultStack.getItem() instanceof DrinkBlockItem drinkItem) {
            assert this.level != null;
            int quality = this.level.getBlockState(this.getBlockPos()).getValue(BlockStateRegistry.MATERIAL) == BrewMaterial.NETHERITE ? 3 : this.solved;
            DrinkBlockItem.addQuality(resultStack, quality);
            drinkItem.addCount(resultStack, this.solved == 0 ? 1 : this.solved);
        }
        this.beer = resultStack;
        spawnElementals();
        endBrewing();

        if (this.level != null) {
            BlockState blockState = this.level.getBlockState(this.getBlockPos());
            this.level.setBlockAndUpdate(this.getBlockPos(), blockState.setValue(BlockStateRegistry.LIQUID, Liquid.BEER));

            BlockPos ovenPos = BrewHelper.getBlock(ObjectRegistry.BREW_OVEN.get(), this.components, level);
            BlockState ovenState = this.level.getBlockState(ovenPos);
            this.level.setBlockAndUpdate(ovenPos, ovenState.setValue(BlockStateRegistry.HEAT, Heat.OFF));

            BlockPos timerPos = BrewHelper.getBlock(ObjectRegistry.BREW_TIMER.get(), this.components, level);
            BlockState timerState = this.level.getBlockState(timerPos);
            this.level.setBlockAndUpdate(timerPos, timerState.setValue(BlockStateRegistry.TIME, false));
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

    private void spawnElementals() {
        assert this.level != null;
        BlockState blockState = this.level.getBlockState(this.getBlockPos());
        if (this.solved == 0 && this.level != null && this.level.random.nextDouble() >= 0.1D && blockState.getValue(BlockStateRegistry.MATERIAL) == BrewMaterial.WOOD) {
            BlockPos spawnPos = BrewHelper.getBlock(ObjectRegistry.BREW_OVEN.get(), this.components, level);
            if (spawnPos != null) {
                BeerElementalEntity beerElemental = new BeerElementalEntity(EntityRegistry.BEER_ELEMENTAL.get(), this.level);
                beerElemental.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                this.level.addFreshEntity(beerElemental);

                this.level.playSound(null, spawnPos, spawnEntitySound, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    public void endBrewing() {
        BrewHelper.finishEvents(this);
        this.solved = 0;
        this.brewTime = 0;
        this.totalEvents = 0;
        this.soundTime = 3 * 20;
        this.timeToNextEvent = Integer.MIN_VALUE;
    }

    public boolean isPartOf(BlockPos blockPos) {
        return components.contains(blockPos);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        if(!this.components.isEmpty()) GeneralUtil.putBlockPoses(compoundTag, this.components);
        ContainerHelper.saveAllItems(compoundTag, this.ingredients, provider);
        if(!this.beer.isEmpty()) compoundTag.put("beer", this.beer.save(provider));
        compoundTag.putInt("solved", solved);
        compoundTag.putInt("brewTime", brewTime);
        compoundTag.putInt("totalEvents", totalEvents);
        compoundTag.putInt("timeToNextEvent", timeToNextEvent);
        BrewHelper.saveAdditional(this, compoundTag);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        this.components = GeneralUtil.readBlockPoses(compoundTag);
        this.ingredients = NonNullList.withSize(3, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, this.ingredients, provider);
        if (compoundTag.contains("beer")) this.beer = ItemStack.parse(provider, compoundTag.getCompound("beer")).get();
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
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag, provider);
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