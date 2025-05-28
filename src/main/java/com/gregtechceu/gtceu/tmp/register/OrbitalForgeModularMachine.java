package com.gregtechceu.gtceu.tmp.register;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.*;
import com.gregtechceu.gtceu.tmp.modular.CoilWorkableElectricModularMultiblockMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static com.gregtechceu.gtceu.common.registry.GTRegistration.REGISTRATE;

//spotless: off
public class OrbitalForgeModularMachine {

    public final static MultiblockMachineDefinition ORBITAL_TEMPERING_FORGE = REGISTRATE.multiblock(
                    "orbital_tempering_forge", OrbitalForgeModularMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(GTRecipeTypes.ORBITAL_FORGE)
            .recipeModifiers(GTRecipeModifiers.PARALLEL_HATCH,
                    GTRecipeModifiers::ebfModuleOverclock)
            .appearanceBlock(GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("##QQQ##", "##QQQ##", "###Q###", "#######", "#######", "#######", "#######", "#######", "###Q###", "##QQQ##", "##QQQ##")
                    .aisle("#QQQQQ#", "#QQSQQ#", "#FQQQF#", "#FQ#QF#", "#F###F#", "#F###F#", "#F###F#", "#FQ#QF#", "#FQQQF#", "#QQSQQ#", "#QQQQQ#")
                    .aisle("QQQQQQQ", "QQSSSQQ", "#QSSSQ#", "##HGH##", "##HGH##", "##HGH##", "##HGH##", "#QHGHQ#", "#QSSSQ#", "QQSSSQQ", "QQQQQQQ")
                    .aisle("QQQQQQQ", "QSSSSSQ", "QQSSSQQ", "##GSG##", "##GSG##", "##GSG##", "##GSG##", "##GSG##", "QQSSSQQ", "QSSISSQ", "QQQUQQQ")
                    .aisle("QQQQQQQ", "QQSSSQQ", "#QSSSQ#", "##HGH##", "##HGH##", "##HGH##", "##HGH##", "#QHGHQ#", "#QSSSQ#", "QQSSSQQ", "QQQQQQQ")
                    .aisle("#QQQQQ#", "#QQSQQ#", "#FQQQF#", "#FQ#QF#", "#F###F#", "#F###F#", "#F###F#", "#FQ#QF#", "#FQQQF#", "#QQSQQ#", "#QQQQQ#")
                    .aisle("##QQQ##", "##QCQ##", "###Q###", "#######", "#######", "#######", "#######", "#######", "###Q###", "##QQQ##", "##QQQ##")
                    .where('#', any())
                    .where("C", controller(blocks(definition.getBlock())))
                    .where('F', blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.NaquadahAlloy)))
                    .where('S', heatingCoils())
                    .where('H', blocks(GTBlocks.CASING_BRONZE_PIPE.get()))
                    .where('G', blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                    .where('I', blocks(GTMachines.MODULE_CONNECTOR.get()))
                    .where('U', blocks(GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING.get())
                            .or(blocks(OrbitalForgeEBFModule.ORBITAL_TEMPERING_FORGE_EBF_MODULE.get())))
                    .where('Q', blocks(GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING.get())
                            .or(abilities(PartAbility.IMPORT_FLUIDS))
                            .or(abilities(PartAbility.EXPORT_FLUIDS))
                            .or(abilities(PartAbility.IMPORT_ITEMS))
                            .or(abilities(PartAbility.EXPORT_ITEMS))
                            .or(abilities(PartAbility.INPUT_ENERGY))
                            .or(abilities(PartAbility.MAINTENANCE))
                            .or(abilities(PartAbility.DATA_ACCESS))
                            .or(abilities(PartAbility.COMPUTATION_DATA_RECEPTION))
                            .or(abilities(PartAbility.OPTICAL_DATA_RECEPTION))
                            .or(abilities(PartAbility.PARALLEL_HATCH))
                            .or(abilities(PartAbility.INPUT_LASER))
                            .or(abilities(PartAbility.INPUT_ENERGY)))
                    .build())
            .workableCasingRenderer(GTCEu.id("block/casings/gcym/large_scale_assembling_casing"),
                    GTCEu.id("block/multiblock/gcym/large_assembler"))
            .additionalDisplay((controller, components) -> {
                if (controller instanceof CoilWorkableElectricModularMultiblockMachine coilMachine && controller.isFormed()) {
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
