package de.coderr.core.commands;

import de.coderr.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PositionCommand implements CommandExecutor, TabCompleter
{
    private String prefix = ChatColor.DARK_GRAY + "[" + Main.themecolor + "Positionen" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            int x = p.getLocation().getBlockX();
            int y = p.getLocation().getBlockY();
            int z = p.getLocation().getBlockZ();

            if (args.length > 0)
            {
                if (args[0].equalsIgnoreCase("get") && args.length == 2)
                {
                    if (getPosition(args[1],p.getWorld().getName()) != null)
                    {
                        Location loc = getPosition(args[1],p.getWorld().getName());
                        p.sendMessage(prefix + Main.themecolor + args[1] + ChatColor.GRAY + " ist bei " + Main.themecolor + ChatColor.ITALIC + loc.getX() + " " + loc.getY() + " " + loc.getZ() + ChatColor.RESET + ChatColor.GRAY + ".");
                    }
                    else
                    {
                        p.sendMessage(prefix + ChatColor.RED + "Der Ort " + Main.themecolor + args[1] + ChatColor.RED + " ist nicht gespeichert.");
                    }

                }
                else if (args[0].equalsIgnoreCase("remove") && args.length == 2)
                {
                    if (getPosition(args[1],p.getWorld().getName()) != null)
                    {
                        removePosition(args[1],p.getWorld().getName());
                        p.sendMessage(prefix + Main.themecolor + args[1] + ChatColor.GRAY + " wurde gelöscht.");
                    }
                    else
                    {
                        p.sendMessage(prefix + ChatColor.RED + "Der Ort " + Main.themecolor + args[1] + ChatColor.RED + " ist nicht gespeichert.");
                    }
                }
                else
                {
                    if (getPosition(args[0],p.getWorld().getName()) == null) {

                        saveInFile(args[0], x, y, z, p.getWorld().getName());

                        String worldname = p.getWorld().getName().replace("_nether","").replace("_the_end","");
                        for (Player a : Bukkit.getOnlinePlayers()) {
                            if (a.getWorld().getName().equals(worldname) || a.getWorld().getName().equals(worldname+"_nether") || a.getWorld().getName().equals(worldname+"_the_end")) {
                                a.sendMessage(prefix + ChatColor.GRAY + p.getDisplayName() + ChatColor.GRAY + " hat bei " + Main.themecolor + ChatColor.ITALIC + x + " " + y + " " + z + ChatColor.RESET + ChatColor.GRAY + " den Ort " + Main.themecolor + args[0] + ChatColor.GRAY + " markiert.");
                            }
                        }
                    }
                    else
                    {
                        p.sendMessage(prefix + Main.themecolor + args[0] + ChatColor.RED + " ist bereits gespeichert.");
                    }
                }
            }
            else
            {
                String worldname = p.getWorld().getName().replace("_nether","").replace("_the_end","");
                for (Player a : Bukkit.getOnlinePlayers()) {
                    if (a.getWorld().getName().equals(worldname) || a.getWorld().getName().equals(worldname+"_nether") || a.getWorld().getName().equals(worldname+"_the_end")) {
                        a.sendMessage(prefix + ChatColor.GRAY + p.getDisplayName() + ChatColor.GRAY + " ist bei " + Main.themecolor + ChatColor.ITALIC + x + " " + y + " " + z + ChatColor.RESET + ChatColor.GRAY + ".");
                    }
                }
            }


        }
        return true;
    }

    private void saveInFile(String name, int x, int y, int z, String world)
    {
        try
        {
            File positions = new File("plugins//CoderrCore//data//positions.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(positions);

            if (!positions.exists())
            {
                config.options().header("Speichert die Positionen der Orte");
                config.save(positions);
            }

            config.set(world+"."+name + ".x",x);
            config.set(world+"."+name + ".y",y);
            config.set(world+"."+name + ".z",z);

            config.save(positions);

        }
        catch (Exception ignored)
        { }

    }

    private Location getPosition(String ort, String world)
    {
        Location loc = null;
        try
        {
            File positions = new File("plugins//CoderrCore//data//positions.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(positions);

            for (String name : config.getConfigurationSection(world).getKeys(false))
            {
                if (name.equalsIgnoreCase(ort))
                {
                    loc = new Location(Bukkit.getWorld(Objects.requireNonNull(world)),config.getDouble(world+"."+name + ".x"), config.getDouble(world+"."+name + ".y"), config.getDouble(world+"."+name + ".z"));
                    break;
                }
            }
        }
        catch (Exception ignored)
        { }
        return loc;
    }

    private void removePosition(String ort, String world)
    {
        try
        {
            File positions = new File("plugins//CoderrCore//data//positions.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(positions);

            config.set(world+"."+ort,null);
            config.save(positions);
        }
        catch (Exception ignored)
        { }
    }

    private int round(double d)
    {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if(result<0.5){
            return d<0 ? -i : i;
        }else{
            return d<0 ? -(i+1) : i+1;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> tabComplete = new ArrayList<String>();

        if ((args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("remove"))
                && args.length == 2)
        {
            try
            {
                File positions = new File("plugins//CoderrCore//data//positions.yml");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(positions);
                Player p = (Player) sender;
                tabComplete.addAll(config.getConfigurationSection(p.getWorld().getName()).getKeys(false));
            }
            catch (Exception ignored)
            { }
        }
        else if (args.length == 1) {
            tabComplete.add("set");
            tabComplete.add("get");
            tabComplete.add("remove");
        }

        return tabComplete;
    }
}
