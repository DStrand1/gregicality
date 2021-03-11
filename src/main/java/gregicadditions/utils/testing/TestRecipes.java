package gregicadditions.utils.testing;

import gregicadditions.utils.GALog;
import gregicadditions.utils.Tuple;
import gregtech.api.recipes.*;
import gregtech.api.util.ItemStackHashStrategy;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TestRecipes {

    /**
     * Hashing strategy used for Maps where the key is an ItemStack.
     */
    private static final Hash.Strategy<ItemStack> strategy =
            ItemStackHashStrategy.builder()
                    .compareCount(true)
                    .compareDamage(true)
                    .compareItem(true)
                    .build();

    /**
     * Test for duplicate recipes in Recipe Maps.
     *
     * Recommended to run only one RecipeMap test at a time, as it
     * is a large amount of data running in O(N^2) time at best.
     */
    public static void testRecipes() {
        GALog.logger.info("Testing recipes for duplicates...");
        testOneToOne();
        testManyToMany();
    }

    /**
     * Test all Recipe Maps that have exactly 1 input and 1 output (Item).
     *
     * Ignores circuits, molds, shapes, etc..
     */
    public static void testOneToOne() {
        // testOneToOneMap(RecipeMaps.BENDER_RECIPES);
        // testOneToOneMap(RecipeMaps.EXTRUDER_RECIPES);
        // testOneToOneMap(RecipeMaps.FURNACE_RECIPES);
        // testOneToOneMap(RecipeMaps.COMPRESSOR_RECIPES);
        // testOneToOneMap(RecipeMaps.FORGE_HAMMER_RECIPES); // TODO ERRORING FOR ALL
        // testOneToOneMap(RecipeMaps.FORMING_PRESS_RECIPES);
        // testOneToOneMap(RecipeMaps.LATHE_RECIPES);
        // testOneToOneMap(RecipeMaps.POLARIZER_RECIPES);
        // testOneToOneMap(RecipeMaps.LASER_ENGRAVER_RECIPES);
    }

    public static void testManyToMany() {

    }

    /**
     * Used to test Recipe Maps where there is 1 input and 1 output (Item)
     *
     * Ignores circuits, molds, shapes, etc..
     * @param map The Recipe Map to check
     */
    public static void testOneToOneMap(RecipeMap<?> map) {
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

    /**
     * Test a RecipeMap with more than just one input and one output.
     *
     * @param map The RecipeMap to test.
     */
    public static void testManyToManyMap(RecipeMap<?> map) {
        testManyToManyMap(map, false, false);
    }

    /**
     * Test a RecipeMap with more than just one input and one output.
     *
     * @param map      The RecipeMap to test.
     * @param detailed Show more detailed logging info.
     * @param exit     Throw an exception upon finding a duplicate.
     *                 Will collect all duplicates for the map before exiting.
     *
     * @return         A instance of {@link RecipeList} containing two Lists, first being
     *                 duplicate recipes, second being errored recipes.
     */
    public static RecipeList testManyToManyMap(RecipeMap<?> map, boolean detailed, boolean exit) {
        GALog.logger.info("Testing " + map.getLocalizedName() + " for duplicates...");

        Collection<Recipe> recipes = map.getRecipeList();

        List<RecipeStruct> checkedRecipes = new ArrayList<>();
        List<RecipeStruct> duplicates = new ArrayList<>();
        List<RecipeStruct> errored = new ArrayList<>();
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

            RecipeStruct currentRecipe = new RecipeStruct(inputs, fluidInputs, outputs, fluidOutputs);

            if (hasErrored.getAndSet(false)) {
                errored.add(currentRecipe);
                continue;
            }

            for (RecipeStruct oldRecipe : checkedRecipes) {
                if (compareStackLists(inputs, oldRecipe.inputs)
                 && compareStackLists(outputs, oldRecipe.outputs)
                 && compareStackLists(fluidInputs, oldRecipe.fluidInputs)
                 && compareStackLists(fluidOutputs, oldRecipe.fluidOutputs)) {

                    duplicates.add(currentRecipe);
                    hasErrored.set(true);
                    break;
                }
            }
            if (hasErrored.getAndSet(false))
                checkedRecipes.add(currentRecipe);
        }

        for (RecipeStruct recipe : duplicates) {
            GALog.logger.error("Duplicate found, "
                    + (detailed ? recipe.toStringDetailed() : recipe.toString()));
        }
        if (!errored.isEmpty()) {
            GALog.logger.error("Recipes that could not be handled, check manually:");
            GALog.logger.error("Report this error if far too many recipes are listed");
        }
        for (RecipeStruct recipe : errored)
            GALog.logger.error("Could not handle recipe with output: "
                    + (detailed ? recipe.toStringDetailed() : recipe.toString()));

        if (!duplicates.isEmpty())
            if (exit)
                GALog.logger.error("Exiting...", new IllegalStateException());
        else
            GALog.logger.info("No recipe duplicates for " + map.getLocalizedName() + " found");
    }

    /**
     * Compare the contents of the two Sets.
     * @param <T> Either ItemStack or FluidStack
     * @return True if equal, false otherwise
     */
    private static <T> boolean compareStackLists(Set<T> first, Set<T> second) {
        if (first == null && second == null)
            return true;

        if ((first == null || second == null) || first.size() != second.size())
            return false;

        return first.equals(second);
    }

    public static <T> boolean compareStacks(T first, T second) {
        if (first == null || second == null)
            throw new IllegalArgumentException("Parameters cannot be null");

        if (!first.getClass().equals(second.getClass()))
            throw new IllegalArgumentException("Parameter types do not match");

        if (first instanceof ItemStack) {
            ItemStack input = (ItemStack) first;
            ItemStack output = (ItemStack) second;

            return ItemStack.areItemStacksEqual(input, output)
                    && ItemStack.areItemStackTagsEqual(input, output);

        } else if (first instanceof FluidStack) {
            FluidStack input = (FluidStack) first;
            FluidStack output = (FluidStack) second;

            return input.equals(output) && output.equals(input);

        } else throw new IllegalArgumentException("Passed values are of illegal type");
    }

    /**
     * A simple data structure to help keep all of the recipe
     * lists organized in a sane and efficient way.
     */
    private static class RecipeStruct {

        public Set<ItemStack> inputs;
        public Set<FluidStack> fluidInputs;
        public Set<ItemStack> outputs;
        public Set<FluidStack> fluidOutputs;

        public RecipeStruct(Set<ItemStack> inputs,
                            Set<FluidStack> fluidInputs,
                            Set<ItemStack> outputs,
                            Set<FluidStack> fluidOutputs) {
            this.inputs = inputs;
            this.fluidInputs = fluidInputs;
            this.outputs = outputs;
            this.fluidOutputs = fluidOutputs;
        }

        public String toString() {
            return "Inputs: " +
                    inputs.stream()
                          .map(ItemStack::getDisplayName)
                          .collect(Collectors.toList()) +
                    System.lineSeparator() +
                    "Outputs: " +
                    outputs.stream()
                           .map(ItemStack::getDisplayName)
                           .collect(Collectors.toList());
        }

        public String toStringDetailed() {

            return this.toString() +
                    System.lineSeparator() +
                    "Fluid inputs: " +
                    fluidOutputs.stream()
                                .map(FluidStack::getLocalizedName)
                                .collect(Collectors.toList()) +
                    System.lineSeparator() +
                    "Fluid Outputs: " +
                    fluidOutputs.stream()
                                .map(FluidStack::getLocalizedName)
                                .collect(Collectors.toList());
        }
    }

    private static class RecipeList {
        public List<RecipeStruct> duplicates;
        public List<RecipeStruct> errored;

        public RecipeList(List<RecipeStruct> duplicates, List<RecipeStruct> errored) {
            this.duplicates = duplicates;
            this.errored = errored;
        }
    }

    private static class TestCases {

        /**
         * Test two Recipes to see if they are duplicate.
         *
         * @return True if recipes are identical, false otherwise.
         */
        public static boolean testMatching(RecipeStruct recipe, RecipeStruct oldRecipe) {

            return compareStackLists(recipe.inputs, oldRecipe.inputs)
                && compareStackLists(recipe.outputs, oldRecipe.outputs)
                && compareStackLists(recipe.fluidInputs, oldRecipe.fluidInputs)
                && compareStackLists(recipe.fluidOutputs, oldRecipe.fluidOutputs);
        }

        /**
         * Tests to see if the parameters are a subset of each other.
         *
         * @return A Tuple, where key is true if first parameter is a subset, and
         *         value is true if the second parameter is a subset.
         */
        private static Tuple<Boolean, Boolean> conflict(RecipeStruct recipe, RecipeStruct oldRecipe) {
            boolean newIsSubset = recipe.inputs.containsAll(oldRecipe.inputs)
                               && recipe.fluidInputs.containsAll(oldRecipe.fluidInputs); // TODO Is this good?

            boolean oldIsSubset = oldRecipe.inputs.containsAll(recipe.inputs)
                               && oldRecipe.fluidInputs.containsAll(recipe.fluidInputs);

            return new Tuple<>(newIsSubset, oldIsSubset);
        }

        /**
         * Test if two recipes are a subset of each other.
         *
         * @return True if either recipe is a subset, false otherwise.
         */
        public static boolean testConflicting(RecipeStruct recipe, RecipeStruct oldRecipe) {
            Tuple<Boolean, Boolean> subsets = conflict(recipe, oldRecipe);

            // TODO
            if (subsets.getKey()) {
                return true;
            } else if (subsets.getValue()) {
                return true;
            } else {
                return false;
            }
        }
    }
}
