package com.gregtechceu.gtceu.tmp.modular;

import com.gregtechceu.gtceu.api.block.ICoilType;

public interface ICoilModularMultiblock {
    int getTier();
    int getCoilTier();
    ICoilType getCoilType();
    long getOverclockVoltage();
}
