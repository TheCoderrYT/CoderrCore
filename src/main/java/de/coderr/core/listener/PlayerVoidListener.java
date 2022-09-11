package de.coderr.core.listener;

import de.coderr.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerVoidListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        allowFlight(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerChangedWorldEvent event) {
        allowFlight(event.getPlayer());
    }

    private void allowFlight(Player p) {
        if (p.getWorld() == Bukkit.getWorld(Main.instance.getConfig().getString("world.lobby"))) {
            p.setAllowFlight(true);
        } else {
            p.setAllowFlight(false);
        }
    }
}
