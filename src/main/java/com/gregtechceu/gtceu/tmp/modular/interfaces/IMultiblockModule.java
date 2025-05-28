package com.gregtechceu.gtceu.tmp.modular.interfaces;

import net.minecraft.core.BlockPos;

public non-sealed interface IMultiblockModule extends IModularBase {

    void addBase(IModularMultiblock base);

    void removeBase(IModularMultiblock base);

    void onBaseUpdate();

    void onBaseFormed();

    void onBaseInvalid();

    void notifyBases();

    int getBaseCount();
}
