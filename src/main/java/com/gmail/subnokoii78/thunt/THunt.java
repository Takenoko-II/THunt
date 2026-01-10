package com.gmail.subnokoii78.thunt;

import com.gmail.subnokoii78.gpcore.GPCore;
import com.gmail.subnokoii78.gpcore.events.Events;
import com.gmail.subnokoii78.thunt.commands.TrackerCommand;
import com.gmail.subnokoii78.thunt.compass.HunterEventListener;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class THunt extends JavaPlugin {
    private final THuntPluginBootstrap bootstrap;

    THunt(THuntPluginBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void onEnable() {
        GPCore.initialize(this, bootstrap, getConfigFilePath(), DEFAULT_CONFIG_RESOURCE_PATH);

        getServer().getPluginManager().registerEvents(HunterEventListener.INSTANCE, this);

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

    public static final String JOIN_TO_GIVE = "join_to_give";

    public String getConfigFilePath() {
        return getDataPath() + "/config.json";
    }

    public static final String DEFAULT_CONFIG_RESOURCE_PATH = "/default_config.json";
}
