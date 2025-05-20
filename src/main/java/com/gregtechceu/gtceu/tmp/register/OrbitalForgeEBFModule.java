package com.gregtechceu.gtceu.tmp.register;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.*;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static com.gregtechceu.gtceu.common.registry.GTRegistration.REGISTRATE;

public class OrbitalForgeEBFModule {

    public final static MultiblockMachineDefinition ORBITAL_TEMPERING_FORGE_EBF_MODULE = REGISTRATE.multiblock(
            "orbital_tempering_forge_ebf_module", OrbitalForgeModule::new)
            .rotationState(RotationState.ALL)
            .recipeType(GTRecipeTypes.ORBITAL_FORGE)
            .recipeModifiers(GTRecipeModifiers.PARALLEL_HATCH,
                    GTRecipeModifiers::ebfModuleOverclock)
            .appearanceBlock(GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAA", "BBB")
                    .aisle("ACA", "BDB")
                    .aisle("AAA", "BBB")
                    .where("D", controller(blocks(definition.getBlock())))
                    .where('C', blocks(GTMachines.MODULE_CONNECTOR.get()))
                    .where('A', blocks(GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING.get()))
                    .where('B', blocks(GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING.get())
                            .or(abilities(PartAbility.IMPORT_FLUIDS))
                            .or(abilities(PartAbility.EXPORT_FLUIDS))
                            .or(abilities(PartAbility.IMPORT_ITEMS))
                            .or(abilities(PartAbility.EXPORT_ITEMS))
                            .or(abilities(PartAbility.INPUT_ENERGY))
                            .or(abilities(PartAbility.COMPUTATION_DATA_RECEPTION).setMaxGlobalLimited(1, 1))
                            .or(abilities(PartAbility.DATA_ACCESS, PartAbility.OPTICAL_DATA_RECEPTION)
                                    .setMaxGlobalLimited(1, 1))
                            .or(abilities(PartAbility.PARALLEL_HATCH)
                                    .setExactLimit(1)))
                    .build())
            .workableCasingRenderer(GTCEu.id("block/casings/gcym/large_scale_assembling_casing"),
                    GTCEu.id("block/multiblock/gcym/large_assembler"))
            .additionalDisplay((controller, components) -> {
                if (controller instanceof OrbitalForgeModule coilMachine && controller.isFormed()) {
                    components.add(Component.translatable("gtceu.multiblock.blast_furnace.max_temperature",
                            Component
                                    .translatable(
                                            FormattingUtil
                                                    .formatNumbers(coilMachine.getCoilType().getCoilTemperature() +
                                                            100L * Math.max(0, coilMachine.getTier() - GTValues.MV)) +
                                                    "K")
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.RED))));
                }
            })
            .register();
    public static void init() {}
}
