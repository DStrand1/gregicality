package gregicadditions.utils.testing;

import gregicadditions.utils.GALog;
import gregtech.api.recipes.CountableIngredient;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TestRecipes extends TestBase {

    /**
     * Test for duplicate recipes in Recipe Maps.
     *
     * Recommended to run only one RecipeMap test at a time, as it
     * is a large amount of data running in O(N^2) time at best.
     */
    public static void testRecipes() {
        GALog.logger.info("Testing recipes for duplicates...");
        testManyToMany();
    }

    public static void testManyToMany() {
    }

    /**
     * Test a RecipeMap for duplicate recipes.
     *
     * @param map The RecipeMap to test.
     */
    public static void testDuplicates(RecipeMap<?> map) {
        testDuplicates(map, false, false);
    }

    /**
     * Test a RecipeMap for duplicate recipes.
     *
     * @param map      The RecipeMap to test.
     * @param detailed Show more verbose logging.
     * @param exit     Throw an exception after finding duplicates.
     */
    public static void testDuplicates(RecipeMap<?> map, boolean detailed, boolean exit) {
        try {
            Method testMethod = TestCases.class.getDeclaredMethod("testMatching", HelperObjects.RecipeStruct.class, HelperObjects.RecipeStruct.class);
            Method loggingMethod = TestLogging.class.getDeclaredMethod("simpleLoggingDupe", HelperObjects.RecipeList.class, String.class, boolean.class, boolean.class);
            testManyToManyMap(map, testMethod, loggingMethod, detailed, exit);
        } catch (NoSuchMethodException e) {
            GALog.logger.error("Failed to reflect test case in method declaration...", e);
        }
    }

    /**
     * Test a RecipeMap for conflicting recipes.
     *
     * @param map The RecipeMap to test.
     */
    public static void testConflicts(RecipeMap<?> map) {
        testConflicts(map, false, false);
    }

    /**
     * Test a RecipeMap for conflicting recipes.
     *
     * @param map      The RecipeMap to test.
     * @param detailed Show more verbose logging.
     * @param exit     Throw an exception after finding conflicts.
     */
    public static void testConflicts(RecipeMap<?> map, boolean detailed, boolean exit) {
        try {
            Method testMethod = TestCases.class.getDeclaredMethod("testConflicting", HelperObjects.RecipeStruct.class, HelperObjects.RecipeStruct.class);
            Method loggingMethod = TestLogging.class.getDeclaredMethod("simpleLoggingConflict", HelperObjects.RecipeList.class, String.class, boolean.class, boolean.class);
            testManyToManyMap(map, testMethod, loggingMethod, detailed, exit);
        } catch (NoSuchMethodException e) {
            GALog.logger.error("Failed to reflect test case in method declaration...", e);
        }
    }

    /**
     * Test a RecipeMap with more than just one input and one output.
     *
     * @param map      The RecipeMap to test.
     * @param detailed Show more detailed logging info.
     * @param exit     Throw an exception upon finding a duplicate.
     *                 Will collect all duplicates for the map before exiting.
     *
     * @return         A instance of {@link HelperObjects.RecipeList} containing two Lists, first being
     *                 duplicate recipes, second being errored recipes.
     */
    private static HelperObjects.RecipeList testManyToManyMap(RecipeMap<?> map,
                                                              Method testMethod,
                                                              Method loggingMethod,
                                                              boolean detailed,
                                                              boolean exit) {
        GALog.logger.info("Testing " + map.getLocalizedName() + " for duplicates...");

        Collection<Recipe> recipes = map.getRecipeList();

        List<HelperObjects.RecipeStruct> checkedRecipes = new ArrayList<>();
        List<HelperObjects.RecipeStruct> duplicates = new ArrayList<>();
        List<HelperObjects.RecipeStruct> errored = new ArrayList<>();
        AtomicBoolean hasErrored = new AtomicBoolean(false);

        for (Recipe recipe : recipes) {
            Set<ItemStack> inputs = new ObjectOpenCustomHashSet<>(strategy);
            inputs.addAll(recipe.getInputs().stream()
                    .map(CountableIngredient::getIngredient)
                    .map(Ingredient::getMatchingStacks)
                    .filter(stack -> {
                        if (stack.length == 0)
                            hasErrored.set(true);
                        return true;
                    })
                    .map(array -> array[0])
                    .collect(Collectors.toSet()));

            Set<FluidStack> fluidInputs = new HashSet<>(recipe.getFluidInputs());
            Set<ItemStack> outputs = new ObjectOpenCustomHashSet<>(strategy);
            outputs.addAll(recipe.getOutputs());
            recipe.getChancedOutputs().forEach(entry -> outputs.add(entry.getItemStack()));
            Set<FluidStack> fluidOutputs = new HashSet<>(recipe.getFluidOutputs());

            HelperObjects.RecipeStruct currentRecipe = new HelperObjects.RecipeStruct(inputs, fluidInputs, outputs, fluidOutputs);

            if (hasErrored.getAndSet(false)) {
                errored.add(currentRecipe);
                continue;
            }

            for (HelperObjects.RecipeStruct oldRecipe : checkedRecipes) {
                try {
                    Boolean result = (Boolean) testMethod.invoke(null, oldRecipe, currentRecipe);
                    if (result) {
                        duplicates.add(currentRecipe);
                        hasErrored.set(true);
                        break;
                    }
                } catch (Exception e) {

                    // This should never be hit
                    GALog.logger.error("Failed to reflect test case...", e);
                }
            }
            if (hasErrored.getAndSet(false))
                checkedRecipes.add(currentRecipe);
        }

        HelperObjects.RecipeList list = new HelperObjects.RecipeList(duplicates, errored);

        try {
            loggingMethod.invoke(null, list, map.getLocalizedName(), detailed, exit);
        } catch (Exception e) {
            GALog.logger.error("Failed to reflect logging method...", e);
        }

        return list;
    }
}
