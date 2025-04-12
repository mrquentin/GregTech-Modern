package com.gregtechceu.gtceu.integration.jade.provider;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.IEnergyInfoProvider;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class ElectricContainerBlockProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public ResourceLocation getUid() {
        return GTCEu.id("electric_container_provider");
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof IMachineBlockEntity blockEntity) {
            var machine = blockEntity.getMetaMachine();
            if (machine instanceof IEnergyInfoProvider energyInfoProvider) {
                var supportBigIntegers = energyInfoProvider.supportsBigIntEnergyValues();
                compoundTag.putBoolean("SupportBigIntegers", supportBigIntegers);

                var energyInfo = energyInfoProvider.getEnergyInfo();
                if (!supportBigIntegers) {
                    compoundTag.putLong("Energy", energyInfo.stored().longValue());
                    compoundTag.putLong("MaxEnergy", energyInfo.capacity().longValue());
                } else {

                    compoundTag.putByteArray("Energy", energyInfo.stored().toByteArray());
                    compoundTag.putByteArray("MaxEnergy", energyInfo.capacity().toByteArray());
                }
            }
        }
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getBlockEntity() instanceof IMachineBlockEntity blockEntity) {
            var machine = blockEntity.getMetaMachine();
            if (machine instanceof IEnergyInfoProvider energyInfoProvider) {
                var supportBigIntegers = blockAccessor.getServerData().getBoolean("SupportBigIntegers");

                String energyStr;
                String maxEnergyStr;
                float progress;

                if (supportBigIntegers) {
                    var energy = new BigInteger(blockAccessor.getServerData().getByteArray("Energy"));
                    var maxEnergy = new BigInteger(blockAccessor.getServerData().getByteArray("MaxEnergy"));
                    if (maxEnergy.compareTo(BigInteger.ZERO) <= 0) return;
                    energyStr = FormattingUtil.formatNumbers(energy);
                    maxEnergyStr = FormattingUtil.formatNumbers(maxEnergy);
                    progress = getProgress(energy, maxEnergy);
                } else {
                    var energy = blockAccessor.getServerData().getLong("Energy");
                    var maxEnergy = blockAccessor.getServerData().getLong("MaxEnergy");
                    if (maxEnergy == 0) return;
                    energyStr = FormattingUtil.formatNumbers(energy);
                    maxEnergyStr = FormattingUtil.formatNumbers(maxEnergy);
                    progress = getProgress(energy, maxEnergy);
                }

                var helper = iTooltip.getElementHelper();

                iTooltip.add(
                        helper.progress(
                                progress,
                                Component.translatable("gtceu.jade.energy_stored", energyStr, maxEnergyStr),
                                helper.progressStyle().color(0xFFEEE600, 0xFFEEE600).textColor(-1),
                                Util.make(BoxStyle.DEFAULT, style -> style.borderColor = 0xFF555555),
                                true
                        )
                );
            }
        }
    }

    protected float getProgress(long progress, long maxProgress) {
        return maxProgress == 0 ? 0 : (float) ((double) progress / maxProgress);
    }

    protected float getProgress(BigInteger progress, BigInteger maxProgress) {
        if (maxProgress.equals(BigInteger.ZERO)) return 0;
        return new BigDecimal(progress).divide(new BigDecimal(maxProgress), MathContext.DECIMAL32).floatValue();
    }

//    @Override
//    protected boolean allowDisplaying(IEnergyContainer capability) {
//        return !capability.isOneProbeHidden();
//    }
}
