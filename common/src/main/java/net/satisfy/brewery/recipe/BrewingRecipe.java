package net.satisfy.brewery.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.satisfy.brewery.block.property.BrewMaterial;
import net.satisfy.brewery.registry.RecipeTypeRegistry;
import net.satisfy.brewery.util.rope.StreamCodecUtil;
import org.jetbrains.annotations.NotNull;

public class BrewingRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack output;
    private final BrewMaterial material;

    public BrewingRecipe(NonNullList<Ingredient> ingredients, ItemStack output, String material) {
        this.ingredients = ingredients;
        this.output = output;
        this.material = setMaterial(material);
    }

    public BrewMaterial getMaterial() {
        return material;
    }

    public String getMaterialName() {
        return material.getSerializedName();
    }

    public BrewMaterial setMaterial(String material) {
        return BrewMaterial.valueOf(material.toUpperCase());
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }


    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        StackedContents recipeMatcher = new StackedContents();
        int matchingStacks = 0;

        for (int i = 0; i < 3; ++i) {
            ItemStack itemStack = recipeInput.getItem(i);
            if (!itemStack.isEmpty()) {
                ++matchingStacks;
                recipeMatcher.accountStack(itemStack, 1);
            }
        }
        return matchingStacks == this.ingredients.size() && recipeMatcher.canCraft(this, null);
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.output.copy();
    }

    public ItemStack result() {
        return getResultItem(null);
    }

    public @NotNull ResourceLocation getId() {
        return RecipeTypeRegistry.BREWING_RECIPE_TYPE.getId();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegistry.BREWING_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypeRegistry.BREWING_RECIPE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<BrewingRecipe> {
        public static final MapCodec<BrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(list -> {
                            Ingredient[] ingredients = list.toArray(Ingredient[]::new);
                            if (ingredients.length == 0) {
                                return DataResult.error(() -> "No ingredients for shapeless recipe");
                            }
                            return DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                        }, DataResult::success).forGetter(BrewingRecipe::getIngredients),
                        ItemStack.CODEC.fieldOf("result").forGetter(brewingRecipe -> brewingRecipe.output),
                Codec.STRING.fieldOf("material").forGetter(brewingRecipe -> brewingRecipe.material.name())
                ).apply(instance, BrewingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> STREAM_CODEC = StreamCodec.composite(
                StreamCodecUtil.nonNullList(Ingredient.CONTENTS_STREAM_CODEC, Ingredient.EMPTY), BrewingRecipe::getIngredients,
                ItemStack.STREAM_CODEC, BrewingRecipe::result,
                ByteBufCodecs.STRING_UTF8, BrewingRecipe::getMaterialName,
                BrewingRecipe::new
        );

        @Override
        public @NotNull MapCodec<BrewingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}