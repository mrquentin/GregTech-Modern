package com.gregtechceu.gtceu.integration.top.provider;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;

import com.gregtechceu.gtceu.api.capability.IEnergyInfoProvider;
import com.gregtechceu.gtceu.integration.top.element.ProgressElement;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class ElectricContainerInfoProvider extends CapabilityInfoProvider<IEnergyInfoProvider> {

    @Override
    public ResourceLocation getID() {
        return GTCEu.id("energy_container_provider");
    }

    @Nullable
    @Override
    protected IEnergyInfoProvider getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        return GTCapabilityHelper.getEnergyInfoProvider(level, pos, side);
    }

    @Override
    protected void addProbeInfo(IEnergyInfoProvider capability, IProbeInfo probeInfo, Player player,
                                BlockEntity blockEntity, IProbeHitData data) {
        var supportBigInteger = capability.supportsBigIntEnergyValues();
        var energyInfo = capability.getEnergyInfo();

        String energyStr;
        String maxEnergyStr;
        float progress;

        if (supportBigInteger) {
            if (energyInfo.capacity().compareTo(BigInteger.ZERO) <= 0) return;
            energyStr = FormattingUtil.formatNumberOrSic(energyInfo.stored(), 1e12);
            maxEnergyStr = FormattingUtil.formatNumberOrSic(energyInfo.capacity(), 1e12);
            progress = getProgress(energyInfo.stored(), energyInfo.capacity());
        } else {
            if (energyInfo.capacity().longValue() == 0) return;
            energyStr = FormattingUtil.formatNumberOrSic(energyInfo.stored().longValue(), 1e12);
            maxEnergyStr = FormattingUtil.formatNumberOrSic(energyInfo.capacity().longValue(), 1e12);
            progress = getProgress(energyInfo.stored().longValue(), energyInfo.capacity().longValue());
        }

        probeInfo.element(new ProgressElement(
                progress,
                Component.translatable("gtceu.jade.energy_stored", energyStr, maxEnergyStr),
                probeInfo.defaultProgressStyle()
                        .filledColor(0xFFEEE600)
                        .alternateFilledColor(0xFFEEE600)
                        .borderColor(0xFF555555))
        );
    }

    protected float getProgress(long progress, long maxProgress) {
        return maxProgress == 0 ? 0 : (float) ((double) progress / maxProgress);
    }

    protected float getProgress(BigInteger progress, BigInteger maxProgress) {
        if (maxProgress.equals(BigInteger.ZERO)) return 0;
        return new BigDecimal(progress).divide(new BigDecimal(maxProgress), MathContext.DECIMAL32).floatValue();
    }

    @Override
    protected boolean allowDisplaying(@NotNull IEnergyInfoProvider capability) {
        return !capability.isOneProbeHidden();
    }
}
