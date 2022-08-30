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
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WorldTeleportInventory implements Listener, CommandExecutor {

    private Inventory inv;
    private Map<Integer, World> slotWorldMap = new HashMap<>();

    public WorldTeleportInventory() {
        setupInv();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == inv) {
            if (slotWorldMap.containsKey(event.getSlot())) {
                Bukkit.dispatchCommand(event.getWhoClicked(), "world " + slotWorldMap.get(event.getSlot()).getName());
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        for (HumanEntity a : inv.getViewers()) {
            a.closeInventory();
        }
    }

    public Inventory getInventory() {
        setupInv();
        return inv;
    }

    private void setupInv() {
        inv = Bukkit.createInventory(null,3*9,"Welten");
        int slot = 10;

        setItem(inv,slot,Material.COMPASS, Main.themecolor+"Lobby",ChatColor.GRAY+"(Rechtsklick zum Teleportieren)");
        slotWorldMap.put(slot,Bukkit.getWorld(Main.instance.getConfig().getString("world.lobby")));
        slot++;
        if (Bukkit.getWorld(Main.instance.getConfig().getString("world.testworld")) != null) {
            setItem(inv,slot,Material.SANDSTONE,Main.themecolor+"Testwelt",ChatColor.GRAY+"(Rechtsklick zum Teleportieren)");
            slotWorldMap.put(slot,Bukkit.getWorld(Main.instance.getConfig().getString("world.testworld")));
            slot++;
        }
        slot++;

        for (World w : Bukkit.getWorlds()) {
            if (!w.getName().contains("_nether") && !w.getName().contains("_the_end") && !w.getName().equals(Main.instance.getConfig().getString("world.lobby")) && !w.getName().equals(Main.instance.getConfig().getString("world.testworld"))) {
                boolean knockffa = false;
                if (Bukkit.getPluginManager().isPluginEnabled("CoderrKnockFFA")) {
                    if (!Bukkit.getPluginManager().getPlugin("CoderrKnockFFA").getConfig().getString("worlds." + w.getName()).equals("false")) {
                        knockffa = true;
                    }
                }
                if (knockffa) {
                    setItem(inv, slot, Material.STICK, Main.themecolor + w.getName(), ChatColor.GRAY + "(Rechtsklick zum Teleportieren)");
                } else {
                    setItem(inv, slot, Material.GRASS_BLOCK, Main.themecolor + w.getName(), ChatColor.GRAY + "(Rechtsklick zum Teleportieren)");
                }
                slotWorldMap.put(slot,w);
                slot++;
            }
        }

        setEmptySlots(inv);
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
            p.openInventory(getInventory());
        }
        return true;
    }
}
