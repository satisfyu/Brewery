package net.bmjo.brewery.registry;

import net.bmjo.brewery.block.property.BrewMaterial;
import net.bmjo.brewery.block.property.Heat;
import net.bmjo.brewery.block.property.LineConnectingType;
import net.bmjo.brewery.block.property.Liquid;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BlockStateRegistry {
    public static final EnumProperty<BrewMaterial> MATERIAL = EnumProperty.create("material", BrewMaterial.class);
    public static final EnumProperty<Liquid> LIQUID = EnumProperty.create("liquid", Liquid.class);
    public static final EnumProperty<Heat> HEAT = EnumProperty.create("heat", Heat.class);
    public static final BooleanProperty WHISTLE = BooleanProperty.create("whistle");
    public static final BooleanProperty TIME = BooleanProperty.create("time");
    public static final EnumProperty<LineConnectingType> LINE_CONNECTING_TYPE = EnumProperty.create("type", LineConnectingType.class);
}
