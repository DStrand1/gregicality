package gregicadditions.jei;

import com.google.common.collect.Lists;
import gregicadditions.item.GAMetaBlocks;
import gregicadditions.item.fusion.GAFusionCasing;
import gregicadditions.machines.GATileEntities;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.integration.jei.multiblock.MultiblockInfoPage;
import gregtech.integration.jei.multiblock.MultiblockShapeInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

import java.util.List;

public class FusionReactorInfo extends MultiblockInfoPage {

    private final int tier;

    public FusionReactorInfo(int tier) {
        this.tier = tier;
    }

    @Override
    public MultiblockControllerBase getController() {
        return GATileEntities.FUSION_REACTOR[tier];
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        MultiblockShapeInfo shapeInfo = MultiblockShapeInfo.builder()
                .aisle("###############", "######NCN######", "###############")
                .aisle("######DCD######", "####CCcccCC####", "######UCU######")
                .aisle("####CC###CC####", "###sccNCNccs###", "####CC###CC####")
                .aisle("###C#######C###", "##wcnC###Cnce##", "###C#######C###")
                .aisle("##C#########C##", "#Cce#######wcC#", "##C#########C##")
                .aisle("##C#########C##", "#CcC#######CcC#", "##C#########C##")
                .aisle("#D###########D#", "WcE#########WcE", "#U###########U#")
                .aisle("#C###########C#", "CcC#########CcC", "#C###########C#")
                .aisle("#D###########D#", "WcE#########WcE", "#U###########U#")
                .aisle("##C#########C##", "#CcC#######CcC#", "##C#########C##")
                .aisle("##C#########C##", "#Cce#######wcC#", "##C#########C##")
                .aisle("###C#######C###", "##wcsC###Csce##", "###C#######C###")
                .aisle("####CC###CC####", "###nccSCSccn###", "####CC###CC####")
                .aisle("######DCD######", "####CCcccCC####", "######UCU######")
                .aisle("###############", "######NMN######", "###############")
                .where('M', GATileEntities.FUSION_REACTOR[tier], EnumFacing.SOUTH)
                .where('C', getCasingState())
                .where('c', getCoilState())
                .where('W', MetaTileEntities.FLUID_EXPORT_HATCH[tier + 6], EnumFacing.WEST)
                .where('E', MetaTileEntities.FLUID_EXPORT_HATCH[tier + 6], EnumFacing.EAST)
                .where('S', MetaTileEntities.FLUID_EXPORT_HATCH[tier + 6], EnumFacing.SOUTH)
                .where('N', MetaTileEntities.FLUID_EXPORT_HATCH[tier + 6], EnumFacing.NORTH)
                .where('w', MetaTileEntities.ENERGY_INPUT_HATCH[tier + 6], EnumFacing.WEST)
                .where('e', MetaTileEntities.ENERGY_INPUT_HATCH[tier + 6], EnumFacing.EAST)
                .where('s', MetaTileEntities.ENERGY_INPUT_HATCH[tier + 6], EnumFacing.SOUTH)
                .where('n', MetaTileEntities.ENERGY_INPUT_HATCH[tier + 6], EnumFacing.NORTH)
                .where('U', MetaTileEntities.FLUID_IMPORT_HATCH[tier + 6], EnumFacing.UP)
                .where('D', MetaTileEntities.FLUID_IMPORT_HATCH[tier + 6], EnumFacing.DOWN)
                .where('#', Blocks.AIR.getDefaultState())
                .build();

        return Lists.newArrayList(shapeInfo);
    }

    private IBlockState getCasingState() {
        switch (tier) {
            case 0:
                return MetaBlocks.MUTLIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.FUSION_CASING);
            case 1:
                return MetaBlocks.MUTLIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.FUSION_CASING_MK2);
            default:
                return GAMetaBlocks.FUSION_CASING.getState(GAFusionCasing.CasingType.FUSION_3);
        }
    }

    private IBlockState getCoilState() {
        switch (tier) {
            case 0:
                return MetaBlocks.WIRE_COIL.getState(BlockWireCoil.CoilType.FUSION_COIL);
            case 1:
                return GAMetaBlocks.FUSION_CASING.getState(GAFusionCasing.CasingType.FUSION_COIL_2);
            default:
                return GAMetaBlocks.FUSION_CASING.getState(GAFusionCasing.CasingType.FUSION_COIL_3);
        }
    }

    @Override
    public String[] getDescription() {
        String translationKey = String.format("gregtech.multiblock.fusion_reactor_mk%d.description", tier + 1);
        return new String[]{I18n.format(translationKey)};
    }

    @Override
    public float getDefaultZoom() {
        return 0.4f;
    }
}
