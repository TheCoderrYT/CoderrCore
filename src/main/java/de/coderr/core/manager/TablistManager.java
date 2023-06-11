package de.coderr.core.manager;

import de.coderr.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.*;

public class TablistManager implements Listener
{
    public Map<String, PermissionAttachment> permissions = new HashMap<>();
    private final Timer timer;

    public TablistManager()
    {
        Main main = Main.instance;
        if(!main.getConfig().contains("tablist")) {
            main.getConfig().set("tablist.header", "&t§lMinecraft Servernetzwerk");
            main.getConfig().set("tablist.footer","&8>>> &r&t&opowered by CoderrCore &r&8<<<");
            main.saveConfig();
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateTablist();
            }
        }, 0,1000);

        createPermissionsFile();
    }

    public void stopTimer()
    {
        timer.cancel();
    }

    public void setTablist(Player p)
    {
        String tps = Main.tpsCommand.getTPSMessage();
        p.setPlayerListHeaderFooter(" " + convertColorCode(Objects.requireNonNull(Main.instance.getConfig().getString("tablist.header"))) + " ",
                Main.themecolor + parseTime(p.getWorld().getTime()) + ChatColor.DARK_GRAY + " - " + Main.themecolor + tps + "\n"
                        + " " + convertColorCode(Objects.requireNonNull(Main.instance.getConfig().getString("tablist.footer"))) + " ");
        
    }

    private String parseTime(long time) {
        long stunden = time / 1000;
        for (int i=0;i<6;i++) {
            stunden++;
            if (stunden > 23) {
                stunden = 0;
            }
        }
        long minuten = (time % 1000) * 60 / 1000;
        String sm = "0" + minuten;
        sm = sm.substring(sm.length() - 2, sm.length());
        return stunden + ":" + sm;
    }

    public void updateTablist()
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            setTablist(p);
            setPlayerWorldInfo(p);
        }
    }

    public void setPlayerWorldInfo(Player p) {
        for (World w : Bukkit.getWorlds()) {
            if (p.getPlayerListName().contains("["+w.getName()+"]")) {
                p.setPlayerListName(p.getPlayerListName().replace(ChatColor.DARK_GRAY+" ["+w.getName()+"]",""));
                break;
            }
        }
        p.setPlayerListName(p.getPlayerListName()+ChatColor.DARK_GRAY+" ["+p.getWorld().getName()+"]");
    }

    public void setPlayerTeams(Player p)
    {
        Scoreboard scoreboard = p.getScoreboard();

        Team oparators = scoreboard.getTeam("aoperators");

        if(oparators == null)
        {
            oparators = scoreboard.registerNewTeam("aoperators");
        }

        oparators.setColor(ChatColor.DARK_RED);

        Team vips = scoreboard.getTeam("bvips");

        if(vips == null)
        {
            vips = scoreboard.registerNewTeam("bvips");
        }

        vips.setColor(ChatColor.DARK_PURPLE);

        Team premiums = scoreboard.getTeam("cpremiums");

        if(premiums == null)
        {
            premiums = scoreboard.registerNewTeam("cpremiums");
        }

        premiums.setColor(ChatColor.GOLD);

        Team players = scoreboard.getTeam("dplayers");

        if(players == null)
        {
            players = scoreboard.registerNewTeam("dplayers");
        }

        players.setColor(ChatColor.GREEN);

        for (Player a : Bukkit.getOnlinePlayers())
        {
            if (Main.rankManager.getRank(a.getUniqueId()) == 3)
            {
                a.setOp(true);
                oparators.addEntry(a.getName());
                a.setPlayerListName(ChatColor.DARK_RED + "Admin " + ChatColor.DARK_GRAY + "| " + ChatColor.DARK_RED + a.getName());
                a.setDisplayName(ChatColor.DARK_RED + a.getName());

                PermissionAttachment att = permissions.get(String.valueOf(a.getUniqueId()));
                for (Map.Entry<String, Boolean> entry : att.getPermissions().entrySet())
                {
                    att.unsetPermission(entry.getKey());
                }
                setPermissions(att,3);
                permissions.put(String.valueOf(a.getUniqueId()),att);
            }
            else if (Main.rankManager.getRank(a.getUniqueId()) == 2)
            {
                vips.addEntry(a.getName());
                a.setPlayerListName(ChatColor.DARK_PURPLE + "VIP " + ChatColor.DARK_GRAY + "| " + ChatColor.DARK_PURPLE + a.getName());
                a.setDisplayName(ChatColor.DARK_PURPLE + a.getName());

                PermissionAttachment att = permissions.get(String.valueOf(a.getUniqueId()));
                for (Map.Entry<String, Boolean> entry : att.getPermissions().entrySet())
                {
                    att.unsetPermission(entry.getKey());
                }
                setPermissions(att,2);
                permissions.put(String.valueOf(a.getUniqueId()),att);
            }
            else if (Main.rankManager.getRank(a.getUniqueId()) == 1)
            {
                premiums.addEntry(a.getName());
                a.setPlayerListName(ChatColor.GOLD + "Premium " + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + a.getName());
                a.setDisplayName(ChatColor.GOLD + a.getName());

                PermissionAttachment att = permissions.get(String.valueOf(a.getUniqueId()));
                for (Map.Entry<String, Boolean> entry : att.getPermissions().entrySet())
                {
                    att.unsetPermission(entry.getKey());
                }
                setPermissions(att,1);
                permissions.put(String.valueOf(a.getUniqueId()),att);
            }
            else
            {
                players.addEntry(a.getName());
                a.setPlayerListName(ChatColor.GREEN + "Spieler " + ChatColor.DARK_GRAY + "| " + ChatColor.GREEN + a.getName());
                a.setDisplayName(ChatColor.GREEN + a.getName());

                PermissionAttachment att = permissions.get(String.valueOf(a.getUniqueId()));
                for (Map.Entry<String, Boolean> entry : att.getPermissions().entrySet())
                {
                    att.unsetPermission(entry.getKey());
                }
                setPermissions(att,0);
                permissions.put(String.valueOf(a.getUniqueId()),att);
            }
        }

        setPlayerWorldInfo(p);
    }

    private void setPermissions(PermissionAttachment permissionAttachment, int ranklevel)
    {
        File permissions = new File("plugins//CoderrCore//permissions.yml");
        YamlConfiguration permissionsConfig = YamlConfiguration.loadConfiguration(permissions);

        String rank = null;
        if (ranklevel == 0)
        {
            rank = "player";
        }
        else if (ranklevel == 1)
        {
            rank = "premium";
        }
        else if (ranklevel == 2)
        {
            rank = "vip";
        }
        else if (ranklevel == 3)
        {
            rank = "admin";
        }
        else
        {
            rank = "";
        }

        for (String string : Objects.requireNonNull(permissionsConfig.getConfigurationSection(rank)).getKeys(false))
        {
            String permission = string.replace("-",".");
            try
            {
                permissionAttachment.setPermission(permission, permissionsConfig.getBoolean(rank + "." + string));
            } catch (Exception ignored) {
                System.out.println(Main.consoleprefix + permission + " konnte nicht gesetzt werden.");
            }

        }
    }

    public void createPermissionsFile()
    {
        File permissions = new File("plugins//CoderrCore//permissions.yml");
        YamlConfiguration permissionsConfig = YamlConfiguration.loadConfiguration(permissions);

        if (!permissions.exists())
        {
            try {
                permissionsConfig.options().header("Trage hier die Permissions für den entsprechenden Rang ein.\n" +
                        "Beispiel:\n" +
                        "|admin:                             |\n" +
                        "|   minecraft-command-gamemode: true|");
                permissionsConfig.set("admin.minecraft-command-gamemode",true);
                permissionsConfig.set("admin.minecraft-command-give",true);
                permissionsConfig.set("admin.coderrcore-command-rank",true);
                permissionsConfig.set("admin.coderrcore-command-update",true);
                permissionsConfig.set("admin.coderrcore-command-permissions",true);
                permissionsConfig.set("admin.coderrcore-rank-admin",true);
                permissionsConfig.set("admin.coderrcore-command-testworld",true);
                permissionsConfig.set("admin.coderrcore-command-invact",true);
                permissionsConfig.set("vip.coderrcore-command-update",true);
                permissionsConfig.set("vip.coderrcore-command-permissions",true);
                permissionsConfig.set("vip.coderrcore-rank-vip",true);
                permissionsConfig.set("vip.coderrcore-command-testworld",true);
                permissionsConfig.set("vip.coderrcore-command-invsee",true);
                permissionsConfig.set("premium.coderrcore-command-permissions",true);
                permissionsConfig.set("premium.coderrcore-rank-premium",true);
                permissionsConfig.set("player.coderrcore-command-permissions",true);
                permissionsConfig.set("player.coderrcore-rank-player",true);
                permissionsConfig.save(permissions);
            } catch (Exception ignored) { }
        }
    }

    public static String convertColorCode(String string) {
        return string
                .replace("&0",ChatColor.BLACK+"")
                .replace("&1",ChatColor.DARK_BLUE+"")
                .replace("&2",ChatColor.DARK_GREEN+"")
                .replace("&3",ChatColor.BLUE+"")
                .replace("&4",ChatColor.DARK_RED+"")
                .replace("&5",ChatColor.DARK_PURPLE+"")
                .replace("&6",ChatColor.GOLD+"")
                .replace("&7",ChatColor.GRAY+"")
                .replace("&8",ChatColor.DARK_GRAY+"")
                .replace("&9",ChatColor.BLUE+"")
                .replace("&a",ChatColor.GREEN+"")
                .replace("&b",ChatColor.AQUA+"")
                .replace("&c",ChatColor.RED+"")
                .replace("&d",ChatColor.LIGHT_PURPLE+"")
                .replace("&e",ChatColor.YELLOW+"")
                .replace("&f",ChatColor.WHITE+"")
                .replace("&k",ChatColor.MAGIC+"")
                .replace("&m",ChatColor.STRIKETHROUGH+"")
                .replace("&n",ChatColor.UNDERLINE+"")
                .replace("&l",ChatColor.BOLD+"")
                .replace("&o",ChatColor.ITALIC+"")
                .replace("&r",ChatColor.RESET+"")
                .replace("&t",Main.themecolor+"");
    }

}
