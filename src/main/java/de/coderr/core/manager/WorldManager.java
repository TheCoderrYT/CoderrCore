package de.coderr.core.manager;

import de.coderr.core.Main;
import de.coderr.core.worldgenerator.EmptyWorldGenerator;
import de.coderr.core.worldgenerator.TestWorldGenerator;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WorldManager implements CommandExecutor, Listener, TabCompleter
{
    private final String prefix = ChatColor.DARK_GRAY + "[" + Main.themecolor + "World" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    File file;
    YamlConfiguration configuration;

    Map<World, PlayerDataManager> worldPlayerDataManager = new HashMap<>();
    List<Map<World, PlayerDataManager>> playerDataManagerHistory = new ArrayList<>();
    List<Map<Player, World>> playerWorldHistory = new ArrayList<>();
    Map<String, Location> playerCurrentWorld = new HashMap<>();
    List<Player> proceedPlayer = new ArrayList<>();

    public WorldManager() {
        Main main = Main.instance;

        if(!main.getConfig().contains("world.lobby")) {
            main.getConfig().set("world.lobby", "lobby");
            main.saveConfig();
        }
        if(!main.getConfig().contains("world.testworld")) {
            main.getConfig().set("world.testworld", "testworld");
            main.saveConfig();
        }

        file = new File("plugins//CoderrCore//worlds.yml");
        configuration = YamlConfiguration.loadConfiguration(file);
        try {
            configuration.options().header("Gespeicherte Welten");
            configuration.save(file);
            if (!configuration.contains(Bukkit.getWorlds().get(0).getName())) { configuration.set(Bukkit.getWorlds().get(0).getName(),"Laden..."); }
            if (!configuration.contains(main.getConfig().getString("world.lobby"))) { configuration.set(main.getConfig().getString("world.lobby"),"Laden..."); }
            if (!configuration.contains(main.getConfig().getString("world.testworld"))) { configuration.set(main.getConfig().getString("world.testworld"),"Laden..."); }
            int slot = 10;
            for (String worldName : configuration.getKeys(false)) {
                if (!configuration.contains(worldName + ".datafile")) { configuration.set(worldName + ".datafile", ""+worldName + "_data.yml"); }
                if (!configuration.contains(worldName+".gamemode")) { configuration.set(worldName+".gamemode", Bukkit.getDefaultGameMode().toString()); }
                if (!configuration.contains(worldName+".difficulty")) { configuration.set(worldName+".difficulty", Bukkit.getWorlds().get(0).getDifficulty().toString()); }
                if (!configuration.contains(worldName+".slot")) { configuration.set(worldName+".slot", slot); }
                slot++;
                if (Bukkit.getWorlds().get(0).getName().equals(worldName)) {
                    if (!configuration.contains(worldName + ".damage")) {
                        configuration.set(worldName + ".damage", true);
                    }
                    if (!configuration.contains(worldName + ".saturation")) {
                        configuration.set(worldName + ".saturation", false);
                    }
                } else {
                    if (!configuration.contains(worldName+".enabled")) { configuration.set(worldName+".enabled",true); }
                    if (main.getConfig().getString("world.testworld").equals(worldName)) {
                        if (!configuration.contains(worldName+".loadOnStart")) { configuration.set(worldName+".loadOnStart",false); }
                    }
                    if (!configuration.contains(worldName+".hardcore")) { configuration.set(worldName+".hardcore", false); }
                    if (!configuration.contains(worldName+".saturation")) { configuration.set(worldName+".saturation",false); }
                    if (!configuration.contains(worldName+".damage")) { configuration.set(worldName+".damage", true); }
                    if (!configuration.contains(worldName+".nether")) { configuration.set(worldName+".nether", false); }
                    if (!configuration.contains(worldName+".the_end")) { configuration.set(worldName+".the_end", false); }
                    if (main.getConfig().getString("world.lobby").equals(worldName)) {
                        configuration.set(worldName + ".enabled",null);
                        configuration.set(worldName + ".damage", false);
                        configuration.set(worldName + ".gamemode", "ADVENTURE");
                        configuration.set(worldName + ".difficulty", "PEACEFUL");
                        configuration.set(worldName + ".hardcore", null);
                        configuration.set(worldName + ".saturation", true);
                    } else if (main.getConfig().getString("world.testworld").equals(worldName)) {
                        configuration.set(worldName+".gamemode","CREATIVE");
                        configuration.set(worldName+".difficulty","PEACEFUL");
                    } else {
                        if (!configuration.contains(worldName+".generator")) { configuration.set(worldName + ".generator", "DEFAULT"); }
                    }
                }
                configuration.save(file);

                if (configuration.getBoolean(worldName+".enabled") || worldName.equals(main.getConfig().getString("world.lobby")) || worldName.equals(Bukkit.getWorlds().get(0).getName())) {
                    loadWorld(worldName);
                    if (Bukkit.getWorld(worldName) != null) {
                        World world = Bukkit.getWorld(worldName);
                        if (configuration.contains(worldName + ".difficulty")) {
                            world.setDifficulty(Difficulty.valueOf(configuration.getString(worldName + ".difficulty")));
                            if (Bukkit.getWorld(worldName+"_nether") != null) {
                                if (configuration.getBoolean(worldName + ".nether")) {
                                    Bukkit.getWorld(worldName + "_nether").setDifficulty(Difficulty.valueOf(configuration.getString(worldName + ".difficulty")));
                                }
                            }
                            if (Bukkit.getWorld(worldName+"_the_end") != null) {
                                if (configuration.getBoolean(worldName + ".the_end")) {
                                    Bukkit.getWorld(worldName + "_the_end").setDifficulty(Difficulty.valueOf(configuration.getString(worldName + ".difficulty")));
                                }
                            }
                        }
                        worldPlayerDataManager.put(world, new PlayerDataManager(new File("plugins//CoderrCore//worlddata//" + configuration.getString(world.getName()+".datafile"))));
                        worldPlayerDataManager.get(world).restore();
                    }
                }
            }

            loadCurrentWorlds();
        } catch (IOException ignored) { }
    }

    public void updateConfig() {
        file = new File("plugins//CoderrCore//worlds.yml");
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void store() {
        for (Map.Entry<World,PlayerDataManager> entry : worldPlayerDataManager.entrySet()) {
            entry.getValue().save();
        }
    }

    private void loadWorld(String worldName) {
        if (Bukkit.getWorld(worldName) == null) {
            WorldCreator creator = null;
            if (Main.instance.getConfig().getString("world.lobby").equals(worldName)) {
                creator = new WorldCreator(worldName);
                creator.generateStructures(false);
                creator.environment(World.Environment.NORMAL);
                creator.generator(new EmptyWorldGenerator());
            } else if (Main.instance.getConfig().getString("world.testworld").equals(worldName)) {
                if (configuration.getBoolean(worldName+".loadOnStart")) {
                    creator = new WorldCreator(worldName);
                    creator.generateStructures(false);
                    creator.environment(World.Environment.NORMAL);
                    creator.generator(new TestWorldGenerator());
                }
            } else {
                creator = new WorldCreator(worldName);
                if (configuration.getString(worldName+".generator").equalsIgnoreCase("REDSTONE")) {
                    creator.generator(new TestWorldGenerator());
                } else if (configuration.getString(worldName+".generator").equalsIgnoreCase("EMPTY")) {
                    creator.generator(new EmptyWorldGenerator());
                }
            }
            if (creator != null) {
                System.out.println(Main.consoleprefix+ "" + worldName + " wird nun geladen...");
                boolean loadedBefore = false;
                if (new File(worldName).exists()) {
                    loadedBefore = true;
                }
                Bukkit.createWorld(creator);
                World world = Bukkit.getWorld(worldName);
                if (Main.instance.getConfig().getString("world.lobby").equals(worldName)) {
                    assert world != null;
                    world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                    world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                    world.setPVP(false);
                    if (!loadedBefore) {
                        Location spawn = world.getSpawnLocation().getBlock().getLocation();
                        Location l = new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ());
                        l.setX(l.getX() - 2);
                        l.setZ(l.getZ() - 2);
                        l.setY(l.getY() - 1);
                        for (int x = 0; x < 5; x++) {
                            for (int z = 0; z < 5; z++) {
                                l.getBlock().setType(Material.EMERALD_BLOCK);
                                l.setZ(l.getZ() + 1);
                            }
                            l.setX(l.getX() + 1);
                            l.setZ(l.getZ() - 5);
                        }
                    }
                } else if (Main.instance.getConfig().getString("world.testworld").equals(worldName)) {
                    if (configuration.getBoolean(worldName + ".loadOnStart")) {
                        assert world != null;
                        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                    }
                } else if (configuration.getString(worldName+".generator").equalsIgnoreCase("EMPTY")) {
                    if (!new File(worldName).exists()) {
                        Location spawn = world.getSpawnLocation();
                        spawn.setY(spawn.getY() - 1);
                        spawn.getBlock().setType(Material.BEDROCK);
                    }
                }
                if (configuration.getBoolean(worldName + ".nether")) {
                    creator = new WorldCreator(worldName + "_nether");
                    creator.environment(World.Environment.NETHER);
                    Bukkit.createWorld(creator);
                }
                if (configuration.getBoolean(worldName + ".the_end")) {
                    creator = new WorldCreator(worldName + "_the_end");
                    creator.environment(World.Environment.THE_END);
                    Bukkit.createWorld(creator);
                }
                if (Bukkit.getWorld(worldName+"_nether") != null) {
                    Bukkit.getWorld(worldName + "_nether").setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
                }
                if (Bukkit.getWorld(worldName+"_the_end") != null) {
                    Bukkit.getWorld(worldName + "_the_end").setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
                }
                System.out.println(Main.consoleprefix + "" + worldName + " wurde erfolgreich geladen");
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (command.getName().equals("world")) {
                World w = null;
                if (args.length == 0) {
                    if (p.getWorld() == Bukkit.getWorlds().get(0)) {
                        p.sendMessage(prefix + ChatColor.RED + "Du bist bereits in der " + Main.themecolor + Bukkit.getWorlds().get(0).getName() + ChatColor.RED + ".");
                    } else {
                        w = Bukkit.getWorlds().get(0);
                    }
                } else if (args.length == 1) {
                    if (Bukkit.getWorld(args[0]) != null) {
                        w = Bukkit.getWorld(args[0]);
                        if (p.getWorld() == w) {
                            p.sendMessage(prefix + ChatColor.RED + "Du bist bereits in der " + Main.themecolor + w.getName() + ChatColor.RED + ".");
                            w = null;
                        }
                    } else if (args[0].equalsIgnoreCase("info") && Bukkit.getWorld("info") == null) {
                        p.sendMessage(prefix + Main.fontcolor + "/world [weltname|create|info] [weltname] [gamemode] [welttyp] [difficulty] [hardcore] [nether] [the_end] [damage] [saturation]");
                    } else if (args[0].equalsIgnoreCase("return") && Bukkit.getWorld("return") == null) {
                        if (p.isOp() || !playerCurrentWorld.get(p.getUniqueId().toString()).getWorld().getName().equals(p.getWorld().getName().replace("_nether", "").replace("_the_end", ""))) {
                            //p.teleport(worldPlayerDataManager.get(playerCurrentWorld.get(p.getUniqueId().toString()).getWorld()).getRespawnLocation(p,false));
                            p.teleport(playerCurrentWorld.get(p.getUniqueId().toString()));
                            //p.teleport(worldPlayerDataManager.get(playerCurrentWorld.get(p.getUniqueId().toString()).getWorld()).getStoredLocation(p)); //Nicht möglich, da in worldchange-Methode die store-Methode erst nach dem Teleport ausgeführt wird
                        } else {
                            p.sendMessage(prefix + ChatColor.RED + "Dieser Command ist nur nach externen Teleports verfügbar.");
                        }
                    }
                    else if (args[0].equalsIgnoreCase("undo")) {
                        if (p.isOp()) {
                            if (playerWorldHistory.size() > 0) {
                                Map<Player, World> playerHistory = new HashMap<>();
                                for (Player a : Bukkit.getOnlinePlayers()) {
                                    playerHistory.put(a, Bukkit.getWorld(a.getWorld().getName().replace("_nether", "").replace("_the_end", "")));
                                }
                                boolean equals = true;
                                for (Player tempPlayer : playerHistory.keySet()) {
                                    if (!playerWorldHistory.get(playerWorldHistory.size() - 1).containsKey(tempPlayer)
                                            || !playerWorldHistory.get(playerWorldHistory.size() - 1).containsValue(playerHistory.get(tempPlayer))) {
                                        equals = false;
                                    }
                                }
                                // TODO: Maybe Schwierigkeiten wenn Spieler joint
                                if (!equals) {
                                    proceedPlayer.add(p);
                                    //worldPlayerDataManager.get(p.getWorld()).storePlayerData(p);
                                    System.out.println(playerWorldHistory.get(playerWorldHistory.size() - 1).get(p).getName());
                                    playerDataManagerHistory.get(playerDataManagerHistory.size() - 1).get(playerWorldHistory.get(playerWorldHistory.size() - 1).get(p)).setStoredPlayerData(p);
                                    playerWorldHistory.remove(playerWorldHistory.size() - 1);
                                    worldPlayerDataManager = playerDataManagerHistory.get(playerDataManagerHistory.size() - 1);
                                    playerDataManagerHistory.remove(playerDataManagerHistory.size() - 1);
                                    playerCurrentWorld.put(p.getUniqueId().toString(), worldPlayerDataManager.get(p.getWorld()).getRespawnLocation(p,false));
                                    proceedPlayer.remove(p);
                                    p.sendMessage(prefix + "Dein letzter Teleport wurde rückgängig gemacht.");
                                } else {
                                    p.sendMessage(prefix + ChatColor.RED + "Für dich ist kein Verlauf gespeichert.");
                                }
                            } else {
                                p.sendMessage(prefix + ChatColor.RED + "Es sind keine Verläufe gespeichert.");
                            }
                        } else {
                            p.sendMessage(prefix + ChatColor.RED + "Du hast nicht die passenden Rechte.");
                        }
                    } else {
                        if (args[0].equals(Main.instance.getConfig().getString("world.testworld"))) {
                            if (configuration.getBoolean(args[0]+".enabled")) {
                                System.out.println(Main.consoleprefix+""+args[0]+" wird nun erstellt von " + p.getName() + "...");
                                p.sendMessage(prefix + ChatColor.GRAY +"Testwelt wird nun geladen...");

                                WorldCreator creator = new WorldCreator(args[0]);
                                creator.generateStructures(false);
                                creator.environment(World.Environment.NORMAL);
                                creator.generator(new TestWorldGenerator());
                                Bukkit.createWorld(creator);
                                World world = Bukkit.getWorld(args[0]);
                                assert world != null;
                                world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                                world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);

                                if (configuration.contains(world.getName()+".difficulty")) {
                                    world.setDifficulty(Difficulty.valueOf(configuration.getString(world.getName() + ".difficulty")));
                                    if (configuration.getBoolean(world.getName()+".nether")) {
                                        Bukkit.getWorld(world.getName()+"_nether").setDifficulty(Difficulty.valueOf(configuration.getString(world.getName()+".difficulty")));
                                    }
                                    if (configuration.getBoolean(world.getName()+".the_end")) {
                                        Bukkit.getWorld(world.getName()+"_the_end").setDifficulty(Difficulty.valueOf(configuration.getString(world.getName()+".difficulty")));
                                    }
                                }
                                worldPlayerDataManager.put(world, new PlayerDataManager(new File("plugins//CoderrCore//worlddata//" + configuration.getString(world.getName()+".datafile"))));
                                worldPlayerDataManager.get(world).restore();

                                System.out.println(Main.consoleprefix+""+args[0] + " wurde erfolgreich geladen");
                                p.sendMessage(prefix + ChatColor.GREEN + "Testwelt wurde erfolgreich geladen");

                                TextComponent text = new TextComponent();
                                text.setText(prefix + Main.themecolor + "Du kannst nun die Testwelt betreten | ");
                                TextComponent component = new TextComponent();
                                component.setText(Main.themecolor + "" + ChatColor.BOLD + "Betreten");
                                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/testworld"));
                                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/testworld").create()));
                                text.addExtra(component);
                                p.spigot().sendMessage(text);
                            } else {
                                p.sendMessage(prefix + ChatColor.RED+"Testwelt ist deaktiviert");
                            }
                        } else {
                            p.sendMessage(prefix + ChatColor.RED + "Diese Welt ist nicht geladen");
                        }
                    }
                } else {
                    if (args[0].equalsIgnoreCase("create")) {
                        if (sender.isOp()) {
                            try {
                                boolean error = false;
                                String worldname = args[1].replace("_nether","").replace("_the_end","");
                                if (!configuration.contains(args[1])) {
                                    configuration.set(worldname+".enabled",true);
                                    configuration.set(worldname + ".datafile", ""+worldname + "_data.yml");
                                    String gamemode = GameMode.SURVIVAL.toString();
                                    String generator = "DEFAULT";
                                    String difficulty = Difficulty.NORMAL.toString();
                                    boolean hardcore = false;
                                    boolean damage = true;
                                    boolean saturation = false;
                                    boolean nether = true;
                                    boolean the_end = true;
                                    try {
                                        if (args.length >= 3) {
                                            gamemode = GameMode.valueOf(args[2]).toString();
                                        }
                                        if (args.length >= 4) {
                                            if (args[3].equalsIgnoreCase("REDSTONE")) {
                                                generator = "REDSTONE";
                                            } else if (args[3].equalsIgnoreCase("EMPTY")) {
                                                generator = "EMPTY";
                                            } else {
                                                generator = "DEFAULT";
                                            }
                                        }
                                        if (args.length >= 5) {
                                            difficulty = Difficulty.valueOf(args[4]).toString();
                                        }
                                        if (args.length >= 6) {
                                            if (args[5].equalsIgnoreCase("true")) {
                                                hardcore = true;
                                            } else if (args[5].equalsIgnoreCase("false")){
                                                hardcore = false;
                                            } else {
                                                error = true;
                                            }
                                        }
                                        if (args.length >= 7) {
                                            if (args[6].equalsIgnoreCase("true")) {
                                                nether = true;
                                            } else if (args[6].equalsIgnoreCase("false")){
                                                nether = false;
                                            } else {
                                                error = true;
                                            }
                                        }
                                        if (args.length >= 8) {
                                            if (args[7].equalsIgnoreCase("true")) {
                                                the_end = true;
                                            } else if (args[7].equalsIgnoreCase("false")){
                                                the_end = false;
                                            } else {
                                                error = true;
                                            }
                                        }
                                        if (args.length >= 9) {
                                            if (args[8].equalsIgnoreCase("true")) {
                                                damage = true;
                                            } else if (args[8].equalsIgnoreCase("false")){
                                                damage = false;
                                            } else {
                                                error = true;
                                            }
                                        }
                                        if (args.length >= 10) {
                                            if (args[9].equalsIgnoreCase("true")) {
                                                saturation = true;
                                            } else if (args[9].equalsIgnoreCase("false")){
                                                saturation = false;
                                            } else {
                                                error = true;
                                            }
                                        }
                                    } catch (Exception e) {
                                        error = true;
                                    }
                                    configuration.set(worldname+".gamemode",gamemode);
                                    configuration.set(worldname+".difficulty",difficulty);
                                    configuration.set(worldname+".generator",generator);
                                    configuration.set(worldname+".hardcore",hardcore);
                                    configuration.set(worldname+".damage",damage);
                                    configuration.set(worldname+".saturation",saturation);
                                    configuration.set(worldname+".nether",nether);
                                    configuration.set(worldname+".the_end",the_end);
                                } else {
                                    configuration.set(worldname+".enabled",true);
                                }
                                if (!error) {
                                    configuration.save(file);
                                    for (Player a : Bukkit.getOnlinePlayers()) {
                                        a.sendMessage(Main.ingameprefix + Main.themecolor + "Sie werden in 30 Sekunden gekickt, da der Server eine neue Welt generiert.");
                                    }
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new TimerTask() {
                                        @Override
                                        public void run() {
                                            for (Player a : Bukkit.getOnlinePlayers()) {
                                                a.kickPlayer(Main.themecolor + "Der Server generiert nun eine neue Welt.\nSie können in wenigen Sekunden wieder den Server betreten.");
                                                Bukkit.dispatchCommand(p, "reload");
                                            }
                                        }
                                    }, 20 * 30);
                                } else {
                                    p.sendMessage(prefix + ChatColor.RED + "/world [weltname|create|info] [weltname] [gamemode] [welttyp] [difficulty] [hardcore] [nether] [the_end] [damage] [saturation]");
                                }
                                configuration = null;
                                updateConfig();
                            } catch (Exception e) {
                                p.sendMessage(prefix + ChatColor.RED+"Welt konnte nicht generiert werden");
                            }
                        }
                    }
                    else if (args[0].equalsIgnoreCase("disable")) {
                        if (Bukkit.getWorld(args[1]) != null) {
                            String worldname = Bukkit.getWorld(args[1]).getName().replace("_nether","").replace("_the_end","");
                            try {
                                configuration.set(worldname + ".enabled", false);
                                configuration.save(file);
                                for (Player a : Bukkit.getOnlinePlayers()) {
                                    if (a.getWorld().getName().equals(worldname) || a.getWorld().getName().equals(worldname+"_nether") || a.getWorld().getName().equals(worldname+"_the_end")) {
                                        Bukkit.dispatchCommand(a,"l");
                                        a.sendMessage(prefix + Main.themecolor+"Du wurdest in die Lobby teleportiert, da "+worldname+" deaktiviert wurde.");
                                        if (p.getPlayerListName().contains("["+worldname+"]") || p.getPlayerListName().contains("["+worldname+"_nether]") || p.getPlayerListName().contains("["+worldname+"_the_end]")) {
                                            p.setPlayerListName(p.getPlayerListName().replace(ChatColor.DARK_GRAY + " [" + worldname + "]", ""));
                                        }
                                    }
                                }
                                Bukkit.unloadWorld(worldname,true);
                                if (Bukkit.getWorld(worldname+"_nether") != null) {
                                    Bukkit.unloadWorld(worldname+"_nether",true);
                                }
                                if (Bukkit.getWorld(worldname+"_the_end") != null) {
                                    Bukkit.unloadWorld(worldname+"_the_end",true);
                                }
                                p.sendMessage(prefix + ChatColor.GREEN + worldname + " wurde deaktiviert");
                                System.out.println(Main.consoleprefix + worldname + " wurde deaktiviert");
                            } catch (IOException ioException) {
                                p.sendMessage(prefix + ChatColor.RED + worldname + " konnte nicht deaktiviert werden");
                            }
                        } else {
                            p.sendMessage(prefix + ChatColor.RED + "Diese Welt ist nicht geladen.");
                        }
                    }
                }
                if (w != null) {
                    if (w.getEnvironment() != World.Environment.NETHER && w.getEnvironment() != World.Environment.THE_END) {
                        if (p.getWorld().getName().replace("_nether", "").replace("_the_end", "").equals(playerCurrentWorld.get(p.getUniqueId().toString()).getWorld().getName())) {
                            for (Player a : Bukkit.getOnlinePlayers()) {
                                if (a.getWorld().getName().equals(p.getWorld().getName()) || a.getWorld().getName().equals(p.getWorld().getName() + "_nether") || a.getWorld().getName().equals(p.getWorld().getName() + "_the_end")) {
                                    if (a != p) {
                                        a.sendMessage(prefix + p.getDisplayName() + ChatColor.GRAY + " hat die Welt verlassen.");
                                    }
                                }
                            }

                            saveInHistory();
                            //Main.jumpAndRunManager.worldChange(p); // priority
                            for (Map.Entry<World, PlayerDataManager> entry : worldPlayerDataManager.entrySet()) {
                                if (p.getWorld() == entry.getKey() || p.getWorld().getName().equals(entry.getKey().getName() + "_nether") || p.getWorld().getName().equals(entry.getKey().getName() + "_the_end")) {
                                    entry.getValue().storePlayerData(p);
                                }
                            }
                            proceedPlayer.add(p);
                            if (worldPlayerDataManager.get(w).getStoredLocation(p) != null) {
                                worldPlayerDataManager.get(w).setStoredPlayerData(p);
                            } else {
                                //worldPlayerDataManager.get(w).storePlayerData(p);
                                p.setGameMode(GameMode.SPECTATOR);
                                p.setHealth(20);
                                p.setFoodLevel(20);
                                p.setLevel(0);
                                p.setExp(0);
                                for (PotionEffect potionEffect : p.getActivePotionEffects()) {
                                    p.removePotionEffect(potionEffect.getType());
                                }
                                p.getInventory().clear();
                                p.teleport(w.getSpawnLocation().getBlock().getLocation());
                                worldPlayerDataManager.get(Bukkit.getWorld(p.getWorld().getName().replace("_nether","").replace("_the_end",""))).setStoredRespawnLocation(p,Bukkit.getWorld(p.getWorld().getName().replace("_nether","").replace("_the_end","")).getSpawnLocation(),0);
                                if (Bukkit.getWorlds().get(0) != w) {
                                    try {
                                        p.setGameMode(GameMode.valueOf(configuration.getString(w.getName() + ".gamemode")));
                                    } catch (Exception ignored) {
                                    }
                                } else {
                                    p.setGameMode(Bukkit.getDefaultGameMode());
                                }
                            }
                            proceedPlayer.remove(p);
                            playerCurrentWorld.put(p.getUniqueId().toString(), worldPlayerDataManager.get(w).getRespawnLocation(p,false));

                            if (p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.CREATIVE) {
                                p.setAllowFlight(true);
                                p.setFlying(true);
                            } else {
                                p.setAllowFlight(false);
                            }

                            if (w.getName().equals(Main.instance.getConfig().getString("world.lobby"))) {
                                Main.lobbyInventory.setInv(p);
                            }

                            System.out.println(Main.consoleprefix + p.getName() + " joined " + w.getName());
                            p.sendMessage(prefix + Main.themecolor + "Willkommen in der " + Main.themecolor + w.getName() + ".");

                            for (Player a : Bukkit.getOnlinePlayers()) {
                                if (a.getWorld().getName().equals(w.getName()) || a.getWorld().getName().equals(w.getName() + "_nether") || a.getWorld().getName().equals(w.getName() + "_the_end")) {
                                    if (a != p) {
                                        a.sendMessage(prefix + p.getDisplayName() + ChatColor.GRAY + " hat die Welt betreten.");
                                    }
                                }
                            }
                        } else {
                            p.sendMessage(prefix + Main.fontcolor + "Du bist nicht in der aktuell für dich gespeicherten Welt.");
                            p.sendMessage(prefix + Main.fontcolor + "Durch den Teleport würden deine Daten beschädigt werden.");
                            TextComponent msg0 = new TextComponent(prefix + Main.fontcolor + "Bitte begebe dich in die ");
                            TextComponent msg1 = new TextComponent(Main.themecolor + playerCurrentWorld.get(p.getUniqueId().toString()).getWorld().getName());
                            msg1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/world return"));
                            msg1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,TextComponent.fromLegacyText("/world return")));
                            TextComponent msg2 = new TextComponent(Main.fontcolor + " und versuche es erneut.");
                            msg0.addExtra(msg1);
                            msg0.addExtra(msg2);
                            p.spigot().sendMessage(msg0);
                        }
                    } else {
                        p.sendMessage(prefix + ChatColor.RED + "Du kannst dich nicht in diese Welt teleportieren.");
                    }
                }
            }
            if (command.getName().equals("testworld")) {
                Bukkit.dispatchCommand(p,"world "+Main.instance.getConfig().getString("world.testworld"));
            }
            if (command.getName().equals("lobby")) {
                Bukkit.dispatchCommand(p,"world "+Main.instance.getConfig().getString("world.lobby"));
            }
            if (command.getName().equals("l")) {
                Bukkit.dispatchCommand(p,"world "+Main.instance.getConfig().getString("world.lobby"));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (World w : Bukkit.getWorlds()) {
                if (w.getEnvironment() != World.Environment.NETHER && w.getEnvironment() != World.Environment.THE_END) {
                    list.add(w.getName());
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("disable")) {
            for (World w : Bukkit.getWorlds()) {
                if (w.getEnvironment() != World.Environment.NETHER && w.getEnvironment() != World.Environment.THE_END) {
                    list.add(w.getName());
                }
            }
        } else if (args.length >= 3 && args[0].equalsIgnoreCase("create")) {
            if (args.length == 3) {
                for (GameMode g : GameMode.values()) {
                    list.add(g.name());
                }
            } else if (args.length == 4) {
                list.add("DEFAULT");
                list.add("REDSTONE");
                list.add("EMPTY");
            } else if (args.length == 5) {
                for (Difficulty d : Difficulty.values()) {
                    list.add(d.name());
                }
            } else if (args.length <= 10) {
                list.add("true");
                list.add("false");
            }
        }

        return list;
    }

    // SECONDARY
    public void onJoin(Player p) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new TimerTask() {
            @Override
            public void run() {
                if (!playerCurrentWorld.containsKey(p.getUniqueId().toString())) {
                    saveInHistory();
                    if (worldPlayerDataManager.get(p.getWorld().getName().replace("_nether", "").replace("_the_end", "")) != null) {
                        playerCurrentWorld.put(p.getUniqueId().toString(), worldPlayerDataManager.get(p.getWorld().getName().replace("_nether", "").replace("_the_end", "")).getRespawnLocation(p, false));
                    } else {
                        playerCurrentWorld.put(p.getUniqueId().toString(), Bukkit.getWorld(p.getWorld().getName().replace("_nether", "").replace("_the_end", "")).getSpawnLocation());
                    }
                }
            }
        }, 25);
    }

    // PRIMARY
    public void onFirstJoin(Player p) {
        p.setGameMode(GameMode.SPECTATOR);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new TimerTask() {
            @Override
            public void run() {
                Location l = Bukkit.getWorld(Main.instance.getConfig().getString("world.lobby")).getSpawnLocation();
                p.teleport(new Location(l.getWorld(),l.getX()+0.5,l.getY(),l.getZ()+0.5));
                p.setBedSpawnLocation(p.getWorld().getSpawnLocation().getBlock().getLocation(), true);
                try {
                    p.setGameMode(GameMode.valueOf(configuration.getString(p.getWorld().getName() + ".gamemode")));
                } catch (Exception ignored) { }
            }
        }, 20);
    }

    public void onDisable() {
        for (Player a : Bukkit.getOnlinePlayers()) {
            if (a.isDead()) {
                a.spigot().respawn();
            }
            World lobby = Bukkit.getWorld(Main.instance.getConfig().getString("world.lobby"));
            World world = Bukkit.getWorlds().get(0);
            World world_nether = Bukkit.getWorld(Bukkit.getWorlds().get(0).getName()+"_nether");
            World world_the_end = Bukkit.getWorld(Bukkit.getWorlds().get(0).getName()+"_the_end");
            if (a.getWorld() != lobby
                    && a.getWorld() != world
                    && a.getWorld() != world_nether
                    && a.getWorld() != world_the_end) {
                if (!a.getWorld().getName().replace("_nether", "").replace("_the_end", "").equals(playerCurrentWorld.get(a.getUniqueId().toString()).getWorld().getName())) {
                    Bukkit.dispatchCommand(a, "world return");
                }
                Bukkit.dispatchCommand(a,"lobby");
            }
        }
        saveCurrentWorlds();
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player p = event.getPlayer();
        if (playerCurrentWorld.containsKey(p.getUniqueId().toString())) {
            if (!p.getWorld().getName().replace("_nether", "").replace("_the_end", "").equals(playerCurrentWorld.get(p.getUniqueId().toString()).getWorld().getName()) && !proceedPlayer.contains(p)) {
                worldPlayerDataManager.get(event.getFrom()).storePlayerData(p);
                //playerCurrentWorld.put(p.getUniqueId().toString(), new Location(event.getFrom(),0,0,0));
                p.getInventory().clear();
                System.out.println(Main.consoleprefix + p.getName() + " betritt " + p.getWorld().getName() + " durch externen Teleport.");
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new TimerTask() {
                    @Override
                    public void run() {
                        p.updateInventory();
                    }
                }, 5);
            } else if (p.getWorld().getName().replace("_nether", "").replace("_the_end", "").equals(playerCurrentWorld.get(p.getUniqueId().toString()).getWorld().getName())
                    && !event.getFrom().getName().replace("_nether", "").replace("_the_end", "").equals(playerCurrentWorld.get(p.getUniqueId().toString()).getWorld().getName())) {
                p.setGameMode(worldPlayerDataManager.get(p.getWorld()).getStoredGamemode(p));
                p.getInventory().setContents(worldPlayerDataManager.get(p.getWorld()).getStoredInventory(p));
                p.setHealth(worldPlayerDataManager.get(p.getWorld()).getStoredHealth(p));
                p.setFoodLevel(worldPlayerDataManager.get(p.getWorld()).getStoredHunger(p));
                p.setLevel(worldPlayerDataManager.get(p.getWorld()).getStoredLevel(p));
                p.setExp(worldPlayerDataManager.get(p.getWorld()).getStoredExp(p));
                p.addPotionEffects(Arrays.asList(worldPlayerDataManager.get(p.getWorld()).getStoredPotionEffects(p)));
                //p.setGameMode(GameMode.valueOf(configuration.getString(p.getWorld().getName()+".gamemode")));
                System.out.println(Main.consoleprefix + p.getName() + " wird in gespeicherte Welt zurückteleportiert.");
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        World world = Bukkit.getWorld(event.getPlayer().getWorld().getName().replace("_nether","").replace("_the_end",""));
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (playerCurrentWorld.get(p.getUniqueId().toString()).getWorld() == world) {
                Block b = event.getClickedBlock();
                if (b.getType().toString().contains("BED") && b.getType() != Material.BEDROCK) {
                    if (p.getWorld().getEnvironment() != World.Environment.NETHER && p.getWorld().getEnvironment() != World.Environment.THE_END) {
                        if (worldPlayerDataManager.get(world).getRespawnLocation(p, false).getBlockX() != b.getLocation().getBlockX()
                                || worldPlayerDataManager.get(world).getRespawnLocation(p, false).getBlockY() != b.getLocation().getBlockY() + 1
                                || worldPlayerDataManager.get(world).getRespawnLocation(p, false).getBlockZ() != b.getLocation().getBlockZ()) {
                            p.sendMessage(Main.ingameprefix + Main.fontcolor + "Respawnpunkt wurde gesetzt.");
                        }
                        worldPlayerDataManager.get(world).setStoredRespawnLocation(p, event.getClickedBlock().getLocation(), 1);
                        saveInHistory();
                        playerCurrentWorld.put(p.getUniqueId().toString(),event.getClickedBlock().getLocation());
                    }
                } else if (b.getBlockData() instanceof RespawnAnchor) {
                    if (p.getWorld().getEnvironment() == World.Environment.NETHER) {
                        RespawnAnchor respawnAnchor = (RespawnAnchor) b.getBlockData();
                        if (0 < respawnAnchor.getCharges() && p.getItemInHand().getType() == Material.AIR) {
                            if (worldPlayerDataManager.get(world).getRespawnLocation(p, false).getBlockX() != event.getClickedBlock().getLocation().getBlockX()
                                    || worldPlayerDataManager.get(world).getRespawnLocation(p, false).getBlockY() != event.getClickedBlock().getLocation().getBlockY() + 1
                                    || worldPlayerDataManager.get(world).getRespawnLocation(p, false).getBlockZ() != event.getClickedBlock().getLocation().getBlockZ()) {
                                p.sendMessage(Main.ingameprefix + Main.fontcolor + "Respawnpunkt wurde gesetzt.");
                            }
                            worldPlayerDataManager.get(world).setStoredRespawnLocation(p, event.getClickedBlock().getLocation(), 2);
                            saveInHistory();
                            playerCurrentWorld.put(p.getUniqueId().toString(),event.getClickedBlock().getLocation());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRespawnCommand(PlayerCommandPreprocessEvent event) {
        if (playerCurrentWorld.get(event.getPlayer().getUniqueId().toString()).getWorld() == event.getPlayer().getWorld()) {
            if (event.getMessage().contains("/spawnpoint")) {
                Player p = event.getPlayer();
                World world = Bukkit.getWorld(event.getPlayer().getWorld().getName().replace("_nether", "").replace("_the_end", ""));
                event.setCancelled(true);
                if (event.getMessage().equals("/spawnpoint")) {
                    worldPlayerDataManager.get(world).setStoredRespawnLocation(p, p.getLocation(), 0);
                    saveInHistory();
                    playerCurrentWorld.put(p.getUniqueId().toString(),p.getLocation());
                    p.sendMessage(Main.ingameprefix + Main.fontcolor + "Respawnpunkt wurde gesetzt.");
                } else {
                    p.sendMessage(Main.ingameprefix + Main.fontcolor + "Dieser Command ist deaktiviert.");
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (configuration.getBoolean(event.getPlayer().getWorld().getName() + ".hardcore")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new TimerTask() {
                @Override
                public void run() {
                    event.getPlayer().setGameMode(GameMode.SPECTATOR);
                    event.getPlayer().sendMessage(Main.ingameprefix + Main.fontcolor + "Diese Welt ist im HARDCORE Modus. Du kannst nicht respawnen.");
                }
            },5);
        } else {
            Player p = event.getPlayer();
            if (playerCurrentWorld.get(p.getUniqueId().toString()).getWorld().getName().equals(p.getWorld().getName().replace("_nether", "").replace("_the_end", ""))) {
                event.isAnchorSpawn();
                World world = Bukkit.getWorld(event.getPlayer().getWorld().getName().replace("_nether","").replace("_the_end",""));
                Location respawnlocation = worldPlayerDataManager.get(world).getRespawnLocation(p,false);
                respawnlocation.setY(respawnlocation.getY() - 1);
                if (respawnlocation.getBlock().getBlockData() instanceof RespawnAnchor) {
                    if (event.isAnchorSpawn()) {
                        event.setRespawnLocation(worldPlayerDataManager.get(world).getRespawnLocation(p,true));
                    } else {
                        event.setRespawnLocation(Bukkit.getWorld(p.getWorld().getName().replace("_nether","").replace("_the_end","")).getSpawnLocation());
                        p.sendMessage(Main.ingameprefix + Main.fontcolor + "Der Seelenanker ist nicht mehr geladen.");
                    }
                } else {
                    event.setRespawnLocation(worldPlayerDataManager.get(world).getRespawnLocation(p,true));
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        World lobby = Bukkit.getWorld(Main.instance.getConfig().getString("world.lobby"));
        World world = Bukkit.getWorlds().get(0);
        World world_nether = Bukkit.getWorld(Bukkit.getWorlds().get(0).getName()+"_nether");
        World world_the_end = Bukkit.getWorld(Bukkit.getWorlds().get(0).getName()+"_the_end");
        if (p.getWorld() != lobby
                && p.getWorld() != world
                && p.getWorld() != world_nether
                && p.getWorld() != world_the_end) {
            Bukkit.dispatchCommand(p,"lobby");
        }
        worldPlayerDataManager.get(p.getWorld()).storePlayerData(p);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().getWorld().getName().equals(Main.instance.getConfig().getString("world.lobby"))) {
            if (event.getTo().getY() < 10) {
                event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(3).add(new Vector(0,10,0))); // max velocity is 10
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().getWorld().getName().equals(Main.instance.getConfig().getString("world.lobby")) && !event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            String worldname = event.getEntity().getWorld().getName();
            if (worldname.contains("_nether")) {
                worldname = worldname.replace("_nether","");
            }
            if (worldname.contains("_the_end")) {
                worldname = worldname.replace("_the_end","");
            }
            if (!configuration.getBoolean(worldname+".damage")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            String worldname = event.getEntity().getWorld().getName();
            if (worldname.contains("_nether")) {
                worldname = worldname.replace("_nether","");
            }
            if (worldname.contains("_the_end")) {
                worldname = worldname.replace("_the_end","");
            }
            if (configuration.getBoolean(worldname+".saturation")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPortal(EntityPortalEvent event) {
        Entity entity = event.getEntity();
        World world = Bukkit.getWorld(event.getEntity().getWorld().getName().replace("_nether","").replace("_the_end",""));
        World world_nether = Bukkit.getWorld(world.getName()+"_nether");
        World world_the_end = Bukkit.getWorld(world.getName()+"_the_end");
        if (event.getTo().getWorld().getName().contains("_nether") || event.getFrom().getWorld().getName().contains("_nether")) {
            if (world_nether != null) {
                if (entity.getWorld() == world_nether) {
                    event.getTo().setWorld(world);
                } else {
                    event.getTo().setWorld(world_nether);
                }
            } else {
                event.setCancelled(true);
            }
        } else if (event.getFrom().getWorld().getName().contains("_the_end") && !event.getTo().getWorld().getName().contains("_the_end")
                || !event.getFrom().getWorld().getName().contains("_the_end") && event.getTo().getWorld().getName().contains("_the_end")) {
            if (world_the_end != null) {
                if (entity.getWorld() == world_the_end) {
                    event.getTo().setWorld(world);
                } else {
                    event.getTo().setWorld(world_the_end);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPortal(PlayerPortalEvent event) {
        Player entity = event.getPlayer();
        World world = Bukkit.getWorld(event.getPlayer().getWorld().getName().replace("_nether","").replace("_the_end",""));
        World world_nether = Bukkit.getWorld(world.getName()+"_nether");
        World world_the_end = Bukkit.getWorld(world.getName()+"_the_end");
        if (event.getTo().getWorld().getName().contains("_nether") || event.getFrom().getWorld().getName().contains("_nether")) {
            if (world_nether != null) {
                if (entity.getWorld() == world_nether) {
                    event.getTo().setWorld(world);
                } else {
                    event.getTo().setWorld(world_nether);
                }
            } else {
                event.setCancelled(true);
            }
        } else if (event.getFrom().getWorld().getName().contains("_the_end") && !event.getTo().getWorld().getName().contains("_the_end")
                || !event.getFrom().getWorld().getName().contains("_the_end") && event.getTo().getWorld().getName().contains("_the_end")) {
            if (world_the_end != null) {
                if (entity.getWorld() == world_the_end) {
                    event.getTo().setWorld(world);
                } else {
                    event.getTo().setWorld(world_the_end);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    public void saveInHistory() {
        for (Player a : Bukkit.getOnlinePlayers()) {
            worldPlayerDataManager.get(a.getWorld()).storePlayerData(a);
        }
        if (playerDataManagerHistory.size() == 3) {
            playerDataManagerHistory.remove(2);
        }
        if (playerWorldHistory.size() == 3) {
            playerWorldHistory.remove(2);
        }
        playerDataManagerHistory.add(worldPlayerDataManager);
        Map<Player, World> playerHistory = new HashMap<>();
        for (Player a : Bukkit.getOnlinePlayers()) {
            playerHistory.put(a,Bukkit.getWorld(a.getWorld().getName().replace("_nether","").replace("_the_end","")));
        }
        playerWorldHistory.add(playerHistory);
    }

    public void loadCurrentWorlds() {
        try {
            File file = new File("plugins//CoderrCore//data//player.yml");
            if (file.exists()) {
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                for (String uuid : configuration.getKeys(false)) {
                    String[] loc = configuration.getString(uuid + ".location").split(",");
                    playerCurrentWorld.put(uuid, new Location(Bukkit.getWorld(loc[0]),Double.parseDouble(loc[1]),Double.parseDouble(loc[2]),Double.parseDouble(loc[3])));
                }
            }
        } catch (Exception e) {
            System.out.println(Main.consoleprefix + "Aktuelle Welten der Spieler konnten nicht geladen werden.");
        }
    }

    public void saveCurrentWorlds() {
        try {
            File file = new File("plugins//CoderrCore//data//player.yml");
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            for (Map.Entry<String,Location> uuid : playerCurrentWorld.entrySet()) {
                    configuration.set(uuid.getKey() + ".location", uuid.getValue().getWorld().getName()+","+uuid.getValue().getBlockX()+","+uuid.getValue().getBlockY()+","+uuid.getValue().getBlockZ());
            }
            configuration.save(file);
        } catch (Exception e) {
            System.out.println(Main.consoleprefix + "Aktuelle Welten der Spieler konnten nicht gespeichert werden.");
        }
    }
}
