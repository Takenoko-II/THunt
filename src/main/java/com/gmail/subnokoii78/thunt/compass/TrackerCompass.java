package com.gmail.subnokoii78.thunt.compass;

import com.gmail.subnokoii78.gpcore.GPCore;
import com.gmail.subnokoii78.gpcore.itemstack.ItemStackBuilder;
import com.gmail.subnokoii78.gpcore.itemstack.ItemStackCustomDataAccess;
import com.gmail.subnokoii78.gpcore.ui.container.ContainerInteraction;
import com.gmail.subnokoii78.gpcore.ui.container.ItemButton;
import com.gmail.subnokoii78.gpcore.ui.container.ItemButtonClickSound;
import com.gmail.subnokoii78.thunt.THunt;
import com.gmail.takenokoii78.json.JSONValueTypes;
import com.gmail.takenokoii78.json.values.JSONObject;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class TrackerCompass {
    private static final MojangsonPath TRACKER_ROOT = MojangsonPath.of("tracker");

    private static final MojangsonPath TRACKED_ENTITY = MojangsonPath.of("tracker.tracked_entity");

    private final ItemStack itemStack;

    private final World using;

    private TrackerCompass(ItemStack itemStack, World using) {
        this.itemStack = itemStack;
        this.using = using;
    }

    public ContainerInteraction createUi() {
        final Set<Player> entities = Set.copyOf(Bukkit.getOnlinePlayers());

        final int columns = (entities.size() - 1) / 9 + 3;

        final ItemButton pane = ItemButton.item(Material.GRAY_STAINED_GLASS_PANE).hideTooltip();

        final ContainerInteraction interaction = new ContainerInteraction(Component.text("Target Selector"), columns)
            .fillRow(0, pane.copy())
            .fillRow(columns - 1, pane.copy())
            .fillColumn(0, pane.copy())
            .fillColumn(8, pane.copy());

        for (final Entity entity : entities) {
            interaction.add(
                ItemButton.playerHead()
                    .player(entity.getName())
                    .name(Component.text(
                        entity.getName()
                    ).color(NamedTextColor.GOLD))
                    .clickSound(ItemButtonClickSound.BASIC)
                    .onClick(event -> {
                        final TrackerUseResult result = track(entity);
                        event.getPlayer().sendMessage(result.getMessage());
                        event.close();
                    })
            );
        }

        while (interaction.hasEmptySlot()) {
            interaction.add(ItemButton.item(Material.LIGHT_GRAY_STAINED_GLASS_PANE).hideTooltip());
        }

        return interaction;
    }

    public boolean isSetTrackedTarget() {
        final MojangsonCompound nbt = ItemStackCustomDataAccess.of(itemStack).read();
        return nbt.has(TRACKED_ENTITY);
    }

    public @Nullable Entity getTrackedEntity() {
        final MojangsonCompound nbt = ItemStackCustomDataAccess.of(itemStack).read();

        if (!nbt.has(TRACKED_ENTITY)) {
            return null;
        }

        final int[] uuidArr = nbt.get(TRACKED_ENTITY, MojangsonValueTypes.INT_ARRAY).toArray();
        return Bukkit.getEntity(UUIDUtil.uuidFromIntArray(uuidArr));
    }

    private TrackerUseResult track(Entity entity) {
        if (!entity.isValid()) return TrackerUseResult.ALREADY_UNLOADED;

        final ItemStackCustomDataAccess access = ItemStackCustomDataAccess.of(itemStack);

        final MojangsonCompound data = access.read();

        final TrackerUseResult successResult;
        if (data.has(TRACKED_ENTITY)) {
            successResult = TrackerUseResult.INFORMATION_UPDATED;
        }
        else {
            successResult = TrackerUseResult.TARGET_SET;
        }

        data.set(TRACKED_ENTITY, UUIDUtil.uuidToIntArray(entity.getUniqueId()));
        access.write(data);

        final Location location;

        if (entity.getWorld().equals(using)) {
            location = entity.getLocation();
        }
        else {
            final JSONObject config = GPCore.getPluginConfigLoader().get();
            final boolean trackLastUsedPortal = config.get(THunt.CONFIG_TRACK_LAST_USED_PORTAL, JSONValueTypes.BOOLEAN).getValue();

            if (trackLastUsedPortal) {
                location = PortalEventTracker.getPortal(entity);

                if (location == null) {
                    return TrackerUseResult.PORTAL_NOT_FOUND;
                }
            }
            else {
                return TrackerUseResult.ANOTHER_DIMENSION;
            }
        }

        itemStack.setData(
            DataComponentTypes.LODESTONE_TRACKER,
            LodestoneTracker.lodestoneTracker()
                .tracked(false)
                .location(location)
        );

        final ItemLore lore = itemStack.getData(DataComponentTypes.LORE);
        final List<Component> list = new ArrayList<>(lore.lines());
        list.removeLast();
        list.add(
            Component.text("tracked-target: " + entity.getName())
            .decoration(TextDecoration.ITALIC, false)
            .color(NamedTextColor.DARK_PURPLE)
        );
        itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(list));

        return successResult;
    }

    public TrackerUseResult updateTracking() {
        if (isSetTrackedTarget()) {
            final Entity entity = getTrackedEntity();

            if (entity == null) return TrackerUseResult.ALREADY_UNLOADED;

            return track(entity);
        }
        else {
            return TrackerUseResult.TARGET_UNSET;
        }
    }

    public static ItemStack createCompass() {
        return new ItemStackBuilder(Material.COMPASS)
            .customName(
                Component.text("Tracker Compass")
                    .decoration(TextDecoration.ITALIC, false)
                    .color(NamedTextColor.GREEN)
            )
            .lore(
                Component.text("left-click: open target selector")
                    .decoration(TextDecoration.ITALIC, false)
                    .color(NamedTextColor.GRAY)
            )
            .lore(
                Component.text("right-click: update tracking")
                    .decoration(TextDecoration.ITALIC, false)
                    .color(NamedTextColor.GRAY)
            )
            .lore(
                Component.empty()
            )
            .lore(
                Component.text("tracked-target: none")
                    .decoration(TextDecoration.ITALIC, false)
                    .color(NamedTextColor.DARK_PURPLE)
            )
            .customData(TRACKER_ROOT, new MojangsonCompound())
            .build();
    }

    public static boolean isCompass(ItemStack itemStack) {
        final MojangsonCompound compound = ItemStackCustomDataAccess.of(itemStack).read();
        if (!compound.has(TRACKER_ROOT)) return false;
        return compound.getTypeOf(TRACKER_ROOT).equals(MojangsonValueTypes.COMPOUND);
    }

    public static TrackerCompass of(ItemStack itemStack, World using) {
        if (!isCompass(itemStack)) {
            throw new IllegalArgumentException("トラッカーコンパスでないアイテムが引数に渡されました");
        }
        return new TrackerCompass(itemStack, using);
    }

    public enum TrackerUseResult {
        TARGET_SET(true, "追跡対象が正常に設定されました"),

        TARGET_UNSET(false, "追跡対象が設定されていません"),

        INFORMATION_UPDATED(true, "追跡対象の位置情報が更新されました"),

        ALREADY_UNLOADED(false, "追跡対象は既にアンロードされています"),

        PORTAL_NOT_FOUND(false, "追跡対象が最後に使用したポータルが見つかりません"),

        ANOTHER_DIMENSION(false, "追跡対象の位置と現在位置ではディメンションが異なります");

        private final boolean successful;

        private final String message;

        TrackerUseResult(boolean successful, String message) {
            this.successful = successful;
            this.message = message;
        }

        public Component getMessage() {
            return Component.text(message).color(successful ? NamedTextColor.GREEN : NamedTextColor.RED);
        }

        public boolean isSuccess() {
            return successful;
        }
    }
}
