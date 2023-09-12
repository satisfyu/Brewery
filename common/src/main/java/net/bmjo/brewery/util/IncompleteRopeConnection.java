package net.bmjo.brewery.util;

import net.bmjo.brewery.entity.HopRopeKnotEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class IncompleteRopeConnection {
    public final HopRopeKnotEntity from;

    public final int toId;

    private boolean alive = true;

    public IncompleteRopeConnection(HopRopeKnotEntity from, int toId) {
        this.from = from;
        this.toId = toId;
    }

    public boolean tryCompleteOrRemove() {
        if (isDead()) return true;
        Entity secondary = from.getLevel().getEntity(toId);
        if (secondary == null) return false;
        HopRopeConnection.create(from, secondary);
        return true;
    }

    public boolean isDead() {
        return !alive || this.from.isRemoved();
    }

    public void destroy() {
        if (!alive) return;
        this.alive = false;
        // Can't drop items on the client I guess
    }
}
