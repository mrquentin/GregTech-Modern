package com.gregtechceu.gtceu.common.machine.multiblock;

import java.util.HashMap;
import java.util.UUID;

public abstract class GlobalWirelessVariableStorage {
    // --------------------- NEVER access these maps! Use the methods provided! ---------------------

    // Global wireless data stick map
    public static HashMap<UUID, WirelessCwuStore> GlobalWirelessCwu = new HashMap<>(20, 0.9f);
}
