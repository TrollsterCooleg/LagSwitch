package me.cooleg.lagswitch;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LongArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LagSwitch extends JavaPlugin {

    public static final Logger LOGGER = LoggerFactory.getLogger(LagSwitch.class);
    private PlayerLagManager manager;

    @Override
    public void onEnable() {
        manager = new PlayerLagManager();
        manager.reAddHandlers();

        Bukkit.getPluginManager().registerEvents(new PlayerListeners(manager), this);
        registerCommands();
    }

    @Override
    public void onDisable() {
        manager.clearAllHandlers();
    }

    private void registerCommands() {
        new CommandAPICommand("setdelay")
                .withArguments(new PlayerArgument("player"), new LongArgument("ms"))
                .withPermission("lagswitch.commands")
                .executes(((commandSender, commandArguments) -> {
                    Player target = (Player) commandArguments.get("player");
                    long ms = (Long) commandArguments.get("ms");
                    manager.setAddedPing(target, ms);

                    commandSender.sendMessage(Component.text("Set target's ping to " + ms).color(NamedTextColor.GREEN));
                })).register();
    }
}
