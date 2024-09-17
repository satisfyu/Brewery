package net.satisfy.brewery.block;

import com.mojang.serialization.MapCodec;
import de.cristelknight.doapi.common.block.CabinetBlock;
import de.cristelknight.doapi.common.registry.DoApiSoundEventRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.BaseEntityBlock;
import org.jetbrains.annotations.NotNull;

public class CabinetBlockImp extends CabinetBlock {
    public static final MapCodec<CabinetBlockImp> CODEC = simpleCodec(CabinetBlockImp::new);

    public CabinetBlockImp(Properties settings) {
        super(settings, DoApiSoundEventRegistry.CABINET_OPEN, DoApiSoundEventRegistry.CABINET_CLOSE);
    }

    public CabinetBlockImp(Properties properties, RegistrySupplier<SoundEvent> cabinetOpen, RegistrySupplier<SoundEvent> cabinetClose) {
        super(properties, cabinetOpen, cabinetClose);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
