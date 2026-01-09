package com.gmail.subnokoii78.thunt;

import com.gmail.subnokoii78.thunt.commands.TrackerCommand;
import com.gmail.subnokoii78.thunt.compass.HunterEventListener;
import com.gmail.subnokoii78.thunt.container.ContainerInteraction;
import com.gmail.subnokoii78.thunt.events.BukkitEventObserver;
import com.gmail.subnokoii78.thunt.events.Events;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class THunt extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(HunterEventListener.INSTANCE, this);
        getServer().getPluginManager().registerEvents(BukkitEventObserver.INSTANCE, this);
        getServer().getPluginManager().registerEvents(ContainerInteraction.ContainerEventObserver.INSTANCE, this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands registrar = event.registrar();
            TrackerCommand.TRACKER_COMMAND.register(registrar);
        });

        getComponentLogger().info(Component.text("プラグイン THunt が正常に有効化されました").color(NamedTextColor.GREEN));
    }

    @Override
    public void onDisable() {
        getComponentLogger().info(Component.text("プラグイン Thunt が停止しました"));
    }

    public static final Events events = new Events();

    public static final THunt INSTANCE = new THunt();

    public static final String JOIN_TO_GIVE = "join_to_give";
}
