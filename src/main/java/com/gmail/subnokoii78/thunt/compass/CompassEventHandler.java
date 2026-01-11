package com.gmail.subnokoii78.thunt.compass;

import com.gmail.subnokoii78.gpcore.GPCore;
import com.gmail.subnokoii78.gpcore.events.EventTypes;
import com.gmail.subnokoii78.gpcore.events.PlayerClickEvent;
import com.gmail.subnokoii78.gpcore.ui.container.ContainerInteraction;
import com.gmail.subnokoii78.thunt.THunt;
import com.gmail.takenokoii78.json.JSONValueTypes;
import com.gmail.takenokoii78.json.values.JSONObject;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class CompassEventHandler implements Listener {
    private CompassEventHandler() {
        GPCore.events.register(EventTypes.PLAYER_CLICK, this::onClick);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (TrackerCompass.isCompass(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!event.getKeepInventory()) {
            final List<ItemStack> drops = event.getDrops();
            final List<ItemStack> itemsToKeep = event.getItemsToKeep();
            final List<ItemStack> compasses = new ArrayList<>();

            for (final ItemStack itemStack : drops) {
                if (TrackerCompass.isCompass(itemStack)) {
                    compasses.add(itemStack);
                }
            }

            for (ItemStack compass : compasses) {
                drops.remove(compass);
                itemsToKeep.add(compass);
            }
        }
    }

    public void onClick(PlayerClickEvent event) {
        if (!event.hasItem()) return;
        if (!TrackerCompass.isCompass(event.getItem())) return;

        final TrackerCompass manager = TrackerCompass.of(event.getItem(), event.getPlayer().getWorld());
        event.cancel();

        switch (event.getClick()) {
            case LEFT -> {
                final ContainerInteraction interaction = manager.createUi();
                interaction.open(event.getPlayer());
            }
            case RIGHT -> {
                final TrackerCompass.TrackerUseResult result = manager.updateTracking();
                if (result.isSuccess()) {
                    event.getPlayer().sendMessage(result.getMessage());
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 5f, 2f);
                }
                else {
                    event.getPlayer().sendMessage(result.getMessage());
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 5f, 1f);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final JSONObject config = GPCore.getPluginConfigLoader().get();

        final boolean joinToGive = config.get(THunt.CONFIG_JOIN_TO_GIVE_COMPASS, JSONValueTypes.BOOLEAN).getValue();

        if (joinToGive) {
            final Inventory inventory = event.getPlayer().getInventory();

            for (int i = 0; i < inventory.getSize(); i++) {
                final ItemStack item = inventory.getItem(i);
                if (item == null) continue;

                if (TrackerCompass.isCompass(item)) {
                    return;
                }
            }

            inventory.addItem(TrackerCompass.createCompass());
        }
    }

    public static final CompassEventHandler INSTANCE = new CompassEventHandler();
}
