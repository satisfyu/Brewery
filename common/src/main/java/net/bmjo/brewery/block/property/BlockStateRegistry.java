package net.bmjo.brewery.block.property;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BlockStateRegistry {
    public static final BooleanProperty COMPLETE = BooleanProperty.create("complete");
    public static final EnumProperty<Liquid> LIQUID = EnumProperty.create("liquid", Liquid.class);
    public static final EnumProperty<Heat> HEAT = EnumProperty.create("heat", Heat.class);
    public static final BooleanProperty WHISTLE = BooleanProperty.create("whistle");
    public static final BooleanProperty TIME = BooleanProperty.create("time");
}
