package com.gmail.subnokoii78.thunt;

import com.gmail.subnokoii78.gpcore.GPCore;
import com.gmail.subnokoii78.gpcore.events.EventTypes;
import com.gmail.subnokoii78.gpcore.files.PluginConfigLoader;
import com.gmail.subnokoii78.thunt.commands.ConfigCommand;
import com.gmail.subnokoii78.thunt.commands.TrackerCommand;
import com.gmail.subnokoii78.thunt.compass.CompassEventHandler;
import com.gmail.subnokoii78.thunt.compass.PortalEventTracker;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class THunt extends JavaPlugin {
    @Override
    public void onEnable() {
        GPCore.initialize(this, null, getConfigFilePath(), DEFAULT_CONFIG_RESOURCE_PATH);

        GPCore.events.register(EventTypes.PLUGIN_CONFIG_UPDATE, event -> {
            Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        });

        final PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(CompassEventHandler.INSTANCE, this);
        pluginManager.registerEvents(PortalEventTracker.INSTANCE, this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands registrar = event.registrar();
            TrackerCommand.TRACKER_COMMAND.register(registrar);
            ConfigCommand.CONFIG_COMMAND.register(registrar);
        });

        getComponentLogger().info(Component.text("プラグイン THunt が正常に有効化されました").color(NamedTextColor.GREEN));
    }

    @Override
    public void onDisable() {
        getComponentLogger().info(Component.text("プラグイン Thunt が停止しました"));
    }

    public static final String CONFIG_TRACK_LAST_USED_PORTAL = "track_last_used_portal";

    public static final String CONFIG_JOIN_TO_GIVE_COMPASS = "join_to_give_compass";

    public static final String CONFIG_REQUIRE_OP_TO_USE_TRACKER = "require_op_to_use_tracker";

    public String getConfigFilePath() {
        return getDataPath() + "/config.json";
    }

    public static final String DEFAULT_CONFIG_RESOURCE_PATH = "/default_config.json";
}
