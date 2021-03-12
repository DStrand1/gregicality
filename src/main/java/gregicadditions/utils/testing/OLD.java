package gregicadditions.utils.testing;

import gregicadditions.utils.GALog;
import gregicadditions.utils.Tuple;
import gregtech.api.recipes.CountableIngredient;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static gregicadditions.utils.testing.TestCases.compareStacks;

public class OLD {

    /**
     * Used to test Recipe Maps where there is 1 input and 1 output (Item)
     *
     * Ignores circuits, molds, shapes, etc..
     * @param map The Recipe Map to check
     */
    private static void testOneToOneMap(RecipeMap<?> map) {
        GALog.logger.info("Testing " + map.getLocalizedName() + " for duplicates...");

        Collection<Recipe> recipes = map.getRecipeList();
        List<Tuple<ItemStack, ItemStack>> checkedRecipes = new ArrayList<>();
        List<Tuple<ItemStack, ItemStack>> duplicates = new ArrayList<>();
        List<Recipe> errored = new ArrayList<>();

        for (Recipe recipe : recipes) {

            // We know that the first input will be the Item, not circuit
            CountableIngredient inputIng = recipe.getInputs().get(0);
            ItemStack[] inputArr = inputIng.getIngredient().getMatchingStacks();
            ItemStack input;
            if (inputArr.length == 0) {
                errored.add(recipe);
                continue;
            } else input = inputArr[0];

            ItemStack output;
            try {
                output = recipe.getOutputs().isEmpty() ? recipe.getChancedOutputs().get(0).getItemStack() : recipe.getOutputs().get(0);
            } catch (IndexOutOfBoundsException e) {
                errored.add(recipe);
                continue;
            }

            for (Tuple<ItemStack, ItemStack> oldRecipe : checkedRecipes) {
                if (compareStacks(input, oldRecipe.getKey()) && compareStacks(output, oldRecipe.getValue()))
                    duplicates.add(oldRecipe);
            }
            checkedRecipes.add(new Tuple<>(input, output));
        }
        for (Tuple<ItemStack, ItemStack> recipe : duplicates) {
            GALog.logger.error("Duplicate found,"
                    + " input: "  + recipe.getKey().getDisplayName()
                    + " output: " + recipe.getValue().getDisplayName());
        }
        if (!errored.isEmpty()) {
            GALog.logger.error("Recipes that could not be handled, check manually:");
            GALog.logger.error("Report this error if far too many recipes are listed");
        }

        for (Recipe recipe : errored)
            GALog.logger.error("Could not handle recipe with output: " + recipe.getOutputs().toString() + recipe.getChancedOutputs().toString());

        if (!duplicates.isEmpty())
            GALog.logger.error("Exiting...", new IllegalStateException());
        else
            GALog.logger.info("No recipe duplicates for " + map.getLocalizedName() + " found");
    }
}
