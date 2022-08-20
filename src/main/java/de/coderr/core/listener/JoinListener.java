package de.coderr.core.listener;

import de.coderr.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.TimerTask;

public class JoinListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event)
    {
        Player p = event.getPlayer();

        if (Main.maintrance) {
            if (p.isOp()) {
                p.kickPlayer(Main.themecolor + "" + ChatColor.BOLD + "Wartungsarbeiten\n" + Main.themecolor + "Sie können in wenigen Minuten wieder den Server betreten.");
            }
        }

        final int[] counter = {0};
        int delay = 2;
        p.sendTitle("_","",0,5,0);
        int task = 0;
        int finalTask = task;
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new TimerTask() {
            @Override
            public void run() {
                switch (counter[0]) {
                    case 1: p.sendTitle(Main.themecolor+"W","",0,delay+2,0);break;
                    case 2: p.sendTitle(Main.themecolor+"Wi","",0,delay+2,0);break;
                    case 3: p.sendTitle(Main.themecolor+"Wil","",0,delay+2,0);break;
                    case 4: p.sendTitle(Main.themecolor+"Will","",0,delay+2,0);break;
                    case 5: p.sendTitle(Main.themecolor+"Willk","",0,delay+2,0);break;
                    case 6: p.sendTitle(Main.themecolor+"Willko","",0,delay+2,0);break;
                    case 7: p.sendTitle(Main.themecolor+"Willkom","",0,delay+2,0);break;
                    case 8: p.sendTitle(Main.themecolor+"Willkomm","",0,delay+2,0);break;
                    case 9: p.sendTitle(Main.themecolor+"Willkomme","",0,delay+2,0);break;
                    case 10: p.sendTitle(Main.themecolor+"Willkommen","",0,delay+2,0);break;
                    case 11: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"a",0,delay+2,0);break;
                    case 12: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"au",0,delay+2,0);break;
                    case 13: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf",0,delay+2,0);break;
                    case 14: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf ",0,delay+2,0);break;
                    case 15: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf F",0,delay+2,0);break;
                    case 16: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Fl",0,delay+2,0);break;
                    case 17: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Flo",0,delay+2,0);break;
                    case 18: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Flor",0,delay+2,0);break;
                    case 19: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Flori",0,delay+2,0);break;
                    case 20: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Floria",0,delay+2,0);break;
                    case 21: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian",0,delay+2,0);break;
                    case 22: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´",0,delay+2,0);break;
                    case 23: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s",0,delay+2,0);break;
                    case 24: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s ",0,delay+2,0);break;
                    case 25: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s S",0,delay+2,0);break;
                    case 26: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s Se",0,delay+2,0);break;
                    case 27: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s Ser",0,delay+2,0);break;
                    case 28: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s Serv",0,delay+2,0);break;
                    case 29: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s Serve",0,delay+2,0);break;
                    case 30: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s Server",0,delay+2,0);break;
                    case 31: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s Servern",0,delay+2,0);break;
                    case 32: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s Serverne",0,delay+2,0);break;
                    case 33: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s Servernet",0,delay+2,0);break;
                    case 34: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s Servernetz",0,delay+2,0);break;
                    case 35: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s Servernetzw",0,delay+2,0);break;
                    case 36: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s Servernetzwe",0,delay+2,0);break;
                    case 37: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s Servernetzwer",0,delay+2,0);break;
                    case 38: p.sendTitle(Main.themecolor+"Willkommen",Main.themecolor+"auf Florian´s Servernetzwerk",0,20,10);Bukkit.getScheduler().cancelTask(finalTask);break;
                }
                counter[0]++;
            }
        }, 0,delay);

        event.setJoinMessage(null);
        for (Player a : Bukkit.getOnlinePlayers()) {
            if (a != p) {
                a.sendMessage(Main.themecolor + p.getName() + " hat den Server betreten");
            }
            else {
                a.sendMessage(Main.themecolor + "Willkommen auf " + ChatColor.BOLD + "Florian´s Servernetzwerk");
            }
        }

        if (!Main.rankManager.containsRank(p.getUniqueId()))
        {
            Main.rankManager.setRank(p,0);
            Main.worldManager.onFirstJoin(p);
        }

        Main.tablistManager.permissions.put(String.valueOf(p.getUniqueId()),p.addAttachment(Main.instance));

        for (Player a : Bukkit.getOnlinePlayers())
        {
            Main.tablistManager.setTablist(a);
            Main.tablistManager.setPlayerTeams(a);
        }

        Main.afkListener.addPlayer(p);

    }
}
