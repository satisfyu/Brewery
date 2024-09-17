package net.satisfy.brewery.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.compat.jei.BreweryJEIClientPlugin;
import net.satisfy.brewery.recipe.BrewingRecipe;
import net.satisfy.brewery.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class BrewingStationCategory implements IRecipeCategory<BrewingRecipe> {
    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Brewery.MOD_ID, "brewing");
    public final static ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Brewery.MOD_ID, "textures/gui/brewingstation.png");

    private final IDrawable background;
    private final IDrawable icon;

    public BrewingStationCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ObjectRegistry.COPPER_BREWINGSTATION.get()));
    }

    @Override
    public @NotNull RecipeType<BrewingRecipe> getRecipeType() {
        return BreweryJEIClientPlugin.BREWING_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("rei.brewery.brewing_station_category");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BrewingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 50, 17).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 50, 35).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 50, 53).addIngredients(recipe.getIngredients().get(2));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 35).addItemStack(recipe.getResultItem(null));
    }
}