package net.satisfy.brewery.core.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.satisfy.brewery.core.compat.jei.category.BrewingStationCategory;
import net.satisfy.brewery.core.recipe.BrewingRecipe;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.brewery.core.registry.RecipeTypeRegistry;
import net.satisfy.brewery.core.util.BreweryIdentifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class BreweryJEIClientPlugin implements IModPlugin {
    public static RecipeType<BrewingRecipe> BREWING_TYPE = new RecipeType<>(BrewingStationCategory.UID, BrewingRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return BreweryIdentifier.identifier( "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new BrewingStationCategory(registration.getJeiHelpers().getGuiHelper()));

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<RecipeHolder<BrewingRecipe>> recipeHolders = rm.getAllRecipesFor(RecipeTypeRegistry.BREWING_RECIPE_TYPE.get());
        List<BrewingRecipe> recipesbrewing = new ArrayList<>();
        recipeHolders.forEach(recipeHolder -> {
            recipesbrewing.add(recipeHolder.value());
        });
        registration.addRecipes(BREWING_TYPE, recipesbrewing);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ObjectRegistry.WOODEN_BREWINGSTATION.get().asItem().getDefaultInstance(), BREWING_TYPE);
        registration.addRecipeCatalyst(ObjectRegistry.COPPER_BREWINGSTATION.get().asItem().getDefaultInstance(), BREWING_TYPE);
        registration.addRecipeCatalyst(ObjectRegistry.NETHERITE_BREWINGSTATION.get().asItem().getDefaultInstance(), BREWING_TYPE);
    }
}