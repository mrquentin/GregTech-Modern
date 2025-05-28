package com.gregtechceu.gtceu.tmp.register;

import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.tmp.modular.interfaces.ICoilModular;
import com.gregtechceu.gtceu.tmp.modular.WorkableElectricMultiblockMachineModule;

import java.util.List;

public class OrbitalForgeModule extends WorkableElectricMultiblockMachineModule implements ICoilModular {
    public OrbitalForgeModule(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public int getCoilTier() {
        for (var base : getBaseMultiBlocks()) {
            if (base instanceof OrbitalForgeModularMultiblockMachine orbital) {
                return orbital.getCoilTier();
            }
        }
        return 0;
    }

    public ICoilType getCoilType() {
        for (var base : getBaseMultiBlocks()) {
            if (base instanceof OrbitalForgeModularMultiblockMachine orbital) {
                return orbital.getCoilType();
            }
        }
        return null;
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        super.setWorkingEnabled(isWorkingAllowed);
    }

    @Override
    public void addCapabilitiesFromBase(List<IoRecipeCapability> capabilitiesToExtract) {
        super.addCapabilitiesFromBase(capabilitiesToExtract);
        capabilitiesToExtract.add(new IoRecipeCapability(IO.BOTH, EURecipeCapability.CAP));
        capabilitiesToExtract.add(new IoRecipeCapability(IO.OUT, ItemRecipeCapability.CAP));
    }
}
