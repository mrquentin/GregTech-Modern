package com.gregtechceu.gtceu.tmp.modular.interfaces;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import net.minecraft.core.BlockPos;

import java.util.List;

public non-sealed interface IModularMultiblock extends IModularBase {

    void addModule(IMultiblockModule module);

    void removeModule(IMultiblockModule module);

    void onModuleUpdate();

    void onModuleFormed();

    void onModuleInvalid();

    void notifyModules();

    int getModuleCount();

    boolean isWorking();

    boolean isFormed();

    List<IRecipeHandler<?>> getCapabilities(IO io, RecipeCapability<?> cap);
}
