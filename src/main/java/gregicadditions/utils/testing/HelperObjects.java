package gregicadditions.utils.testing;

import gregicadditions.materials.SimpleDustMaterial;
import gregicadditions.materials.SimpleFluidMaterial;
import gregtech.api.unification.material.type.FluidMaterial;
import gregtech.api.unification.material.type.Material;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static gregicadditions.utils.testing.TestCases.testMoleCounts;

public class HelperObjects {

    /**
     * A simple data structure to contain two Lists.
     * Used for error handling in test cases.
     */
    protected static class RecipeList {
        public List<RecipeStruct> failed;
        public List<RecipeStruct> errored;

        public RecipeList(List<RecipeStruct> failed, List<RecipeStruct> errored) {
            this.failed = failed;
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

    // TODO Make sure to throw out notConsumed inputs and chance outputs
    protected static class FormulaVerifier {

        private final RecipeStruct recipe;

        private final Map<ItemStack, Map<String, Integer>> inputItems;
        private final Map<ItemStack, Map<String, Integer>> outputItems;
        private final Map<FluidStack, Map<String, Integer>> inputFluids;
        private final Map<FluidStack, Map<String, Integer>> outputFluids;

        /**
         * A simple helper Object used to verify the balance of recipes.
         *
         * @param recipe   The recipe to verify.
         * @param doFluids If true, generate fluid data sets, else do not.
         *                 Intended to save on resources if mole counts of dusts are being tested.
         */
        public FormulaVerifier(RecipeStruct recipe, boolean doFluids) {
            this.recipe = recipe;
            this.inputItems = new Object2ObjectOpenCustomHashMap<>(TestBase.strategy);
            this.outputItems = new Object2ObjectOpenCustomHashMap<>(TestBase.strategy);
            this.inputFluids = new HashMap<>();
            this.outputFluids = new HashMap<>();
            buildChemicals(doFluids);
        }

        /**
         * Used to verify the mole count of dusts present in formulas.
         *
         * @param which If true, verify inputs. If false, verify outputs.
         * @return      True if moles are good, false otherwise.
         */
        public boolean verifyMoleCounts(boolean which) {
            AtomicBoolean b = new AtomicBoolean(false);

            Map<ItemStack, Map<String, Integer>> mapToUse;
            if (which)
                mapToUse = inputItems;
            else
                mapToUse = outputItems;

            mapToUse.forEach((stack, atoms) -> {
                if (!testMoleCounts(stack, atoms))
                    b.set(true);
            });

            return !b.get();
        }

        // TODO
        public boolean verifyFormula() {
            return true;
        }

        /**
         * Used internally to create the mappings of the chemicals.
         */
        private void buildChemicals(boolean doFluids) {
            recipe.inputs.forEach(stack -> calculateChemical(stack, true));
            recipe.outputs.forEach(stack -> calculateChemical(stack, false));
            if (doFluids) {
                recipe.fluidInputs.forEach(stack -> calculateChemical(stack, true));
                recipe.fluidOutputs.forEach(stack -> calculateChemical(stack, false));
            }
        }

        private <T> void calculateChemical(T param, boolean isInput) {
            String chemicalFormula = null;

            // Acquire the chemical formula.
            // This isn't perfect, but still good enough to test chemistry.
            if (param instanceof ItemStack) {
                ItemStack stack = (ItemStack) param;

                if (SimpleDustMaterial.GA_DUSTS.containsKey((short) stack.getItemDamage()) && !stack.getTranslationKey().contains("material")) {
                    SimpleDustMaterial material = SimpleDustMaterial.GA_DUSTS.get((short) stack.getItemDamage());
                    chemicalFormula = material.chemicalFormula;
                } else {
                    Material material = Material.MATERIAL_REGISTRY.getObjectById(stack.getItemDamage());
                    if (stack.getTranslationKey().contains("material") && material != null) {
                        chemicalFormula = material.chemicalFormula;
                    }
                }
            } else if (param instanceof FluidStack) {
                FluidStack stack = (FluidStack) param;

                String[] splitString = stack.getUnlocalizedName().split("\\.");
                if (splitString.length >= 2) {
                    if (splitString[0].equals("fluid")) {
                        SimpleFluidMaterial material = SimpleFluidMaterial.GA_FLUIDS.get(splitString[1]);
                        if (material != null)
                            chemicalFormula = material.chemicalFormula;
                    } else if(splitString[0].equals("material")) {
                        Material material = FluidMaterial.MATERIAL_REGISTRY.getObject(splitString[1]);
                        if (material != null)
                            chemicalFormula = material.chemicalFormula;
                    }
                }
            }

            // Build the Mapping of elements to counts for
            // the formula, if it matches the regex.
            if (chemicalFormula != null) {

                // Trim out weird formulas like "???" and strange formatting chars.
                // NOTE: This will FAIL if the formula contains square brackets.
                if (chemicalFormula.matches("([A-Z][a-z]*)(\\d*)|(\\()|(\\))(\\d*)")) {
                    Map<String, Integer> atoms = calculateFormula(chemicalFormula, 0);
                    if (isInput) {
                        if (param instanceof ItemStack)
                            inputItems.put((ItemStack) param, atoms);
                        else inputFluids.put((FluidStack) param, atoms);
                    } else {
                        if (param instanceof ItemStack)
                            outputItems.put((ItemStack) param, atoms);
                        else outputFluids.put((FluidStack) param, atoms);
                    }
                }
            }
        }

        private static Map<String, Integer> calculateFormula(String formula, int i) {
            int N = formula.length();
            Map<String, Integer> count = new TreeMap<>();
            while (i < N && formula.charAt(i) != ')') {
                if (formula.charAt(i) == '(') {
                    i++;
                    for (Map.Entry<String, Integer> entry : calculateFormula(formula, i).entrySet()) {
                        count.put(entry.getKey(), count.getOrDefault(entry.getKey(), 0) + entry.getValue());
                    }
                } else {
                    int iStart = i++;
                    while (i < N && Character.isLowerCase(formula.charAt(i))) i++;
                    String name = formula.substring(iStart, i);
                    iStart = i;
                    while (i < N && Character.isDigit(formula.charAt(i))) i++;
                    int multiplicity = iStart < i ? Integer.parseInt(formula.substring(iStart, i)) : 1;
                    count.put(name, count.getOrDefault(name, 0) + multiplicity);
                }
            }
            int iStart = ++i;
            while (i < N && Character.isDigit(formula.charAt(i))) i++;
            if (iStart < i) {
                int multiplicity = Integer.parseInt(formula.substring(iStart, i));
                count.replaceAll((k, v) -> count.get(k) * multiplicity);
            }
            return count;
        }
    }
}
