package com.gmail.subnokoii78.thunt.compass;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class PortalEventTracker implements Listener {
    private PortalEventTracker() {}

    private final Map<Entity, Location> portals = new HashMap<>();

    @EventHandler
    public void onEntityTeleport(EntityTeleportEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (to == null) {
            portals.put(event.getEntity(), from);
        }
        else if (!from.getWorld().equals(to.getWorld())) {
            portals.put(event.getEntity(), from);
        }
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveEvent event) {
        // プレイヤー専用なら誤作動はなし
        portals.remove(event.getEntity());
    }

    public static @Nullable Location getPortal(Entity entity) {
        return INSTANCE.portals.get(entity);
    }

    public static final PortalEventTracker INSTANCE = new PortalEventTracker();
}
