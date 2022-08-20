package de.coderr.core.listener;

import de.coderr.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

public class BedListener implements Listener
{
    private final String prefix = ChatColor.DARK_GRAY + "[" + Main.themecolor + "Beds" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;

    public BedListener()
    {
        Main main = Main.instance;
        if (!main.getConfig().contains("settings.sleepingMessage")) {
            main.getConfig().set("settings.sleepingMessage",true);
            main.saveConfig();
        }
    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent event)
    {
        if(event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK)
        {
            createMessage(true, event.getPlayer());
        }
    }

    @EventHandler
    public void onSleep(PlayerBedLeaveEvent event)
    {
        createMessage(false, event.getPlayer());
    }

    public void createMessage(Boolean sleeping, Player p)
    {
        String s = "";
        int n = 0;
        int g = 0;
        if (sleeping) {
            s = "schläft nun";
        } else {
            s = "schläft nun nicht mehr";
        }
        for(Player a : Bukkit.getOnlinePlayers())
        {
            if(a == p && sleeping) {
                n++;
            }
            else if (a.isSleeping())
            {
                if (a != p && sleeping)
                {
                    n++;
                }
            }

            if (a.getWorld() == p.getWorld())
            {
                g++;
            }
        }
        for(Player a : Bukkit.getOnlinePlayers()) {
            if (a.getWorld() == p.getWorld() && p.getWorld().getTime() > 12540) {
                a.sendMessage(prefix + Main.themecolor + p.getName() + Main.fontcolor + " " + s + " (" + Main.themecolor + n + "/" + g + Main.fontcolor + ")");
                if (n == g) {
                    a.sendMessage(prefix + Main.fontcolor + "Die Nacht wird durchgeschlafen");
                }
            }
        }
    }
}
