package gregicadditions.utils.testing;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HelperObjects {

    /**
     * A simple data structure to contain two Lists.
     * Used for error handling in test cases.
     */
    protected static class RecipeList {
        public List<RecipeStruct> duplicates;
        public List<RecipeStruct> errored;

        public RecipeList(List<RecipeStruct> duplicates, List<RecipeStruct> errored) {
            this.duplicates = duplicates;
            this.errored = errored;
        }
    }

    /**
     * A simple data structure to help keep all of the recipe
     * lists organized in a sane and efficient way.
     */
    protected static class RecipeStruct {

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

        /**
         * @return Recipe input and output lists.
         */
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

        /**
         * A more verbose version of toString().
         *
         * @return toString(), plus fluid inputs and outputs.
         */
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
}
