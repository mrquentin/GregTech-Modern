package com.gregtechceu.gtceu.tmp.modular;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.tmp.modular.interfaces.IModularMultiblock;
import com.gregtechceu.gtceu.tmp.modular.interfaces.IMultiblockModule;
import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WorkableMultiblockMachineModule extends WorkableMultiblockMachine implements IMultiblockModule {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            WorkableMultiblockMachineModule.class, WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    private final Set<BlockPos> baseMultiblockPoss = new ObjectOpenHashSet<>();
    private final Set<IModularMultiblock> baseMultiblocks = new ObjectOpenHashSet<>();
    private boolean baseMultiblockResolved = false;
    protected boolean areBasesWorking = false;

    @Getter
    protected boolean basesFormed = false;

    public WorkableMultiblockMachineModule(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void addBase(IModularMultiblock base) {
        baseMultiblockPoss.add(base.getPos());
        baseMultiblocks.add(base);
    }

    @Override
    public void removeBase(IModularMultiblock base) {
        baseMultiblockPoss.remove(base.getPos());
        baseMultiblocks.remove(base);
    }

    @Override
    public int getBaseCount() {
        return baseMultiblocks.size();
    }

    /**
     * This method is called when the module is added to a multiblock.
     * It can be used to add capabilities from the base multiblocks to this module.
     * @param capabilitiesToExtract The list of capabilities to extract from the base multiblocks.
     * empty by default
     */
    public void addCapabilitiesFromBase(List<IoRecipeCapability> capabilitiesToExtract) {}

    private void addBaseCapabilities(List<IoRecipeCapability> capabilitiesToExtract) {
        for (IModularMultiblock base : getBaseMultiBlocks()) {
            for (var ioCap : capabilitiesToExtract) {
                if (ioCap.io == IO.BOTH) {
                    addBaseIORecipeCapability(base, IO.IN, ioCap.cap);
                    addBaseIORecipeCapability(base, IO.OUT, ioCap.cap);
                } else {
                    addBaseIORecipeCapability(base, ioCap.io, ioCap.cap);
                }
            }
        }
    }

    private void addBaseIORecipeCapability(IModularMultiblock base, IO io, RecipeCapability<?> capability) {
        var handlerList = RecipeHandlerList.of(io, base.getCapabilities(io, capability));
        addHandlerList(handlerList);
        traitSubscriptions.add(handlerList.subscribe(recipeLogic::updateTickSubscription));
    }

    public static class IoRecipeCapability {
        protected final IO io;
        protected final RecipeCapability<?> cap;

        public IoRecipeCapability(IO io, RecipeCapability<?> cap) {
            this.io = io;
            this.cap = cap;
        }
    }

    public void setBaseMultiblocks(List<BlockPos> posList) {
        baseMultiblockResolved = true;
        var level = getLevel();
        if (level == null || posList.isEmpty()) baseMultiblocks.clear();
        else {
            baseMultiblockPoss.clear();
            baseMultiblocks.clear();
            for (var pos : posList) {
                if (MetaMachine.getMachine(level, pos) instanceof IModularMultiblock machine) {
                    machine.addModule(this);
                    baseMultiblockPoss.add(pos);
                    baseMultiblocks.add(machine);
                }
            }
        }
    }

    public List<IModularMultiblock> getBaseMultiBlocks() {
        if (!baseMultiblockResolved) setBaseMultiblocks(new ArrayList<>(baseMultiblockPoss));
        return new ArrayList<>(baseMultiblocks);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        Map<Long, IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap", Long2ObjectMaps::emptyMap);
        var poss = new ArrayList<BlockPos>();
        for (IMultiPart part : getParts()) {
            if (part instanceof ModuleConnectorPartMachine) {
                for (var controller : part.getControllers()) {
                    if (controller instanceof IModularMultiblock master) {
                        poss.add(master.getPos());
                    }
                }
            }
        }

        setBaseMultiblocks(poss);
        this.basesFormed = getBaseMultiBlocks().stream().allMatch(IModularMultiblock::isFormed);

        updateTraitSubscriptions();

        notifyBases();
    }

    private void updateTraitSubscriptions() {
        Map<Long, IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap", Long2ObjectMaps::emptyMap);

        traitSubscriptions.forEach(ISubscription::unsubscribe);
        traitSubscriptions.clear();

        for (IMultiPart part : getParts()) {
            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.BOTH);
            if (io == IO.NONE) continue;

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                if (!handlerList.isValid(io)) continue;
                this.addHandlerList(handlerList);
                traitSubscriptions.add(handlerList.subscribe(recipeLogic::updateTickSubscription));
            }
        }

        // Extract requested capabilities from base Multiblocks
        var capabilitiesToExtract = new ArrayList<IoRecipeCapability>();
        addCapabilitiesFromBase(capabilitiesToExtract);
        addBaseCapabilities(capabilitiesToExtract);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        var bases = getBaseMultiBlocks();
        if (!bases.isEmpty()) {
            for (var base : bases) {
                base.removeModule(this);
            }
        }
        this.baseMultiblockPoss.clear();
        this.baseMultiblocks.clear();
    }

    @Override
    public void onBaseFormed() {
        System.out.println("ModuleTest: Base formed notification received. IsClient: " + getLevel().isClientSide);
        this.basesFormed = getBaseMultiBlocks().stream().allMatch(IModularMultiblock::isFormed);
        updateTraitSubscriptions();
        this.recipeLogic.resetRecipeLogic();
    }

    @Override
    public void onBaseInvalid() {
        this.basesFormed = false;
        updateActiveBlocks(false);
        activeBlocks = null;
        capabilitiesProxy.clear();
        capabilitiesFlat.clear();
        traitSubscriptions.forEach(ISubscription::unsubscribe);
        traitSubscriptions.clear();
        recipeLogic.resetRecipeLogic();
    }

    @Override
    public void onBaseUpdate() {
        updateWorkingStatus();
        System.out.println("ModuleTest: Update notification received. IsClient: " + getLevel().isClientSide);
    }

    @Override
    public void notifyBases() {
        for (IModularMultiblock base : getBaseMultiBlocks()) {
            base.onModuleUpdate();
        }
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        if (this.areBasesWorking)
            super.setWorkingEnabled(isWorkingAllowed);
    }

    private void updateWorkingStatus() {
        this.areBasesWorking = getBaseMultiBlocks().stream()
                .allMatch(IModularMultiblock::isWorking);
        getRecipeLogic().setWorkingEnabled(this.areBasesWorking);
    }
}
