package com.gregtechceu.gtceu.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.IOpticalComputationProvider;
import lombok.Getter;

import java.util.UUID;

import static com.gregtechceu.gtceu.common.machine.multiblock.GlobalWirelessVariableStorage.GlobalWirelessCwu;

public class WirelessCwuStore {

    @Getter
    private IOpticalComputationProvider wirelessSource;

    public static WirelessCwuStore getWirelessCwuStore(UUID uuid) {
        GlobalWirelessCwu.computeIfAbsent(uuid, k -> new WirelessCwuStore());
        return GlobalWirelessCwu.get(uuid);
    }

    public static void setWirelessSource(UUID uuid, IOpticalComputationProvider wirelessSource) {
        var dataStore = getWirelessCwuStore(uuid);
        dataStore.wirelessSource = wirelessSource;
        GlobalWirelessCwu.put(uuid, dataStore);
    }

    public static void resetWirelessSource(UUID uuid) {
        var dataStore = getWirelessCwuStore(uuid);
        dataStore.wirelessSource = null;
        GlobalWirelessCwu.put(uuid, dataStore);
    }
}
