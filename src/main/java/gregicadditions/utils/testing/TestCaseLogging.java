package gregicadditions.utils.testing;

import gregicadditions.utils.GALog;

public class TestCaseLogging {

    protected static void simpleLoggingDupe(HelperObjects.RecipeList list, String mapName, boolean detailed, boolean exit) {
        for (HelperObjects.RecipeStruct recipe : list.duplicates) {
            GALog.logger.error("Duplicate found, "
                    + (detailed ? recipe.toStringDetailed() : recipe.toString()));
        }
        if (!list.errored.isEmpty()) {
            GALog.logger.error("Recipes that could not be handled, check manually:");
            GALog.logger.error("Report this error if far too many recipes are listed");
        }
        for (HelperObjects.RecipeStruct recipe : list.errored)
            GALog.logger.error("Could not handle recipe with output: "
                    + (detailed ? recipe.toStringDetailed() : recipe.toString()));

        if (!list.duplicates.isEmpty())
            if (exit)
                GALog.logger.error("Exiting...", new IllegalStateException());
            else
                GALog.logger.info("No recipe duplicates for " + mapName + " found");
    }

    // TODO Make this much more interesting
    protected static void simpleLoggingConflict(HelperObjects.RecipeList list, String mapName, boolean detailed, boolean exit) {
        for (HelperObjects.RecipeStruct recipe : list.duplicates) {
            GALog.logger.error("Conflict found, "
                    + (detailed ? recipe.toStringDetailed() : recipe.toString()));
        }
        if (!list.errored.isEmpty()) {
            GALog.logger.error("Recipes that could not be handled, check manually:");
            GALog.logger.error("Report this error if far too many recipes are listed");
        }
        for (HelperObjects.RecipeStruct recipe : list.errored)
            GALog.logger.error("Could not handle recipe with output: "
                    + (detailed ? recipe.toStringDetailed() : recipe.toString()));

        if (!list.duplicates.isEmpty())
            if (exit)
                GALog.logger.error("Exiting...", new IllegalStateException());
            else
                GALog.logger.info("No recipe conflicts for " + mapName + " found");
    }
}
