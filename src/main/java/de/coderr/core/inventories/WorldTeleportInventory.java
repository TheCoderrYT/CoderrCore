package de.coderr.core.inventories;

import de.coderr.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
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
import java.util.*;

public class WorldTeleportInventory implements Listener, CommandExecutor {

    private Inventory inv;
    private Map<Integer, World> slotWorldMap = new HashMap<>();
    private List<UUID> editingPlayers = new ArrayList<>();
    private File file = new File("plugins//CoderrCore//data//plugininventories.yml");
    private YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

    public WorldTeleportInventory() {
        if (!restoreInventory()) {
            setupInv();
        }
        File worldsfile = new File("plugins//CoderrCore//worlds.yml");
        YamlConfiguration worldsconfiguration = YamlConfiguration.loadConfiguration(worldsfile);
        for (World w : Bukkit.getWorlds()) {
            if (!w.getName().contains("_nether") && !w.getName().contains("_the_end")) {
                slotWorldMap.put(worldsconfiguration.getInt(w.getName()+".slot"),w);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == inv) {
            if (!editingPlayers.contains(event.getWhoClicked().getUniqueId())) {
                if (slotWorldMap.containsKey(event.getSlot())) {
                    Bukkit.dispatchCommand(event.getWhoClicked(), "world " + slotWorldMap.get(event.getSlot()).getName());
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        if (event.getInventory() == inv) {
            editingPlayers.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        for (HumanEntity a : inv.getViewers()) {
            a.closeInventory();
        }
    }

    private void setupInv() {
        inv = Bukkit.createInventory(null,3*9,"Welten");
        File worldsfile = new File("plugins//CoderrCore//worlds.yml");
        YamlConfiguration worldsconfiguration = YamlConfiguration.loadConfiguration(worldsfile);
        for (String worldname : worldsconfiguration.getKeys(false))
        {
            if (Bukkit.getWorld(worldname) != null) {
                if (worldname.equals(Main.instance.getConfig().getString("world.lobby"))) {
                    setItem(inv, worldsconfiguration.getInt(worldname + ".slot"), Material.COMPASS, Main.themecolor + "Lobby", ChatColor.GRAY + "(Rechtsklick zum Teleportieren)");
                } else if (worldname.equals(Main.instance.getConfig().getString("world.testworld"))) {
                    setItem(inv, worldsconfiguration.getInt(worldname + ".slot"), Material.SANDSTONE, Main.themecolor + "Testwelt", ChatColor.GRAY + "(Rechtsklick zum Teleportieren)");
                } else {
                    if(worldsconfiguration.getBoolean(worldname + ".teleport")) {
                        if (Bukkit.getPluginManager().isPluginEnabled("CoderrKnockFFA")) {
                            if (!Bukkit.getPluginManager().getPlugin("CoderrKnockFFA").getConfig().getString("worlds." + worldname).equals("false")) {
                                setItem(inv, worldsconfiguration.getInt(worldname + ".slot"), Material.STICK, Main.themecolor + worldname, ChatColor.GRAY + "(Rechtsklick zum Teleportieren)");
                            } else {
                                setItem(inv, worldsconfiguration.getInt(worldname + ".slot"), Material.GRASS_BLOCK, Main.themecolor + worldname, ChatColor.GRAY + "(Rechtsklick zum Teleportieren)");
                            }
                        } else {
                            setItem(inv, worldsconfiguration.getInt(worldname + ".slot"), Material.GRASS_BLOCK, Main.themecolor + worldname, ChatColor.GRAY + "(Rechtsklick zum Teleportieren)");
                        }
                    }
                }
            }
        }
        /*
        for (World w : Bukkit.getWorlds()) {
            if (!w.getName().contains("_nether") && !w.getName().contains("_the_end")) {
                boolean knockffa = false;
                boolean lobby = false;
                boolean testworld = false;
                if (w.getName().equals(Main.instance.getConfig().getString("world.lobby"))) {
                    lobby = true;
                }
                else if (w.getName().equals(Main.instance.getConfig().getString("world.testworld"))) {
                    testworld = true;
                }
                else if (Bukkit.getPluginManager().isPluginEnabled("CoderrKnockFFA")) {
                    if (!Bukkit.getPluginManager().getPlugin("CoderrKnockFFA").getConfig().getString("worlds." + w.getName()).equals("false")) {
                        knockffa = true;
                    }
                }
                if (lobby) {
                }
                else if (testworld) {
                }
                else if (knockffa) {
                    setItem(inv, slot, Material.STICK, Main.themecolor + w.getName(), ChatColor.GRAY + "(Rechtsklick zum Teleportieren)");
                } else {
                    setItem(inv, slot, Material.GRASS_BLOCK, Main.themecolor + w.getName(), ChatColor.GRAY + "(Rechtsklick zum Teleportieren)");
                }
                //slotWorldMap.put(slot,w);
                slot++;
            }
        }
*/
        setEmptySlots(inv);
    }

    public void saveInventory() {
        try {
            file.delete();
            configuration.set("worldteleporter.rows",inv.getSize() / 9);
            configuration.set("worldteleporter.content",inv.getContents());
            configuration.save(file);
        } catch (Exception e) {
            System.out.println(Main.consoleprefix + "WorldTeleporterInventory konnte nicht gespeichert werden.");
        }
    }

    public boolean restoreInventory() {
        if (!file.exists()) {
            return false;
        }
        try {
            inv = Bukkit.createInventory(null,configuration.getInt("worldteleporter.rows") * 9,"Welten");
            ItemStack[] content = ((List<ItemStack>) configuration.get("worldteleporter.content")).toArray(new ItemStack[0]);
            inv.setContents(content);
            return true;
        } catch (Exception e) {
            System.out.println(Main.consoleprefix + "WorldTeleporterInventory konnte nicht geladen werden.");
            return false;
        }
    }

    private void setItem(Inventory inv, int index, Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (material == Material.STICK) {
            meta.addEnchant(Enchantment.KNOCKBACK,1,true);
        }
        meta.setDisplayName(name);
        if (lore != null) { meta.setLore(Arrays.asList(lore.split("\n"))); }
        item.setItemMeta(meta);
        inv.setItem(index,item);
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                p.openInventory(inv);
            } else if (args.length == 1) {
                if (args[0].equals("edit")) {
                    if (p.hasPermission("coderrcore.rank.admin")) {
                        editingPlayers.add(p.getUniqueId());
                        p.openInventory(inv);
                    } else {
                        p.sendMessage(Main.ingameprefix + "Du hast nicht die passenden Rechte.");
                    }
                } else if (args[0].equals("reset")) {
                    if (p.hasPermission("coderrcore.rank.admin")) {
                        setupInv();
                        p.sendMessage(Main.ingameprefix + "Inventar wurde zurückgesetzt.");
                    } else {
                        p.sendMessage(Main.ingameprefix + "Du hast nicht die passenden Rechte.");
                    }
                }
            }
        }
        return true;
    }
}
