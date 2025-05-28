package com.gregtechceu.gtceu.tmp.modular.interfaces;

import net.minecraft.core.BlockPos;

/// Contains the methods required for both BaseMachines and ModuleMachines
public sealed interface IModularBase permits IModularMultiblock, IMultiblockModule {
    BlockPos getPos();
}
