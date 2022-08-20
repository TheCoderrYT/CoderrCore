package de.coderr.core.manager;

import de.coderr.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.io.IOException;

public class TeleporterManager implements Listener, CommandExecutor
{
    private File file;
    private YamlConfiguration configuration;

    public TeleporterManager() {
        updateConfiguration();
    }

    private void updateConfiguration() {
        file = new File("plugins//CoderrCore//data//teleporter.yml");
        configuration = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                configuration.options().header("Speicherdaten der Teleporter");
                configuration.save(file);
            } catch (IOException ignored) { }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = event.getPlayer();
            if (getTeleporterLocation(event.getClickedBlock().getLocation()) != null) {
                Location l = getTeleporterLocation(event.getClickedBlock().getLocation());
                l.setPitch(p.getLocation().getPitch());
                l.setYaw(p.getLocation().getYaw());
                l.setX(l.getX()+0.5);
                l.setZ(l.getZ()+0.5);
                p.teleport(l);
            } else if (getTeleporterWorld(event.getClickedBlock().getLocation()) != null) {
                Bukkit.dispatchCommand(p,"world "+getTeleporterWorld(event.getClickedBlock().getLocation()).getName());
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.isOp()) {
                if (args.length == 1) {
                    if (args[0].equals("remove")) {
                        removeTeleporter(p.getLocation());
                        p.sendMessage(ChatColor.GREEN+"Teleporter wurde gelöscht");
                    } else if (Bukkit.getWorld(args[0]) != null) {
                        setTeleporter(p.getLocation(),Bukkit.getWorld(args[0]));
                        p.sendMessage(ChatColor.GREEN+"Teleporter zur Welt \""+args[0]+"\" wurde gesetzt");
                    } else {
                        p.sendMessage(ChatColor.RED+"Die Welt ist nicht geladen");
                    }
                } else if (args.length == 3) {
                    try {
                        double x = Double.parseDouble(args[0]);
                        double y = Double.parseDouble(args[1]);
                        double z = Double.parseDouble(args[2]);
                        setTeleporter(p.getLocation(),new Location(p.getWorld(),x,y,z));
                        p.sendMessage(ChatColor.GREEN+"Teleporter wurde gesetzt");
                    } catch (Exception e) {
                        p.sendMessage(ChatColor.RED+"Ungültige Argumente");
                    }
                }
            }

        }
        return true;
    }

    private Location getTeleporterLocation(Location l) {
        if (configuration.contains(l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ())) {
            String value = configuration.getString(l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ());
            if (value.contains(",")) {
                String[] s = value.split(",");
                return new Location(Bukkit.getWorld(s[0]),Double.parseDouble(s[1]),Double.parseDouble(s[2]),Double.parseDouble(s[3]));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    private World getTeleporterWorld(Location l) {
        if (configuration.contains(l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ())) {
            String value = configuration.getString(l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ());
            if (!value.contains(",")) {
                return Bukkit.getWorld(value);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private void setTeleporter(Location from, Location to) {
        try {
            configuration.set(from.getWorld().getName()+","+from.getBlockX()+","+from.getBlockY()+","+from.getBlockZ(),to.getWorld().getName()+","+to.getBlockX()+","+to.getBlockY()+","+to.getBlockZ());
            configuration.save(file);
            updateConfiguration();
        } catch (Exception e) {
            System.out.println(Main.consoleprefix + "Teleporter konnte nicht gespeichert werden");
        }
    }
    private void setTeleporter(Location from, World to) {
        try {
            configuration.set(from.getWorld().getName()+","+from.getBlockX()+","+from.getBlockY()+","+from.getBlockZ(),to.getName());
            configuration.save(file);
            updateConfiguration();
        } catch (Exception e) {
            System.out.println(Main.consoleprefix + "Teleporter konnte nicht gespeichert werden");
        }
    }
    private void removeTeleporter(Location l) {
        try {
            configuration.set(l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ(),null);
            configuration.save(file);
            updateConfiguration();
        } catch (Exception e) {
            System.out.println(Main.consoleprefix + "Teleporter konnte nicht gespeichert werden");
        }
    }
}
