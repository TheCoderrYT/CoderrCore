package de.coderr.core.manager;

import de.coderr.core.Main;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

public class PlayerDataManager
{
    HashMap<String, String> playerLocations = new HashMap<>();
    HashMap<String, String> playerGamemodes = new HashMap<>();
    HashMap<String, ItemStack[]> playerInventories = new HashMap<>();
    HashMap<String, String> playerHealth = new HashMap<>();
    HashMap<String, Integer> playerHunger = new HashMap<>();
    HashMap<String, String> playerXp = new HashMap<>();
    HashMap<String, String> playerRespawnLocations = new HashMap<>();
    HashMap<String, PotionEffect[]> playerPotionEffects = new HashMap<>();
    File file;

    public PlayerDataManager(File storeFile) {
        file = storeFile;
    }

    public void storePlayerData(Player p) {
        playerLocations.put(p.getUniqueId().toString(), p.getWorld().getName() + "," + p.getLocation().getX() + "," + p.getLocation().getY() + "," + p.getLocation().getZ() + "," + p.getLocation().getYaw() + "," + p.getLocation().getPitch());
        playerGamemodes.put(p.getUniqueId().toString(), p.getGameMode().toString());
        playerInventories.put(p.getUniqueId().toString(), p.getInventory().getContents());
        playerHealth.put(p.getUniqueId().toString(), String.valueOf(p.getHealth()));
        playerHunger.put(p.getUniqueId().toString(), p.getFoodLevel());
        playerXp.put(p.getUniqueId().toString(), p.getLevel() + "," + p.getExp());
        if (!playerRespawnLocations.containsKey(p.getUniqueId().toString())) {
            Location respawn = p.getWorld().getSpawnLocation();
            playerRespawnLocations.put(p.getUniqueId().toString(),  respawn.getWorld().getName() + "," + respawn.getX() + "," + respawn.getY() + "," + respawn.getZ()+",3");
        }
        playerPotionEffects.put(p.getUniqueId().toString(),p.getActivePotionEffects().toArray(new PotionEffect[0]));
    }

    public void switchPlayerData(Player p) {
        Location location = getStoredLocation(p);
        double health = getStoredHealth(p);
        int foodlevel = getStoredHunger(p);
        ItemStack[] inventory = getStoredInventory(p);
        GameMode gameMode = getStoredGamemode(p);
        int level = getStoredLevel(p);
        float exp = getStoredExp(p);
        PotionEffect[] potionEffects = getStoredPotionEffects(p);

        storePlayerData(p);

        p.setGameMode(GameMode.SPECTATOR);
        p.setHealth(health);
        p.setFoodLevel(foodlevel);
        p.getInventory().setContents(inventory);
        p.setLevel(level);
        p.setExp(exp);
        for (PotionEffect potionEffect : p.getActivePotionEffects()) {
            p.removePotionEffect(potionEffect.getType());
        }
        if (potionEffects != null) {
            p.addPotionEffects(Arrays.asList(potionEffects));
        }
        p.teleport(location);
        p.setGameMode(gameMode);
    }

    public Location getStoredLocation(Player p) {
        if (playerLocations.containsKey(p.getUniqueId().toString())) {
            try {
                String[] location = playerLocations.get(p.getUniqueId().toString()).split(",");
                return new Location(Bukkit.getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]), Float.parseFloat(location[4]),Float.parseFloat(location[5]));
            } catch (NumberFormatException ignored) {
                return null;
            }
        } else {
            return null;
        }
    }

    public GameMode getStoredGamemode(Player p) {
        if (playerGamemodes.containsKey(p.getUniqueId().toString())) {
            return GameMode.valueOf(playerGamemodes.get(p.getUniqueId().toString()));
        } else {
            return null;
        }
    }

    public ItemStack[] getStoredInventory(Player p) {
        return playerInventories.getOrDefault(p.getUniqueId().toString(), null);
    }

    public double getStoredHealth(Player p) {
        if (playerHealth.containsKey(p.getUniqueId().toString())) {
            return Double.parseDouble(playerHealth.get(p.getUniqueId().toString()));
        } else {
            return 0;
        }
    }

    public int getStoredHunger(Player p) {
        return playerHunger.getOrDefault(p.getUniqueId().toString(), 0);
    }

    public float getStoredExp(Player p) {
        if (playerXp.containsKey(p.getUniqueId().toString())) {
            String[] levelExp = playerXp.get(p.getUniqueId().toString()).split(",");
            return Float.parseFloat(levelExp[1]);
        } else {
            return 0;
        }
    }

    public int getStoredLevel(Player p) {
        if (playerXp.containsKey(p.getUniqueId().toString())) {
            String[] levelExp = playerXp.get(p.getUniqueId().toString()).split(",");
            return Integer.parseInt(levelExp[0]);
        } else {
            return 0;
        }
    }

    public Location getRespawnLocation(Player p, boolean message) {
        if (playerRespawnLocations.containsKey(p.getUniqueId().toString())) {
            try {
                String[] location = playerRespawnLocations.get(p.getUniqueId().toString()).split(",");
                Location l = new Location(Bukkit.getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]));
                switch (location[4]) {
                    case "0":
                        if (l.getBlock().getType() == Material.AIR || l.getBlock().getType() == Material.CAVE_AIR || l.getBlock().getType() == Material.VOID_AIR) {
                            l.setX(l.getX() + 0.5);
                            l.setZ(l.getZ() + 0.5);
                            return l;
                        }
                        break;
                    case "1":
                        if (l.getBlock().getType().toString().contains("BED") && l.getBlock().getType() != Material.BEDROCK) {
                            l.setY(l.getY() + 1);
                            if (l.getBlock().getType() == Material.AIR || l.getBlock().getType() == Material.CAVE_AIR || l.getBlock().getType() == Material.VOID_AIR) {
                                l.setX(l.getX() + 0.5);
                                l.setZ(l.getZ() + 0.5);
                                return l;
                            }
                        } else {
                            if (message) {
                                p.sendMessage(Main.ingameprefix + Main.fontcolor + "Das gespeicherte Bett wurde zerstört.");
                                message = false;
                            }
                        }
                        break;
                    case "2":
                        if (l.getBlock().getType() == Material.RESPAWN_ANCHOR) {
                            // Seelenankerstatus wird in PlayerRespawnEvent geprüft
                            l.setY(l.getY() + 1);
                            if (l.getBlock().getType() == Material.AIR || l.getBlock().getType() == Material.CAVE_AIR || l.getBlock().getType() == Material.VOID_AIR) {
                                l.setX(l.getX() + 0.5);
                                l.setZ(l.getZ() + 0.5);
                                return l;
                            }
                        } else {
                            if (message) {
                                p.sendMessage(Main.ingameprefix + Main.fontcolor + "Der gespeicherte Seelenanker wurde zerstört.");
                                message = false;
                            }
                        }
                        break;
                }
            } catch (NumberFormatException ignored) { }
        }
        if (message) {
            p.sendMessage(Main.ingameprefix + Main.fontcolor + "Du hast kein Bett oder geladenen Seelenanker.");
        }
        return Bukkit.getWorld(p.getWorld().getName().replace("_nether","").replace("_the_end","")).getSpawnLocation();
    }

    public PotionEffect[] getStoredPotionEffects(Player p) {
        return playerPotionEffects.getOrDefault(p.getUniqueId().toString(), null);
    }

    public void setStoredRespawnLocation(Player p, Location respawnLocation, int respawnType) { // type: 0=command 1=bed 2=anchor
        playerRespawnLocations.put(p.getUniqueId().toString(),respawnLocation.getWorld().getName()+","+respawnLocation.getX()+","+respawnLocation.getY()+","+respawnLocation.getZ()+","+respawnType);
    }

    public void restore()
    {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String key : config.getKeys(false)) {
                playerLocations.put(key, config.getString(key + ".location.world") + "," +
                        config.getString(key + ".location.x") + "," +
                        config.getString(key + ".location.y") + "," +
                        config.getString(key + ".location.z") + "," +
                        config.getString(key + ".location.yaw") + "," +
                        config.getString(key + ".location.pitch"));

                playerGamemodes.put(key, config.getString(key + ".gamemode"));

                if (config.get(key + ".inventory") != null) {
                    ItemStack[] inhalt;
                    inhalt = ((List<ItemStack>) config.get(key + ".inventory")).toArray(new ItemStack[0]);
                    playerInventories.put(key, inhalt);
                }

                playerHealth.put(key,config.getString(key + ".health"));

                playerHunger.put(key,config.getInt(key + ".foodlevel"));

                playerXp.put(key,config.getString(key + ".xp"));

                playerRespawnLocations.put(key, config.getString(key + ".respawn.world") + "," +
                        config.getString(key + ".respawn.x") + "," +
                        config.getString(key + ".respawn.y") + "," +
                        config.getString(key + ".respawn.z") + "," +
                        config.getString(key + ".respawn.type"));

                List<PotionEffect> list = new ArrayList<>();
                if (config.get(key+".effects") != null) {
                    for (String potioneffect : config.getConfigurationSection(key + ".effects").getKeys(false)) {
                        list.add(new PotionEffect(PotionEffectType.getByName(potioneffect), config.getInt(key + ".effects." + potioneffect + ".duration"), config.getInt(key + ".effects." + potioneffect + ".amplifier")));
                    }
                    if (list.size() > 0) {
                        PotionEffect[] potionEffects = list.toArray(new PotionEffect[0]);
                        playerPotionEffects.put(key, potionEffects);
                    }
                }

            }
            file.delete();
        } catch (Exception ignored) {
            System.err.println(Main.consoleprefix +"PlayerDataManager: Fehler beim Laden der Spielerdaten");
        }
    }

    public void save()
    {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            boolean contains = false;
            for (Map.Entry<String, String> entry : playerLocations.entrySet()) {
                String[] location = entry.getValue().split(",");
                config.set(entry.getKey() + ".location.world", location[0]);
                config.set(entry.getKey() + ".location.x", location[1]);
                config.set(entry.getKey() + ".location.y", location[2]);
                config.set(entry.getKey() + ".location.z", location[3]);
                config.set(entry.getKey() + ".location.yaw",location[4]);
                config.set(entry.getKey() + ".location.pitch",location[5]);
                contains = true;
            }
            for (Map.Entry<String, String> entry : playerGamemodes.entrySet()) {
                config.set(entry.getKey() + ".gamemode", entry.getValue());
                contains = true;
            }
            for (Map.Entry<String,String> entry : playerHealth.entrySet()) {
                config.set(entry.getKey() + ".health",entry.getValue());
                contains = true;
            }
            for (Map.Entry<String,Integer> entry : playerHunger.entrySet()) {
                config.set(entry.getKey() + ".foodlevel",entry.getValue());
                contains = true;
            }
            for (Map.Entry<String,String> entry : playerXp.entrySet()) {
                config.set(entry.getKey() + ".xp",entry.getValue());
                contains = true;
            }
            for (Map.Entry<String, String> entry : playerRespawnLocations.entrySet()) {
                String[] location = entry.getValue().split(",");
                config.set(entry.getKey() + ".respawn.world", location[0]);
                config.set(entry.getKey() + ".respawn.x", location[1]);
                config.set(entry.getKey() + ".respawn.y", location[2]);
                config.set(entry.getKey() + ".respawn.z", location[3]);
                config.set(entry.getKey() + ".respawn.type", location[4]);
                contains = true;
            }
            for (Map.Entry<String, PotionEffect[]> entry : playerPotionEffects.entrySet()) {
                for (PotionEffect potionEffect : entry.getValue()) {
                    config.set(entry.getKey()+".effects."+potionEffect.getType().getName()+".duration",potionEffect.getDuration());
                    config.set(entry.getKey()+".effects."+potionEffect.getType().getName()+".amplifier",potionEffect.getAmplifier());
                    contains = true;
                }
            }
            for (Map.Entry<String, ItemStack[]> entry : playerInventories.entrySet()) {
                config.set(entry.getKey() + ".inventory", entry.getValue());
                contains = true;
            }

            if (contains) {
                System.out.println(Main.consoleprefix + file.getName() + " wird gespeichert.");
                config.save(file);
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
            System.err.println(Main.consoleprefix +"PlayerDataManager: Fehler beim Speichern der Spielerdaten");
        }
    }
}
