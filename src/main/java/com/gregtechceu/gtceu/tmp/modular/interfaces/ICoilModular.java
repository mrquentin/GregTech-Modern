package com.gregtechceu.gtceu.tmp.modular.interfaces;

import com.gregtechceu.gtceu.api.block.ICoilType;

public interface ICoilModular {
    int getTier();
    int getCoilTier();
    ICoilType getCoilType();
    long getOverclockVoltage();
}
