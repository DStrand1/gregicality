package gregicadditions.jei.multi.simple;

import com.google.common.collect.Lists;
import gregicadditions.GAValues;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.BlockInfo;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.items.behaviors.TurbineRotorBehavior;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.common.metatileentities.electric.multiblockpart.MetaTileEntityRotorHolder;
import gregtech.common.metatileentities.multi.electric.generator.MetaTileEntityLargeTurbine;
import gregtech.integration.jei.multiblock.MultiblockInfoPage;
import gregtech.integration.jei.multiblock.MultiblockShapeInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.List;

public class LargeTurbineInfo extends MultiblockInfoPage {

    private final MetaTileEntityLargeTurbine turbine;

    public LargeTurbineInfo(MetaTileEntityLargeTurbine turbine) {
        this.turbine = turbine;
    }

    @Override
    public MultiblockControllerBase getController() {
        return turbine;
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        MetaTileEntityHolder holder = new MetaTileEntityHolder();
        holder.setMetaTileEntity(MetaTileEntities.ROTOR_HOLDER[2]);
        holder.getMetaTileEntity().setFrontFacing(EnumFacing.NORTH);
        ItemStack rotorStack = MetaItems.TURBINE_ROTOR.getStackForm();
        TurbineRotorBehavior.getInstanceFor(rotorStack).setPartMaterial(rotorStack, Materials.Darmstadtium);
        ((MetaTileEntityRotorHolder) holder.getMetaTileEntity()).getRotorInventory().setStackInSlot(0, rotorStack);

        MultiblockShapeInfo.Builder shapeInfo = MultiblockShapeInfo.builder()
                .aisle("CCC", "CRC", "CCC")
                .aisle("CCC", "S#I", "CCC")
                .aisle("CCC", "C#O", "CCC")
                .aisle("CCC", "CDC", "CCC")
                .where('S', turbine, EnumFacing.WEST)
                .where('C', turbine.turbineType.casingState)
                .where('R', new BlockInfo(MetaBlocks.MACHINE.getDefaultState(), holder))
                .where('D', MetaTileEntities.ENERGY_OUTPUT_HATCH[GAValues.EV], EnumFacing.SOUTH)
                .where('#', Blocks.AIR.getDefaultState())
                .where('I', MetaTileEntities.FLUID_IMPORT_HATCH[GAValues.HV], EnumFacing.EAST);

        if (turbine.turbineType.hasOutputHatch) {
            shapeInfo.where('O', MetaTileEntities.FLUID_EXPORT_HATCH[GAValues.EV], EnumFacing.EAST);
        } else {
            shapeInfo.where('O', turbine.turbineType.casingState);
        }
        return Lists.newArrayList(shapeInfo.build());
    }

    @Override
    public String[] getDescription() {
        return new String[]{I18n.format("gregtech.multiblock.large_turbine.description")};
    }
}
