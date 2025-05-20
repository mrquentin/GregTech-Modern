package com.gregtechceu.gtceu.tmp.register;

import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.tmp.modular.WorkableElectricMultiblockMachineModule;

public class OrbitalForgeModule extends WorkableElectricMultiblockMachineModule {
    public OrbitalForgeModule(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public ICoilType getCoilType() {
        for (var base : getBaseMultiBlocks()) {
            if (base instanceof OrbitalForgeModularMultiblockMachine orbital) {
                return orbital.getCoilType();
            }
        }
        return null;
    }
}
