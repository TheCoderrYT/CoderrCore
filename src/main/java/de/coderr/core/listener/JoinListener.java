package de.coderr.core.listener;

import de.coderr.core.Main;
import de.coderr.core.manager.TablistManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.TimerTask;

public class JoinListener implements Listener
{
    public JoinListener() {
        Main main = Main.instance;
        if(!main.getConfig().contains("tablist.titleOnJoin")) {
            main.getConfig().set("tablist.titleOnJoin", false);
            main.saveConfig();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event)
    {
        Player p = event.getPlayer();

        if (Main.maintrance) {
            if (p.isOp()) {
                p.kickPlayer(Main.themecolor + "" + ChatColor.BOLD + "Wartungsarbeiten\n" + Main.themecolor + "Sie können in wenigen Minuten wieder den Server betreten.");
            }
        }

        if(Main.instance.getConfig().getBoolean("tablist.titleOnJoin")) {
            p.sendTitle(TablistManager.convertColorCode(Objects.requireNonNull(Main.instance.getConfig().getString("tablist.header"))),
                    TablistManager.convertColorCode(Objects.requireNonNull(Main.instance.getConfig().getString("tablist.footer"))), 20, 70, 20);
        }

        event.setJoinMessage(null);
        for (Player a : Bukkit.getOnlinePlayers()) {
            if (a != p) {
                a.sendMessage(Main.themecolor + p.getName() + " hat den Server betreten");
            }
            else {
                a.sendMessage(Main.themecolor + "Willkommen auf " + ChatColor.BOLD + TablistManager.convertColorCode(Main.instance.getConfig().getString("tablist.header")));
            }
        }

        if (!Main.rankManager.containsRank(p.getUniqueId()))
        {
            Main.rankManager.setRank(p,0);
            Main.worldManager.onFirstJoin(p);
        }
        Main.worldManager.onJoin(p);

        Main.tablistManager.permissions.put(String.valueOf(p.getUniqueId()),p.addAttachment(Main.instance));

        for (Player a : Bukkit.getOnlinePlayers())
        {
            Main.tablistManager.setTablist(a);
            Main.tablistManager.setPlayerTeams(a);
        }

        Main.afkListener.addPlayer(p);

    }
}
