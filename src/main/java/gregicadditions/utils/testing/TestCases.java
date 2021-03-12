package gregicadditions.utils.testing;

import gregicadditions.materials.SimpleDustMaterial;
import gregicadditions.utils.GALog;
import gregicadditions.utils.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TestCases {

    /**
     * Compare the contents of the two Sets.
     *
     * @param <T> Either ItemStack or FluidStack
     * @return True if equal, false otherwise
     */
    protected static <T> boolean compareStackLists(Set<T> first, Set<T> second) {
        if (first == null && second == null)
            return true;

        if ((first == null || second == null) || first.size() != second.size())
            return false;

        return first.equals(second);
    }

    /**
     * Test if two stacks are identical.
     *
     * @param <T> Either ItemStack or FluidStack
     * @return True if identical, false otherwise
     * @throws IllegalArgumentException if either param is null, if types do not match,
     *                                  or if they are not ItemStack or FluidStack.
     */
    protected static <T> boolean compareStacks(T first, T second) {
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
     * Test two Recipes to see if they are duplicate.
     *
     * @return True if recipes are identical, false otherwise.
     */
    protected static boolean testMatching(HelperObjects.RecipeStruct recipe, HelperObjects.RecipeStruct oldRecipe) {

        return compareStackLists(recipe.inputs, oldRecipe.inputs)
                && compareStackLists(recipe.outputs, oldRecipe.outputs)
                && compareStackLists(recipe.fluidInputs, oldRecipe.fluidInputs)
                && compareStackLists(recipe.fluidOutputs, oldRecipe.fluidOutputs);
    }

    /**
     * Tests to see if the parameters are a subset of each other.
     *
     * @return A Tuple, where key is true if first parameter is a subset, and
     * value is true if the second parameter is a subset.
     */
    private static Tuple<Boolean, Boolean> conflict(HelperObjects.RecipeStruct recipe, HelperObjects.RecipeStruct oldRecipe) {
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
    protected static boolean testConflicting(HelperObjects.RecipeStruct recipe, HelperObjects.RecipeStruct oldRecipe) {
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

    /**
     * Test if an ItemStack has a valid mole count.
     * @param stack The stack to test.
     * @param atoms A data structure of the exploded chemical formula.
     * @return      True if moles are good, false otherwise.
     */
    protected static boolean testMoleCounts(ItemStack stack, Map<String, Integer> atoms) {
        int atomCount = atoms.values().stream().mapToInt(count -> count).sum();
        return atomCount == stack.getCount();
    }
}
