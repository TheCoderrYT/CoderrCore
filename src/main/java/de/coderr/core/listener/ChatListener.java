package de.coderr.core.listener;

import de.coderr.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener
{
    @EventHandler
    public void onchat(AsyncPlayerChatEvent event)
    {
        Player p = event.getPlayer();

        event.setFormat(p.getDisplayName() + ChatColor.DARK_GRAY + " | " + Main.fontcolor + ChatColor.ITALIC + "%2$s");

        if (event.getMessage().contains("@"))
        {
            String[] msg = event.getMessage().split(" ");
            String name = msg[0].replace("@","");

            event.setCancelled(true);

            try
            {
                Player recipient = Bukkit.getPlayer(name);

                assert recipient != null;
                p.sendMessage(Main.themecolor + "Privat (" + recipient.getDisplayName() + Main.themecolor + ") " + Main.themecolor + "| " + Main.fontcolor + ChatColor.ITALIC + event.getMessage().replaceAll(msg[0] + " ", ""));

                recipient.sendMessage(Main.themecolor + "Privat (" + p.getDisplayName() + Main.themecolor + ") " + Main.themecolor + "| " + Main.fontcolor + ChatColor.ITALIC + event.getMessage().replaceAll(msg[0] + " ", ""));

            }
            catch (Exception e) {
                p.sendMessage(ChatColor.RED + "Der Spieler ist nicht online.");
            }


        }
    }
}
