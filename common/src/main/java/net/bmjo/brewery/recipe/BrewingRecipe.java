package net.bmjo.brewery.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.bmjo.brewery.registry.RecipeRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class BrewingRecipe implements Recipe<Container> {

    private final ResourceLocation identifier;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack output;

    public BrewingRecipe(ResourceLocation identifier, NonNullList<Ingredient> ingredients, ItemStack output) {
        this.identifier = identifier;
        this.ingredients = ingredients;
        this.output = output;
    }

    @Override
    public boolean matches(Container inventory, Level world) {
        StackedContents recipeMatcher = new StackedContents();
        int matchingStacks = 0;

        for (int i = 0; i < 3; ++i) {
            ItemStack itemStack = inventory.getItem(i);
            if (!itemStack.isEmpty()) {
                ++matchingStacks;
                recipeMatcher.accountStack(itemStack, 1);
            }
        }
        return matchingStacks == this.ingredients.size() && recipeMatcher.canCraft(this, null);
    }

    @Override
    public ItemStack assemble(Container inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }


    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return this.output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return this.identifier;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.BREWING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.BREWING_RECIPE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<BrewingRecipe> {

        @Override
        public BrewingRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            final var ingredients = deserializeIngredients(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for Brewing");
            } else if (ingredients.size() > 3) {
                throw new JsonParseException("Too many ingredients for Brewing");
            } else {
                return new BrewingRecipe(resourceLocation, ingredients, ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result")));
            }
        }

        public static NonNullList<Ingredient> deserializeIngredients(JsonArray json) {
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (int i = 0; i < json.size(); i++) {
                Ingredient ingredient = Ingredient.fromJson(json.get(i));
                if (!ingredient.isEmpty()) {
                    ingredients.add(ingredient);
                }
            }
            return ingredients;
        }

        @Override
        public BrewingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            final var ingredients = NonNullList.withSize(buf.readVarInt(), Ingredient.EMPTY);
            ingredients.replaceAll(ignored -> Ingredient.fromNetwork(buf));
            return new BrewingRecipe(id, ingredients, buf.readItem());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BrewingRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buf);
            }

            buf.writeItem(recipe.output);
        }
    }
}