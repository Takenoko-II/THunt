package com.gmail.subnokoii78.thunt.commands;

import com.gmail.subnokoii78.gpcore.commands.AbstractCommand;
import com.gmail.subnokoii78.thunt.compass.HunterCompassManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

@NullMarked
public class TrackerCommand extends AbstractCommand {
    private TrackerCommand() {}

    @Override
    protected LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("tracker")
            .executes(this::run)
            .build();
    }

    @Override
    protected Set<String> getAliases() {
        return Set.of("manhunt", "compass", "thunt", "track", "hunt", "hunter");
    }

    private int run(CommandContext<CommandSourceStack> context) {
        final Entity executor = context.getSource().getExecutor();

        if (executor == null) {
            return failure(context.getSource(), new IllegalStateException(
                "エンティティ以外からの実行は無効です; execute as 等を使用してください"
            ));
        }
        else if (executor instanceof Player player) {
            return activate(context.getSource(), player);
        }
        else {
            return failure(context.getSource(), new IllegalStateException(
                "実行者がプレイヤーではありません"
            ));
        }
    }

    private int activate(CommandSourceStack stack, Player player) {
        final Inventory inventory = player.getInventory();

        boolean hasCompass = false;

        for (int i = 0; i < inventory.getSize(); i++) {
            final ItemStack item = inventory.getItem(i);
            if (item == null) continue;

            if (HunterCompassManager.isCompass(item)) {
                inventory.setItem(i, null);
                hasCompass = true;
            }
        }

        if (hasCompass) {
            stack.getSender().sendMessage(Component.text(
                player.getName() + " のインベントリからトラッカーコンパスを削除しました"
            ).color(NamedTextColor.RED));
            return 1;
        }
        else if (inventory.firstEmpty() == -1) {
            return failure(stack, new IllegalStateException(
                "インベントリがいっぱいのためコンパスを渡せません"
            ));
        }
        else {
            inventory.addItem(HunterCompassManager.createCompass());
            stack.getSender().sendMessage(Component.text(
                player.getName() + " にトラッカーコンパスを渡しました"
            ).color(NamedTextColor.GREEN));
            return 1;
        }
    }

    @Override
    protected String getDescription() {
        return "トラッカーコンパスを取得します(既に所持している場合削除します)";
    }

    public static final TrackerCommand TRACKER_COMMAND = new TrackerCommand();
}
