package net.bmjo.brewery.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.recipe.BrewingRecipe;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public class RecipeRegistry {

    private static final Registrar<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Brewery.MOD_ID, Registry.RECIPE_TYPE_REGISTRY).getRegistrar();
    private static final Registrar<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Brewery.MOD_ID, Registry.RECIPE_SERIALIZER_REGISTRY).getRegistrar();

    public static final RegistrySupplier<RecipeType<BrewingRecipe>> BREWING_RECIPE_TYPE = create("brew");
    public static final RegistrySupplier<RecipeSerializer<BrewingRecipe>> BREWING_RECIPE_SERIALIZER = create("brewing", BrewingRecipe.Serializer::new);

    private static <T extends Recipe<?>> RegistrySupplier<RecipeType<T>> create(String name) {
        Supplier<RecipeType<T>> type = () -> new RecipeType<>() {
            @Override
            public String toString() {
                return name;
            }
        };
        return RECIPE_TYPES.register(new BreweryIdentifier(name), type);
    }

    private static <T extends Recipe<?>> RegistrySupplier<RecipeSerializer<T>> create(String name, Supplier<RecipeSerializer<T>> serializer) {
        return RECIPE_SERIALIZERS.register(new BreweryIdentifier(name), serializer);
    }

    public static void registerRecipes() {
    }
}