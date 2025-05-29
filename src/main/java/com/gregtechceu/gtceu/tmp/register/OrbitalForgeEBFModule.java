package com.gregtechceu.gtceu.tmp.register;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.*;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.ibm.icu.text.MessagePattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static com.gregtechceu.gtceu.common.registry.GTRegistration.REGISTRATE;

public class OrbitalForgeEBFModule {

    public final static MultiblockMachineDefinition ORBITAL_TEMPERING_FORGE_EBF_MODULE = REGISTRATE.multiblock(
            "orbital_tempering_forge_ebf_module", OrbitalForgeModule::new)
            .rotationState(RotationState.Y_AXIS)
            .recipeType(GTRecipeTypes.BLAST_RECIPES)
            .recipeModifiers(GTRecipeModifiers.PARALLEL_HATCH,
                    GTRecipeModifiers::ebfModuleOverclock)
            .appearanceBlock(GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("###", "#C#", "###")
                    .aisle("BBB", "BDB", "BBB")
                    .where("D", controller(blocks(definition.getBlock())))
                    .where('C', blocks(GTMachines.MODULE_CONNECTOR.get()))
                    .where('#', any())
                    .where('B', blocks(GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING.get())
                            .or(abilities(PartAbility.PARALLEL_HATCH))
                            .or(abilities(PartAbility.IMPORT_ITEMS)))
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
