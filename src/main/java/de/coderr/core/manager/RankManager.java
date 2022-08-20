package de.coderr.core.manager;

import de.coderr.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class RankManager implements CommandExecutor, TabCompleter
{
    public RankManager()
    {
        updateRanks();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equals("rank")) {
            if (sender.hasPermission("tablist.command.rank") || !(sender instanceof Player)) {
                if (args.length >= 1) {
                    if (args[0].equalsIgnoreCase("set")) {
                        try {
                            setRank(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));

                            sender.sendMessage(ChatColor.GREEN + args[1] + " wurde das Ranglevel " + args[2] + " gesetzt.");
                            Objects.requireNonNull(Bukkit.getPlayer(args[1])).sendMessage(ChatColor.GREEN + "Du hast nun das Ranglevel " + args[2] + ".");
                        } catch (Exception ignored) {
                            sender.sendMessage(ChatColor.RED + "Nutze /rank <set|remove> <playername> [ranklevel]");
                        }

                    } else if (args[0].equalsIgnoreCase("remove")) {
                        try {
                            setRank(Bukkit.getPlayer(args[1]), 0);

                            sender.sendMessage(ChatColor.GREEN + args[1] + " hat nun keinen Rang mehr.");
                            Objects.requireNonNull(Bukkit.getPlayer(args[1])).sendMessage(ChatColor.RED + "Du hast nun keinen Rang mehr.");
                        } catch (Exception ignored) {
                            sender.sendMessage(ChatColor.RED + "Nutze /rank <set|remove> <playername> [ranklevel]");
                        }

                    } else {
                        sender.sendMessage(ChatColor.RED + "Nutze /rank <set|remove> <playername> [ranklevel]");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Nutze /rank <set|remove> <playername> [ranklevel]");
                }

            } else {
                sender.sendMessage(ChatColor.RED + "Du hast nicht die passenden Rechte!");
            }
        } else if (command.getName().equals("maintrance")) {
            if (sender.hasPermission("tablist.rank.admin") || !(sender instanceof Player) || sender.isOp()) {
                if (!Main.maintrance) {
                    for (Player a : Bukkit.getOnlinePlayers()) {
                        if (Main.rankManager.getRank(a.getUniqueId()) != 3) {
                            a.kickPlayer(Main.themecolor + "" + ChatColor.BOLD + "Wartungsarbeiten\n" + Main.themecolor + "Sie können in wenigen Minuten wieder den Server betreten.");
                        }
                    }
                    Main.maintrance = true;
                    sender.sendMessage(ChatColor.GREEN + "Wartungsarbeiten wurden aktiviert");
                } else {
                    Main.maintrance = false;
                    sender.sendMessage(ChatColor.GREEN + "Wartungsarbeiten wurden deaktiviert");
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        List<String> tabComplete = new ArrayList<String>();
        if(args.length == 1)
        {
            tabComplete.add("set");
            tabComplete.add("remove");
        }
        if(args.length == 2)
        {
            for (Player a : Bukkit.getOnlinePlayers())
            {
                tabComplete.add(a.getName());
            }
        }
        if (args.length == 3)
        {
            for (int i=0; i<4;i++)
            {
                tabComplete.add(String.valueOf(i));
            }
        }

        return tabComplete;
    }

    public void setRank(Player p, int permissionlevel)
    {
        try {
            File permissions = new File("plugins//CoderrCore//data//ranks.yml");
            YamlConfiguration permissionsConfig = YamlConfiguration.loadConfiguration(permissions);
            permissionsConfig.set(p.getUniqueId() + ".name",p.getName());
            permissionsConfig.set(p.getUniqueId() + ".rank",permissionlevel);
            permissionsConfig.save(permissions);
            updateRanks();
        }
        catch (Exception ignored)
        {}
    }

    public int getRank(UUID uuid) {
        try {
            File permissions = new File("plugins//CoderrCore//data//ranks.yml");
            YamlConfiguration permissionsConfig = YamlConfiguration.loadConfiguration(permissions);
            return permissionsConfig.getInt(uuid.toString() + ".rank");
        }
        catch (Exception ignored)
        { return 0; }
    }

    public boolean containsRank(UUID uuid) {
        File permissions = new File("plugins//CoderrCore//data//ranks.yml");
        YamlConfiguration permissionsConfig = YamlConfiguration.loadConfiguration(permissions);
        return permissionsConfig.contains(uuid.toString() + ".rank");
    }


    public void updateRanks()
    {
        File permissions = new File("plugins//CoderrCore//data//ranks.yml");
        YamlConfiguration permissionsConfig = YamlConfiguration.loadConfiguration(permissions);
        if (!permissions.exists())
        {
            try {
                permissionsConfig.options().header("Hier werden die Spielerränge gespeichert\n" +
                        "Admin = 3\n" +
                        "Vip = 2\n" +
                        "Premium = 1\n" +
                        "Player = 0");
                permissionsConfig.save(permissions);
            } catch (Exception ignored) { }
        }

        for (Player a : Bukkit.getOnlinePlayers()) {
            Main.tablistManager.setPlayerTeams(Objects.requireNonNull(a));
        }
    }

}
