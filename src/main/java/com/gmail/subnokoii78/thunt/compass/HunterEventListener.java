package com.gmail.subnokoii78.thunt.compass;

import com.gmail.subnokoii78.gpcore.GPCore;
import com.gmail.subnokoii78.gpcore.events.EventTypes;
import com.gmail.subnokoii78.gpcore.events.PlayerClickEvent;
import com.gmail.subnokoii78.gpcore.ui.container.ContainerInteraction;
import com.gmail.subnokoii78.thunt.THunt;
import com.gmail.takenokoii78.json.JSONValueTypes;
import com.gmail.takenokoii78.json.values.JSONObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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
public class HunterEventListener implements Listener {
    private HunterEventListener() {
        THunt.events.register(EventTypes.PLAYER_CLICK, this::onClick);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (HunterCompassManager.isCompass(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final JSONObject config = GPCore.getPluginConfigLoader().get();

        final boolean joinToGive = config.has(THunt.JOIN_TO_GIVE) ? config.get(THunt.JOIN_TO_GIVE, JSONValueTypes.BOOLEAN).getValue() : false;

        if (joinToGive) {
            final Inventory inventory = event.getPlayer().getInventory();

            for (int i = 0; i < inventory.getSize(); i++) {
                final ItemStack item = inventory.getItem(i);
                if (item == null) continue;

                if (HunterCompassManager.isCompass(item)) {
                    return;
                }
            }

            inventory.addItem(HunterCompassManager.createCompass());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {

        }
        HunterCompassManager.setPortalPos(event.getPlayer(), event.getFrom());
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        HunterCompassManager.clearPortalPos(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!event.getKeepInventory()) {
            final List<ItemStack> list1 = event.getItemsToKeep();
            final List<ItemStack> list2 = event.getDrops();

            final List<ItemStack> compasses = new ArrayList<>();

            for (ItemStack itemStack : list1) {
                if (HunterCompassManager.isCompass(itemStack)) {
                    compasses.add(itemStack);
                }
            }

            for (ItemStack compass : compasses) {
                list1.remove(compass);
            }

            compasses.clear();

            for (ItemStack itemStack : list2) {
                if (HunterCompassManager.isCompass(itemStack)) {
                    compasses.add(itemStack);
                }
            }

            for (ItemStack compass : compasses) {
                list2.remove(compass);
            }

            event.getPlayer().getInventory().addItem(HunterCompassManager.createCompass());
        }
    }

    public void onClick(PlayerClickEvent event) {
        if (!event.hasItem()) return;
        if (!HunterCompassManager.isCompass(event.getItem())) return;

        final HunterCompassManager manager = HunterCompassManager.of(event.getItem());

        switch (event.getClick()) {
            case LEFT -> {
                final ContainerInteraction interaction = manager.createUi();
                interaction.open(event.getPlayer());
            }
            case RIGHT -> {
                if (manager.updateTracking()) {
                    event.getPlayer().sendMessage(Component.text("対象の位置情報を更新しました").color(NamedTextColor.GREEN));
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 5f, 2f);
                }
                else {
                    event.getPlayer().sendMessage(Component.text("追跡対象が指定されていないか、既に存在しません; 左クリックで設定してください").color(NamedTextColor.RED));
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 5f, 1f);
                }
            }
        }
    }

    public static final HunterEventListener INSTANCE = new HunterEventListener();
}
