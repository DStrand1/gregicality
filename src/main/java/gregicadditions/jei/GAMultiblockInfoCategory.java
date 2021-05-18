package gregicadditions.jei;

import com.google.common.collect.Lists;
import gregicadditions.jei.multi.*;
import gregicadditions.jei.multi.advance.*;
import gregicadditions.jei.multi.miner.LargeMinerInfo;
import gregicadditions.jei.multi.miner.VoidMinerInfo;
import gregicadditions.jei.multi.miner.VoidMinerInfo2;
import gregicadditions.jei.multi.miner.VoidMinerInfo3;
import gregicadditions.jei.multi.nuclear.GasCentrifugeInfo;
import gregicadditions.jei.multi.nuclear.HotCoolantTurbineInfo;
import gregicadditions.jei.multi.nuclear.NuclearReactorInfo;
import gregicadditions.jei.multi.override.*;
import gregicadditions.jei.multi.quantum.QubitComputerInfo;
import gregicadditions.jei.multi.simple.*;
import gregicadditions.machines.GATileEntities;
import gregtech.integration.jei.multiblock.MultiblockInfoRecipeWrapper;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.gui.recipes.RecipeLayout;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;

@MethodsReturnNonnullByDefault
public class GAMultiblockInfoCategory implements IRecipeCategory<MultiblockInfoRecipeWrapper> {
    private final IDrawable background;
    private final IGuiHelper guiHelper;

    public GAMultiblockInfoCategory(IJeiHelpers helpers) {
        this.guiHelper = helpers.getGuiHelper();
        this.background = guiHelper.createBlankDrawable(176, 166);
    }

    public static void registerRecipes(IModRegistry registry) {
        registry.addRecipes(Lists.newArrayList(
                new MultiblockInfoRecipeWrapper(new CentralMonitorInfo()),
                new MultiblockInfoRecipeWrapper(new ElectricBlastFurnaceInfo()),
                new MultiblockInfoRecipeWrapper(new CrackerUnitInfo()),
                new MultiblockInfoRecipeWrapper(new DieselEngineInfo()),
                new MultiblockInfoRecipeWrapper(new DistillationTowerInfo()),
                new MultiblockInfoRecipeWrapper(new ImplosionCompressorInfo()),
                new MultiblockInfoRecipeWrapper(new MultiSmelterInfo()),
                new MultiblockInfoRecipeWrapper(new VacuumFreezerInfo()),
                new MultiblockInfoRecipeWrapper(new PyrolyseOvenInfo()),
                new MultiblockInfoRecipeWrapper(new AssemblyLineInfo()),
                new MultiblockInfoRecipeWrapper(new FusionReactorInfo(0)),
                new MultiblockInfoRecipeWrapper(new FusionReactorInfo(1)),
                new MultiblockInfoRecipeWrapper(new FusionReactorInfo(2)),
                new MultiblockInfoRecipeWrapper(new ProcessingArrayInfo()),

                new MultiblockInfoRecipeWrapper(new LargeThermalCentrifugeInfo()), // rotate 180, description
                new MultiblockInfoRecipeWrapper(new LargeElectrolyzerInfo()), // same
                new MultiblockInfoRecipeWrapper(new LargeCentrifugeInfo()), // same
                new MultiblockInfoRecipeWrapper(new LargeCuttingInfo()), // same
                new MultiblockInfoRecipeWrapper(new LargeMixerInfo()), // same
                new MultiblockInfoRecipeWrapper(new LargeMultiUseInfo()), // same
                new MultiblockInfoRecipeWrapper(new LargeMaceratorInfo()), // same
                new MultiblockInfoRecipeWrapper(new LargeSifterInfo()), // same
                new MultiblockInfoRecipeWrapper(new LargeWashingPlantInfo()), // same
                new MultiblockInfoRecipeWrapper(new LargeWiremillInfo()), // desc, fix
                new MultiblockInfoRecipeWrapper(new LargeChemicalReactorInfo()), // desc, fix
                new MultiblockInfoRecipeWrapper(new LargeExtruderInfo()), // desc
                new MultiblockInfoRecipeWrapper(new VolcanusInfo()),
                new MultiblockInfoRecipeWrapper(new LargeAssemblerInfo()), // desc
                new MultiblockInfoRecipeWrapper(new LargeBenderAndFormingInfo()), // same
                new MultiblockInfoRecipeWrapper(new LargeMinerInfo(GATileEntities.LARGE_MINER[0])), // same all 3
                new MultiblockInfoRecipeWrapper(new LargeMinerInfo(GATileEntities.LARGE_MINER[1])),
                new MultiblockInfoRecipeWrapper(new LargeMinerInfo(GATileEntities.LARGE_MINER[2])),
                new MultiblockInfoRecipeWrapper(new LargeTurbineInfo(GATileEntities.LARGE_STEAM_TURBINE)), // add rotor?
                new MultiblockInfoRecipeWrapper(new LargeTurbineInfo(GATileEntities.LARGE_GAS_TURBINE)), // add rotor?
                new MultiblockInfoRecipeWrapper(new LargeTurbineInfo(GATileEntities.LARGE_PLASMA_TURBINE)), // add rotor?
                new MultiblockInfoRecipeWrapper(new HotCoolantTurbineInfo(GATileEntities.HOT_COOLANT_TURBINE)), // add rotor, test in game
                new MultiblockInfoRecipeWrapper(new NuclearReactorInfo(GATileEntities.NUCLEAR_REACTOR)), // same as below, rotate 180
                new MultiblockInfoRecipeWrapper(new NuclearReactorInfo(GATileEntities.NUCLEAR_BREEDER)),
                new MultiblockInfoRecipeWrapper(new LargeCircuitAssemblyLineInfo()), // test


                // TODO BELOW HERE
                new LargeMultiblockInfoRecipeWrapper(new VoidMinerInfo()),
                new MultiblockInfoRecipeWrapper(new LargeTransformerInfo()),
                new MultiblockInfoRecipeWrapper(new IndustrialPrimitiveBlastFurnaceInfo()),
                new LargeMultiblockInfoRecipeWrapper(new AdvancedDistillationTowerInfo()),
                new MultiblockInfoRecipeWrapper(new CryogenicFreezerInfo()),
                new LargeMultiblockInfoRecipeWrapper(new ChemicalPlantInfo()),
                new LargeMultiblockInfoRecipeWrapper(new LargeRocketEngineInfo()),
                new MultiblockInfoRecipeWrapper(new AlloyBlastFurnaceInfo()),
                new MultiblockInfoRecipeWrapper(new LargeForgeHammerInfo()),
                new LargeMultiblockInfoRecipeWrapper(new LargeNaquadahReactorInfo()),
                new LargeMultiblockInfoRecipeWrapper(new BatteryTowerInfo()),
                new LargeMultiblockInfoRecipeWrapper(new HyperReactor1Info()),
                new LargeMultiblockInfoRecipeWrapper(new HyperReactor2Info()),
                new LargeMultiblockInfoRecipeWrapper(new HyperReactor3Info()),
                new LargeMultiblockInfoRecipeWrapper(new AdvancedFusionReactorInfo()),
                new LargeMultiblockInfoRecipeWrapper(new GasCentrifugeInfo()),
                new MultiblockInfoRecipeWrapper(new QubitComputerInfo()),
                new LargeMultiblockInfoRecipeWrapper(new DrillingRigInfo()),
                new LargeMultiblockInfoRecipeWrapper(new StellarForgeInfo()),
                new MultiblockInfoRecipeWrapper(new LargeEngraverInfo()),
                new LargeMultiblockInfoRecipeWrapper(new VoidMinerInfo2()),
                new LargeMultiblockInfoRecipeWrapper(new VoidMinerInfo3()),
                new LargeMultiblockInfoRecipeWrapper(new BioReactorInfo()),
                new MultiblockInfoRecipeWrapper(new PlasmaCondenserInfo()),
                new MultiblockInfoRecipeWrapper(new LargePackagerInfo()),
                new MultiblockInfoRecipeWrapper(new SteamGrinderInfo()),
                new MultiblockInfoRecipeWrapper(new SteamOvenInfo()),
                new MultiblockInfoRecipeWrapper(new CosmicRayDetectorInfo()),
                new MultiblockInfoRecipeWrapper(new ElectricImplosionInfo())
        ), "gregtech:multiblock_info");
    }

    @Override
    public String getUid() {
        return "gtadditions:multiblock_info2";
    }

    @Override
    public String getTitle() {
        return I18n.format("gregtech.multiblock.title");
    }

    @Override
    public String getModName() {
        return "gtadditions";
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, MultiblockInfoRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
        recipeWrapper.setRecipeLayout((RecipeLayout) recipeLayout, guiHelper);
    }
}
