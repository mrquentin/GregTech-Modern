package com.gregtechceu.gtceu.tmp.modular;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.tmp.modular.interfaces.IModularMultiblock;
import com.gregtechceu.gtceu.tmp.modular.interfaces.IMultiblockModule;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;

import java.util.*;

public class WorkableModularMultiblockMachine extends WorkableMultiblockMachine implements IModularMultiblock {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            WorkableModularMultiblockMachine.class, WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    private final Set<BlockPos> modulesPoss = new ObjectOpenHashSet<>();
    private final Set<IMultiblockModule> moduleMachines = new ObjectOpenHashSet<>();
    private boolean modulesResolved = false;

    public WorkableModularMultiblockMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    /// ============================================================
    /// ======================= LIFECYCLE ==========================
    /// ============================================================

    ///  made final to avoid further overriding in subclasses
    @Override
    public final void onStructureFormed() {
        super.onStructureFormed();
        onBaseStructureFormed();
        for (IMultiblockModule moduleMachine : this.moduleMachines)
            moduleMachine.onBaseFormed();
    }

    /// Extracted the formation logic to another method in order to always have the onBaseFormed callback happen after all the setup is over
    public void onBaseStructureFormed() {
        Map<Long, IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap", Long2ObjectMaps::emptyMap);
        var poss = new ArrayList<BlockPos>();
        for (IMultiPart part : getParts()) {
            if (part instanceof ModuleConnectorPartMachine) {
                for (var controller : part.getControllers()) {
                    if (controller instanceof IMultiblockModule master) {
                        poss.add(master.getPos());
                    }
                }
            }
        }
        setModules(poss);
    }

    @Override
    public final void onStructureInvalid() {
        super.onStructureInvalid();
        onBaseStructureInvalid();
        this.moduleMachines.forEach(IMultiblockModule::onBaseInvalid);
        this.moduleMachines.clear();
        this.modulesPoss.clear();
    }

    public void onBaseStructureInvalid() {
        var modules = getModules();
        if (!modules.isEmpty()) {
            for (var module : modules) {
                module.removeBase(this);
            }
        }
    }

    /// ============================================================
    /// ============== MODULE LIFECYCLE CALLBACKS ==================
    /// ============================================================


    @Override
    public void onModuleFormed() {
        GTCEu.LOGGER.info("onModuleFormed. IsClient: {}", getLevel().isClientSide);
    }

    @Override
    public void onModuleInvalid() {
        GTCEu.LOGGER.info("onModuleInvalid. IsClient: {}", getLevel().isClientSide);
    }

    @Override
    public void onModuleUpdate() {
        GTCEu.LOGGER.info("onModuleUpdate. IsClient: {}", getLevel().isClientSide);
    }

    @Override
    public void notifyModules() {
        for (IMultiblockModule module : getModules()) {
            module.onBaseUpdate();
        }
    }

    /// ============================================================
    /// =================== MODULES MANAGEMENT =====================
    /// ============================================================

    @Override
    public final void addModule(IMultiblockModule module) {
        modulesPoss.add(module.getPos());
        moduleMachines.add(module);
    }

    @Override
    public final void removeModule(IMultiblockModule module) {
        modulesPoss.remove(module.getPos());
        moduleMachines.remove(module);
    }

    @Override
    public final int getModuleCount() {
        return modulesPoss.size();
    }

    public final void setModules(List<BlockPos> posList) {
        modulesResolved = true;
        var level = getLevel();
        if (level == null || posList.isEmpty()) moduleMachines.clear();
        else {
            modulesPoss.clear();
            moduleMachines.clear();
            for (var pos : posList) {
                if (MetaMachine.getMachine(level, pos) instanceof IMultiblockModule module) {
                    module.addBase(this);
                    modulesPoss.add(pos);
                    moduleMachines.add(module);
                }
            }
        }
    }

    public final Set<IMultiblockModule> getModules() {
        if (!modulesResolved) setModules(new ArrayList<>(modulesPoss));
        return Collections.unmodifiableSet(moduleMachines);
    }

    @Override
    public List<IRecipeHandler<?>> getCapabilities(IO io, RecipeCapability<?> cap) {
        return getCapabilitiesFlat(io, cap);
    }

    @Override
    public boolean isWorking() {
        return super.isWorkingEnabled();
    }
}
