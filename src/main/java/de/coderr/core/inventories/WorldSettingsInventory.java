package de.coderr.core.inventories;

import de.coderr.core.Main;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WorldSettingsInventory implements Listener, CommandExecutor
{
    String configPath = "plugins//CoderrCore//worlds.yml";
    File file = new File(configPath);
    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
    private Map<World,Inventory> invs = new HashMap<>();
    private Map<Integer,ItemStack> items = new HashMap<>();

    public WorldSettingsInventory() {
        for (World w : Bukkit.getWorlds()) {
            if (!w.getName().contains("_nether") && !w.getName().contains("_the_end")) {
                invs.put(w, Bukkit.createInventory(null, 9 * 3, "Welteinstellungen"));
                updateInventory(w);
            }
        }
    }

    @EventHandler
    public void onLoadWorld(WorldLoadEvent event) {
        if (!invs.containsKey(event.getWorld())) {
            invs.put(event.getWorld(),Bukkit.createInventory(null,9*3,"Welteinstellungen"));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (command.getName().equals("settings")) {
                updateInventory(p.getWorld());
                p.openInventory(invs.get(Bukkit.getWorld(p.getWorld().getName().replace("_nether","").replace("_the_end",""))));
            } else if (command.getName().equals("defaultgamemode")) {
                p.setGameMode(GameMode.valueOf(configuration.getString(p.getWorld().getName()+".gamemode")));
                p.sendMessage(ChatColor.GREEN + "Dein Spielmodus wurde auf "+p.getGameMode().toString()+" geändert.");
            }
        }
        return true;
    }

    /**
     * WorldSpawnPoint-Setting
     * Gamemode-Setting
     * Difficulty-Setting
     * PVP-Setting
     * Damage-Setting
     * Sättigung
     * DayLightCycle-Setting
     * @param event
     */
    @EventHandler
    public void onInventory(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        World world = Bukkit.getWorld(p.getWorld().getName().replace("_nether","").replace("_the_end",""));
        World world_nether = Bukkit.getWorld(world.getName()+"_nether");
        World world_the_end = Bukkit.getWorld(world.getName()+"_the_end");
        if (event.getClickedInventory() == invs.get(world)) {
            if (p.hasPermission("coderrcore.rank.admin") || p.isOp()) {
                if (event.getSlot() == 10) {
                    if (p.getWorld() == world) {
                        world.setSpawnLocation(p.getLocation());
                        p.sendMessage(Main.themecolor + "Der Weltspawnpunkt wurde auf deine Position gesetzt");
                    } else {
                        p.sendMessage(ChatColor.RED+"Der Weltspawnpunkt kann nur in der Oberwelt gesetzt werden");
                    }
                }
                else if (event.getSlot() == 11) // Gamemode
                {
                    int i = 0;
                    switch (getDefaultGamemode(world)) {
                        case ADVENTURE:
                            i = 1;
                            break;
                        case CREATIVE:
                            i = 2;
                            break;
                        case SPECTATOR:
                            i = 3;
                            break;
                    }
                    i++;
                    if (i > 3) {
                        i = 0;
                    }
                    try {
                        switch (i) {
                            case 0:
                                configuration.set(world.getName() + ".gamemode", "SURVIVAL");
                                break;
                            case 1:
                                configuration.set(world.getName() + ".gamemode", "ADVENTURE");
                                break;
                            case 2:
                                configuration.set(world.getName() + ".gamemode", "CREATIVE");
                                break;
                            case 3:
                                configuration.set(world.getName() + ".gamemode", "SPECTATOR");
                                break;
                        }
                    } catch (Exception e) {
                        System.out.println(Main.consoleprefix + "Gamemode-Einstellung konnte nicht gespeichert werden.");
                    }
                /*
                for (Player a : Bukkit.getOnlinePlayers()) {
                    if (a.getWorld() == w) {
                        a.sendMessage(Main.themeManager.getColor() + "Der Standartspielmodus für diese Welt wurde auf " + getDefaultGamemode(w).toString() + " gesetzt");
                        TextComponent text = new TextComponent(Main.themeManager.getColor()+""+ChatColor.BOLD+"(Klicke hier, um in den Standartspielmodus zu welchseln)");
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("SPIELMODUS WECHSELN").create()));
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/defaultgamemode"));
                        p.spigot().sendMessage(text);
                    }
                }
                */
                }
                else if (event.getSlot() == 12) // Difficulty
                {
                    int i = 0;
                    switch (world.getDifficulty()) {
                        case EASY:
                            i = 1;
                            break;
                        case NORMAL:
                            i = 2;
                            break;
                        case HARD:
                            i = 3;
                            break;
                    }
                    i++;
                    if (i > 3) {
                        i = 0;
                    }
                    configuration = YamlConfiguration.loadConfiguration(file);
                    if (configuration.get(world.getName() + ".difficulty") != null) {
                        switch (i) {
                            case 0:
                                configuration.set(world.getName() + ".difficulty", "PEACEFUL");
                                break;
                            case 1:
                                configuration.set(world.getName() + ".difficulty", "EASY");
                                break;
                            case 2:
                                configuration.set(world.getName() + ".difficulty", "NORMAL");
                                break;
                            case 3:
                                configuration.set(world.getName() + ".difficulty", "HARD");
                                break;
                        }
                        try {
                            configuration.save(file);
                            configuration = YamlConfiguration.loadConfiguration(file);
                        } catch (IOException ignored) {
                            System.out.println(Main.consoleprefix + "Speichern der Difficulty-Einstellung fehlgeschlagen.");
                        }
                        world.setDifficulty(Difficulty.valueOf(configuration.getString(world.getName() + ".difficulty")));
                        if (world_nether != null) {
                            world_nether.setDifficulty(Difficulty.valueOf(configuration.getString(world.getName() + ".difficulty")));
                        }
                        if (world_the_end != null) {
                            world_the_end.setDifficulty(Difficulty.valueOf(configuration.getString(world.getName() + ".difficulty")));
                        }
                    }
                    else {
                        System.out.println(Main.consoleprefix+"Fehler beim Speichern der Difficulty-Einstellung.");
                    }
                }
                else if (event.getSlot() == 13) {
                    world.setPVP(!world.getPVP());
                    if (world_nether != null) {
                        world_nether.setPVP(!world.getPVP());
                    }
                    if (world_the_end != null) {
                        world_the_end.setPVP(!world.getPVP());
                    }
                }
                else if (event.getSlot() == 14) {
                    if (configuration.getBoolean(world.getName()+".damage")) {
                        configuration.set(world.getName()+".damage",false);
                    } else {
                        configuration.set(world.getName()+".damage",true);
                    }
                }
                else if (event.getSlot() == 15) {
                    if (configuration.getBoolean(world.getName()+".saturation")) {
                        configuration.set(world.getName()+".saturation",false);
                    } else {
                        configuration.set(world.getName()+".saturation",true);
                    }
                }
                else if (event.getSlot() == 16) {
                    if (world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE) == Boolean.TRUE) {
                        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,Boolean.FALSE);
                        if (world_nether != null) {
                            world_nether.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, Boolean.FALSE);
                        }
                        if (world_the_end != null) {
                            world_the_end.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, Boolean.FALSE);
                        }
                    } else {
                        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,Boolean.TRUE);
                        if (world_nether != null) {
                            world_nether.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, Boolean.TRUE);
                        }
                        if (world_the_end != null) {
                            world_the_end.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, Boolean.TRUE);
                        }
                    }
                }
            } else {
                p.sendMessage(Main.ingameprefix + ChatColor.RED + "Du hast nicht die passenden Rechte");
            }
            try {
                configuration.save(file);
                configuration = YamlConfiguration.loadConfiguration(file);
            } catch (IOException ignored) {
                System.out.println(Main.consoleprefix + "Einstellungen für "+world.getName()+" konnten nicht gespeichert werden.");
            }
            Main.instance.worldManager.updateConfig();
            file = new File(configPath);
            configuration = YamlConfiguration.loadConfiguration(file);
            updateInventory(world);
            event.setCancelled(true);
        }
    }

    public void updateInventory(World w) {
        setItem(invs.get(w),10,Material.COMPASS,1,"Welt-Spawnpunkt", ChatColor.RESET + "" + ChatColor.GRAY + "Aktuell: " + w.getSpawnLocation().getBlockX() + " " + w.getSpawnLocation().getBlockY() + " " + w.getSpawnLocation().getBlockZ() + "\n" + ChatColor.GRAY + "(Linksklick)");

        // Grasblock:Survival, Cleanstone:Creative, Apfel:Adventure, Enderauge:Spectator
        if (getDefaultGamemode(w) == GameMode.SURVIVAL) {
            setItem(invs.get(w), 11, Material.GRASS_BLOCK, 1, "Spielmodus", ChatColor.RESET + "" + ChatColor.GRAY + "Aktuell: " + ChatColor.DARK_GREEN + "Überleben" + "\n" + ChatColor.GRAY + "(Linksklick)");
        } else if (getDefaultGamemode(w) == GameMode.CREATIVE) {
            setItem(invs.get(w), 11, Material.STONE, 1, "Spielmodus", ChatColor.RESET + "" + ChatColor.GRAY + "Aktuell: " + ChatColor.DARK_AQUA + "Kreativ" + "\n" + ChatColor.GRAY + "(Linksklick)");
        } else if (getDefaultGamemode(w) == GameMode.ADVENTURE) {
            setItem(invs.get(w), 11, Material.APPLE, 1, "Spielmodus", ChatColor.RESET + "" + ChatColor.GRAY + "Aktuell: " + ChatColor.DARK_RED + "Abenteuer" + "\n" + ChatColor.GRAY + "(Linksklick)");
        } else if (getDefaultGamemode(w) == GameMode.SPECTATOR) {
            setItem(invs.get(w), 11, Material.ENDER_EYE, 1, "Spielmodus", ChatColor.RESET + "" + ChatColor.GRAY + "Aktuell: " + ChatColor.DARK_RED + "Beobachter" + "\n" + ChatColor.GRAY + "(Linksklick)");
        }

        // Blume:Peaceful:Pink, Lederchestplate:Easy:Braun, Ironchestplate:Normal:Yellow, Diachestplate:Hard:Lightblue
        if (w.getDifficulty() == Difficulty.PEACEFUL) {
            setItem(invs.get(w),12,Material.POPPY,1,"Schwierigkeit",ChatColor.RESET+""+ChatColor.GRAY+"Aktuell: "+ChatColor.LIGHT_PURPLE+"Friedlich" + "\n" + ChatColor.GRAY + "(Linksklick)");
        } else if (w.getDifficulty() == Difficulty.EASY) {
            setItem(invs.get(w),12,Material.LEATHER_CHESTPLATE,1,"Schwierigkeit",ChatColor.RESET+""+ChatColor.GRAY+"Aktuell: "+ChatColor.DARK_GREEN+"Einfach" + "\n" + ChatColor.GRAY + "(Linksklick)");
        } else if (w.getDifficulty() == Difficulty.NORMAL) {
            setItem(invs.get(w),12,Material.IRON_CHESTPLATE,1,"Schwierigkeit",ChatColor.RESET+""+ChatColor.GRAY+"Aktuell: "+ChatColor.YELLOW+"Normal" + "\n" + ChatColor.GRAY + "(Linksklick)");
        } else if (w.getDifficulty() == Difficulty.HARD) {
            setItem(invs.get(w),12,Material.DIAMOND_CHESTPLATE,1,"Schwierigkeit",ChatColor.RESET+""+ChatColor.GRAY+"Aktuell: "+ChatColor.BLUE+"Schwer" + "\n" + ChatColor.GRAY + "(Linksklick)");
        }

        if (w.getPVP()) {
            setItem(invs.get(w),13,Material.IRON_SWORD,1,"PVP", ChatColor.RESET+""+ChatColor.GRAY+"Aktuell: "+ChatColor.GREEN+"Aktiviert" + "\n" + ChatColor.GRAY + "(Linksklick)");
        } else {
            setItem(invs.get(w),13,Material.IRON_SWORD,1,"PVP", ChatColor.RESET+""+ChatColor.GRAY+"Aktuell: "+ChatColor.RED+"Deaktiviert" + "\n" + ChatColor.GRAY + "(Linksklick)");
        }

        if (configuration.getBoolean(w.getName()+".damage")) {
            setItem(invs.get(w),14,Material.TOTEM_OF_UNDYING,1,ChatColor.WHITE+""+ChatColor.ITALIC+"Schaden",ChatColor.RESET+""+ChatColor.GRAY+"Aktuell: "+ChatColor.GREEN+"Aktiviert" + "\n" + ChatColor.GRAY + "(Linksklick)");
        } else {
            setItem(invs.get(w),14,Material.TOTEM_OF_UNDYING,1,ChatColor.WHITE+""+ChatColor.ITALIC+"Schaden",ChatColor.RESET+""+ChatColor.GRAY+"Aktuell: "+ChatColor.RED+"Deaktiviert" + "\n" + ChatColor.GRAY + "(Linksklick)");
        }

        if (configuration.getBoolean(w.getName()+".saturation")) {
            setItem(invs.get(w),15,Material.COOKED_BEEF,1,"Sättigung",ChatColor.RESET+""+ChatColor.GRAY+"Aktuell: "+ChatColor.GREEN+"Aktiviert" + "\n" + ChatColor.GRAY + "(Linksklick)");
        } else {
            setItem(invs.get(w),15,Material.COOKED_BEEF,1,"Sättigung",ChatColor.RESET+""+ChatColor.GRAY+"Aktuell: "+ChatColor.RED+"Deaktiviert" + "\n" + ChatColor.GRAY + "(Linksklick)");
        }

        if (w.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE) == Boolean.TRUE) {
            setItem(invs.get(w),16,Material.DAYLIGHT_DETECTOR,1,"Tag-Nacht-Zyklus",ChatColor.RESET+""+ChatColor.GRAY+"Aktuell: "+ChatColor.GREEN+"Aktiviert" + "\n" + ChatColor.GRAY + "(Linksklick)");
        } else {
            setItem(invs.get(w),16,Material.DAYLIGHT_DETECTOR,1,"Tag-Nacht-Zyklus",ChatColor.RESET+""+ChatColor.GRAY+"Aktuell: "+ChatColor.RED+"Deaktiviert" + "\n" + ChatColor.GRAY + "(Linksklick)");
        }

        setEmptySlots(invs.get(w));
    }

    private void setItem(Inventory inv,int index,Material type,int amount,String name,String lore) {
        ItemStack item = new ItemStack(type,amount);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        if (lore != null) { meta.setLore(Arrays.asList(lore.split("\n"))); }
        item.setItemMeta(meta);
        inv.setItem(index,item);
        items.put(index,item);
    }

    private void setEmptySlots(Inventory inv) {
        for (int i=0;i<inv.getSize();i++) {
            if (inv.getItem(i) == null) {
                ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.setDisplayName(" ");
                item.setItemMeta(meta);
                inv.setItem(i,item);
            }
        }
    }

    private GameMode getDefaultGamemode(World w) {
        if (w.getName().contains("_nether") || w.getName().contains("_the_end")) {
            w = Bukkit.getWorld(w.getName().replace("_nether","").replace("_the_end","").trim());
        }
        if (configuration.contains(w.getName()+".gamemode")) {
            return GameMode.valueOf(configuration.getString(w.getName()+".gamemode"));
        } else {
            return GameMode.SPECTATOR;
        }
    }
}
