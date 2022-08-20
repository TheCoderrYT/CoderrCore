package de.coderr.core.listener;

import de.coderr.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class StopReloadListener implements Listener
{
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event)
    {
        if (event.getPlayer().isOp()) {
            if (event.getMessage().contains("/reload")) {
                event.setCancelled(true);
                kickPlayers(Main.themecolor + "Der Server startet nun neu.\n" + Main.themecolor + "Sie können in wenigen Sekunden wieder den Server betreten.");
                Bukkit.dispatchCommand(event.getPlayer(),event.getMessage().replaceFirst("/",""));
            } else if (event.getMessage().contains("/stop") || event.getMessage().contains("/restart")) {
                event.setCancelled(true);
                Main.worldManager.onDisable();
                Bukkit.dispatchCommand(event.getPlayer(),event.getMessage().replaceFirst("/",""));
            }
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        if (event.getCommand().contains("reload")) {
            event.setCancelled(true);
            kickPlayers(Main.themecolor + "Der Server startet nun neu.\n" + Main.themecolor + "Sie können in wenigen Sekunden wieder den Server betreten.");
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"reload");
        } else if (event.getCommand().contains("stop") || event.getCommand().contains("restart")) {
            event.setCancelled(true);
            Main.worldManager.onDisable();
            Bukkit.dispatchCommand(event.getSender(),event.getCommand());
        }
    }

    private void kickPlayers(String message)
    {
        Main.worldManager.onDisable();
        for (Player a : Bukkit.getOnlinePlayers())
        {
            a.kickPlayer(message);
        }
    }
}
