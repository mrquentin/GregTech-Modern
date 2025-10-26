package com.gregtechceu.gtceu.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.capability.IOpticalComputationProvider;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableComputationContainer;
import com.gregtechceu.gtceu.common.machine.multiblock.WirelessCwuStore;
import com.gregtechceu.gtceu.common.machine.owner.FTBOwner;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class WirelessComputationReceiverHatchPartMachine extends MultiblockPartMachine{

    protected NotifiableComputationContainer computationContainer;

    public WirelessComputationReceiverHatchPartMachine(IMachineBlockEntity holder) {
        super(holder);
        this.computationContainer = new WirelessComputationContainer(this);
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return false;
    }

    @Override
    public boolean canShared() {
        return false;
    }

    private static class WirelessComputationContainer extends NotifiableComputationContainer {
        public WirelessComputationContainer(MetaMachine machine) {
            super(machine, IO.IN, true);
        }

        @Override
        public boolean canBridge() {
            return super.canBridge();
        }

        @Override
        public boolean canBridge(@NotNull Collection<IOpticalComputationProvider> seen) {
            seen.add(this);
            return true;
        }

        @Override
        public int getMaxCWUt() {
            return super.getMaxCWUt();
        }

        @Override
        public int getMaxCWUt(@NotNull Collection<IOpticalComputationProvider> seen) {
            return super.getMaxCWUt(seen);
        }

        @Override
        public int requestCWUt(int cwut, boolean simulate) {
            return super.requestCWUt(cwut, simulate);
        }

        @Override
        public int requestCWUt(int cwut, boolean simulate, @NotNull Collection<IOpticalComputationProvider> seen) {
            return super.requestCWUt(cwut, simulate, seen);
        }

        @Override
        public @Nullable IOpticalComputationProvider getComputationProvider() {
            var team = ((FTBOwner) this.machine.getOwner()).getPlayerTeam(this.machine.getOwnerUUID());
            var uuid = team != null ? team.getTeamId() : this.machine.getOwnerUUID();
            return WirelessCwuStore.getWirelessCwuStore(uuid).getWirelessSource();
        }
    }
}
