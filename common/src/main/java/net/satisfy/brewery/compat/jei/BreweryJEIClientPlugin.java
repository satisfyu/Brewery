package net.satisfy.brewery.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.compat.jei.category.BrewingStationCategory;
import net.satisfy.brewery.recipe.BrewingRecipe;
import net.satisfy.brewery.registry.RecipeTypeRegistry;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class BreweryJEIClientPlugin implements IModPlugin {
    public static RecipeType<BrewingRecipe> BREWING_TYPE = new RecipeType<>(BrewingStationCategory.UID, BrewingRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Brewery.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new BrewingStationCategory(registration.getJeiHelpers().getGuiHelper()));

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<RecipeHolder<BrewingRecipe>> recipesbrewing = rm.getAllRecipesFor(RecipeTypeRegistry.BREWING_RECIPE_TYPE.get());
        List<BrewingRecipe> recipes = Lists.newArrayList();
        for (RecipeHolder<BrewingRecipe> recipe : recipesbrewing) {
            recipes.add(recipe.value());
        }
        registration.addRecipes(BREWING_TYPE, recipes);
    }
}