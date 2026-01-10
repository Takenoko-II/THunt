package com.gmail.subnokoii78.thunt.compass;

import com.gmail.subnokoii78.gpcore.itemstack.ItemStackBuilder;
import com.gmail.subnokoii78.gpcore.itemstack.ItemStackCustomDataAccess;
import com.gmail.subnokoii78.gpcore.ui.container.ContainerInteraction;
import com.gmail.subnokoii78.gpcore.ui.container.ItemButton;
import com.gmail.subnokoii78.gpcore.ui.container.ItemButtonClickSound;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@NullMarked
public class HunterCompassManager {
    private static final MojangsonPath TRACKER_ROOT = MojangsonPath.of("tracker");

    private static final MojangsonPath TARGET = MojangsonPath.of("tracker.tracked_target");

    private final ItemStack itemStack;

    private HunterCompassManager(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ContainerInteraction createUi() {
        final Set<Entity> entities = new HashSet<>(Bukkit.getOnlinePlayers());
        final int columns = (entities.size() - 1) / 9 + 3;

        final ItemButton pane = ItemButton.item(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(""));

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
                        setTrackedEntity(entity);
                        event.close();
                    })
            );
        }

        while (interaction.hasEmptySlot()) {
            interaction.add(ItemButton.item(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(Component.text("")));
        }

        return interaction;
    }

    public @Nullable Player getTrackedEntity() {
        final MojangsonCompound nbt = ItemStackCustomDataAccess.of(itemStack).read();

        if (!nbt.has(TARGET)) {
            return null;
        }

        final int[] uuidArr = nbt.get(TARGET, MojangsonValueTypes.INT_ARRAY).toArray();
        return Bukkit.getPlayer(UUIDUtil.uuidFromIntArray(uuidArr));
    }

    private void setTrackedEntity(Entity entity) {
        itemStack.setData(
            DataComponentTypes.LODESTONE_TRACKER,
            LodestoneTracker.lodestoneTracker()
                .tracked(false)
                .location(entity.getLocation())
        );
        final ItemStackCustomDataAccess access = ItemStackCustomDataAccess.of(itemStack);
       final MojangsonCompound data = access.read();
       data.set(TARGET, UUIDUtil.uuidToIntArray(entity.getUniqueId()));
       access.write(data);
    }

    public boolean updateTracking() {
        final Player entity = getTrackedEntity();

        if (entity == null) return false;

        itemStack.setData(
            DataComponentTypes.LODESTONE_TRACKER,
            LodestoneTracker.lodestoneTracker()
                .tracked(false)
                .location(entity.getLocation())
        );

        return true;
    }

    public static ItemStack createCompass() {
        return new ItemStackBuilder(Material.COMPASS)
            .itemName(Component.text("Tracker Compass").color(NamedTextColor.GREEN))
            .customData(TRACKER_ROOT, new MojangsonCompound())
            .build();
    }

    public static boolean isCompass(ItemStack itemStack) {
        final MojangsonCompound compound = ItemStackCustomDataAccess.of(itemStack).read();
        if (!compound.has(TRACKER_ROOT)) return false;
        return compound.getTypeOf(TRACKER_ROOT).equals(MojangsonValueTypes.COMPOUND);
    }

    public static HunterCompassManager of(ItemStack itemStack) {
        if (!isCompass(itemStack)) {
            throw new IllegalArgumentException("トラッカーコンパスでないアイテムが引数に渡されました");
        }
        return new HunterCompassManager(itemStack);
    }
}
