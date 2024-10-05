package me.cooleg.lagswitch;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {

    private final PlayerLagManager lagManager;

    public PlayerListeners(PlayerLagManager lagManager) {
        this.lagManager = lagManager;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        lagManager.playerJoin(event.getPlayer());
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        lagManager.playerLeave(event.getPlayer());
    }

}
