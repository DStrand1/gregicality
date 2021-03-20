package gregicadditions.machines;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregicadditions.GAValues;
import gregicadditions.utils.GALog;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.CycleButtonWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.render.Textures;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.blocks.BlockGranite;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.blocks.StoneBlock;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gregtech.api.unification.material.Materials.*;

public class MetaTileEntityRockBreaker extends TieredMetaTileEntity {

    private RockList setTypes;

    public MetaTileEntityRockBreaker(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        setTypes = RockList.VANILLA;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return new MetaTileEntityRockBreaker(metaTileEntityId, getTier());
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeEnumValue(setTypes);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        setTypes = buf.readEnumValue(RockList.class);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setString("RockType", setTypes.getNBTKey());
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.setTypes = RockList.getFromNBTKey(data.getString("RockType"));
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote && getTimer() % 20 == 0 && checkSides(Blocks.LAVA) && checkSides(Blocks.WATER) && energyContainer.getEnergyStored() >= getEnergyPerBlockBreak() * 4L) {
            int stack = setTypes == RockList.VANILLA ?
                    (int) Math.pow(2, getTier()) :
                    (int) Math.pow(2, getTier() - 1);

            GALog.logger.info(setTypes.toString());
            int insertSlot = 0;
            List<ItemStack> returns = new ArrayList<>();
            for (RockType rockType : setTypes.types) {
                for (int i = 0; i < 4; i++) {
                    ItemStack rock = rockType.rock.copy();
                    rock.setCount(stack);
                    returns.add(insertSlot / 4, exportItems.insertItem(insertSlot++, rock, false));
                }
            }

            // Consume energy equal to 0.25A of tier times types of rocks produced
            long size;
            if ((size = returns.stream().filter(is -> is.getCount() != stack).count()) > 0)
                energyContainer.removeEnergy(getEnergyPerBlockBreak() * size);
        }
    }

    private boolean checkSides(BlockStaticLiquid liquid) {
        EnumFacing frontFacing = getFrontFacing();
        for (EnumFacing side : EnumFacing.VALUES) {
            if (side == frontFacing || side == EnumFacing.DOWN || side == EnumFacing.UP) continue;
            if (getWorld().getBlockState(getPos().offset(side)) == liquid.getDefaultState())
                return true;
        }
        return false;
    }

    // Base 0.25A when only 1 rock type produced
    private int getEnergyPerBlockBreak() {
        return GAValues.V[getTier()] / 4;
    }

    @Override
    protected IItemHandlerModifiable createExportItemHandler() {
        return new ItemStackHandler(16);
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        int rowSize = 4;
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 176, 18 + 18 * rowSize + 94)
                .label(10, 5, getMetaFullName());

        for (int y = 0; y < rowSize; y++) {
            for (int x = 0; x < rowSize; x++) {
                int index = y * rowSize + x;
                builder.widget(new SlotWidget(exportItems, index, 89 - rowSize * 9 + x * 18, 18 + y * 18, true, false)
                        .setBackgroundTexture(GuiTextures.SLOT));
            }
        }

        Widget button = new CycleButtonWidget(26, 45, 18, 18,
                RockList.class, this::getRockType, this::setRockType)
                .setTooltipHoverString(getButtonHover());
        builder.widget(button);


        builder.bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 8, 18 + 18 * rowSize + 12);
        return builder.build(getHolder(), entityPlayer);
    }

    private void setRockType(RockList rockList) {
        setTypes = rockList;
    }

    private RockList getRockType() {
        return setTypes;
    }

    private String getButtonHover() {
        if (setTypes == RockList.VANILLA)
            return "gtadditions.machine.rock_breaker.button_vanilla";
        else if (getTier() == 1)
            return "gtadditions.machine.rock_breaker.button_gregtech_low_power";
        else
            return "gtadditions.machine.rock_breaker.button_gregtech";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.HAMMER_OVERLAY.render(renderState, translation, pipeline, getFrontFacing(), false);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gtadditions.machine.rock_breaker.description"));
    }

    private enum RockList implements IStringSerializable {

        VANILLA("gtadditions.machine.rock_breaker.mode.vanilla",
                RockType.COBBLESTONE, RockType.DIORITE, RockType.GRANITE, RockType.ANDESITE),
        GREGTECH("gtadditions.machine.rock_breaker.mode.gregtech",
                RockType.BLACKGRANITE, RockType.REDGRANITE, RockType.BASALT, RockType.MARBLE);

        String localeName;
        List<RockType> types;

        RockList(String localeName, RockType... types) {
            this.localeName = localeName;
            this.types = new ArrayList<>(Arrays.asList(types));
        }

        @Override
        public String getName() {
            return localeName;
        }

        @Override
        public String toString() {
            return localeName.split("\\.")[4];
        }

        String getNBTKey() {
            if (this == VANILLA)
                return "vanilla";
            return "gregtech";
        }

        static RockList getFromNBTKey(String key) {
            if (key.equals("vanilla"))
                return VANILLA;
            return GREGTECH;
        }
    }

    private enum RockType {

        // Vanilla
        COBBLESTONE(new ItemStack(Blocks.COBBLESTONE)),
        DIORITE(new ItemStack(Blocks.STONE, 1, 3)),
        GRANITE(new ItemStack(Blocks.STONE, 1, 1)),
        ANDESITE(new ItemStack(Blocks.STONE, 1, 5)),

        // GregTech
        BLACKGRANITE(new ItemStack(MetaBlocks.GRANITE.withVariant(BlockGranite.GraniteVariant.BLACK_GRANITE, StoneBlock.ChiselingVariant.NORMAL).getBlock())),
        REDGRANITE(OreDictUnifier.get(OrePrefix.block, GraniteRed)),
        BASALT(OreDictUnifier.get(OrePrefix.block, Basalt)),
        MARBLE(OreDictUnifier.get(OrePrefix.block, Marble));

        ItemStack rock;

        RockType(ItemStack rock) {
            this.rock = rock;
        }
    }
}
