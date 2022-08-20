package de.coderr.core.listener;

import de.coderr.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class AFKListener implements Listener
{
    private final String prefix = ChatColor.DARK_GRAY + "[" + Main.themecolor + "AFK" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;
    private final Map<String, Integer> afkTime = new HashMap<>();
    private final Timer timer = new Timer();
    private boolean run = false;
    private int timeToAFK;

    public AFKListener()
    {
        Main main = Main.instance;
        if(!main.getConfig().contains("settings.timeToAFK")) {
            main.getConfig().set("settings.timeToAFK", 5);
            main.saveConfig();
        }
        this.timeToAFK = main.getConfig().getInt("settings.timeToAFK");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        Player p = event.getPlayer();
        afkTime.put(p.getName(),0);
        if (p.getPlayerListName().contains(" [AFK]"))
        {
            Main.tablistManager.setPlayerTeams(p);
            for(Player a : Bukkit.getOnlinePlayers())
            {
                a.sendMessage(prefix + ChatColor.GRAY + p.getName() + " ist nun nicht mehr abwesend.");
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    public void startTimer()
    {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                if (run)
                {
                    for (Map.Entry<String, Integer> entry : afkTime.entrySet())
                    {
                        afkTime.put(entry.getKey(), entry.getValue() + 1);

                        if (entry.getValue() >= timeToAFK)
                        {
                            Player p = Bukkit.getPlayer(entry.getKey());
                            assert p != null;
                            if (!p.getPlayerListName().contains(" [AFK]")) {
                                for (Player a : Bukkit.getOnlinePlayers()) {
                                    a.sendMessage(prefix + ChatColor.GRAY + p.getName() + " ist nun abwesend.");
                                }
                            }
                            p.setPlayerListName(p.getPlayerListName().replace(ChatColor.DARK_PURPLE + " [AFK]","") + ChatColor.DARK_PURPLE + " [AFK]");
                        }
                    }
                    if (Main.instance.getConfig().getBoolean("shutdown.enabled"))
                    {
                        Main.shutdownManager.checkShutdown();
                    }
                }
                else
                {
                    timer.cancel();
                }
            }
        }, 0,60000);
    }

    public void addPlayer(Player p)
    {
        afkTime.put(p.getName(), 0);
    }

    public void removePlayer(Player p)
    {
        afkTime.remove(p.getName());
    }

    public void setRun(boolean value)
    {
        run = value;
    }

    public void setTimeToAFK(int timeToAFK)
    {
        this.timeToAFK = timeToAFK;
    }

}
