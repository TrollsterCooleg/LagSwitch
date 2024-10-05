package me.cooleg.lagswitch;

import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PlayerLagManager {

    private final HashMap<UUID, LagPacketHandler> handlerMap = new HashMap<>();

    public void playerJoin(Player player) {
        LagPacketHandler handler = new LagPacketHandler();

        ((CraftPlayer) player).getHandle()
                .connection.connection
                .channel.pipeline()
                .addBefore("packet_handler", "LagSwitch", handler);


        handlerMap.put(player.getUniqueId(), handler);
        System.out.println(handlerMap);
    }

    public void playerLeave(Player player) {
        Channel channel = ((CraftPlayer) player)
                .getHandle().connection.connection.channel;

        channel.eventLoop().submit(() -> {
            try {
                channel.pipeline().remove("LagSwitch");
            } catch (NoSuchElementException | NullPointerException ignored) {}
        });

        handlerMap.remove(player.getUniqueId());
    }

    public void setAddedPing(Player player, long ping) {
        if (ping < 0) ping = 0;
        LagPacketHandler handler = handlerMap.get(player.getUniqueId());
        if (ping < handler.getDelay()) {
            handler.setDecreasingDelay(ping);
        } else {
            handler.setDelay(ping);
        }
    }

    public void reAddHandlers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            LagSwitch.LOGGER.warn("If you are seeing this message, you have done a sin and reloaded a packet-based plugin with players online!");
            LagSwitch.LOGGER.warn("These messages will be sent for each player online at the time of the reload, as repayment for your crimes.");
            LagSwitch.LOGGER.warn("If this plugin was active on any players, they will have experienced packet loss, and problems will probably arise.");
            playerJoin(player);
        }
    }

    public void clearAllHandlers() {
        for (UUID id : handlerMap.keySet()) {
            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;
            playerLeave(player);
        }
    }

}
