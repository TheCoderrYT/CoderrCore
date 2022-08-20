package de.coderr.core.inventories;

import de.coderr.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class LobbyInventory implements Listener
{
    private final Map<Player, ItemStack[]> invs = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (containsItem(event.getPlayer(),event.getPlayer().getItemInHand())) {
                Player p = event.getPlayer();
                int slot = 0;
                for (ItemStack i : p.getInventory().getContents()) {
                    if (i != null && event.getPlayer().getItemInHand() != null) {
                        if (i.getItemMeta().getDisplayName().equals(event.getPlayer().getItemInHand().getItemMeta().getDisplayName())) {
                            break;
                        }
                    }
                    slot++;
                }
                if (slot == 0) {
                    Bukkit.dispatchCommand(p,"worlds");
                } else if (slot == 8) {
                    Bukkit.dispatchCommand(p,"friend");
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (containsItem((Player) event.getWhoClicked(),event.getCurrentItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (containsItem(event.getPlayer(),event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getWorld() == Bukkit.getWorld(Main.instance.getConfig().getString("world.lobby"))) {
            setInv(event.getPlayer());
        }
    }

    public void setInv(Player p) {
        setupInv(p);
        p.getInventory().setContents(invs.get(p));
    }

    public void setupInv(Player p) {
        invs.put(p,new ItemStack[41]);
        ItemStack[] inv = invs.get(p);

        inv[0] = getItem(Main.themecolor+"Teleporter"+ChatColor.GRAY+" (Rechtsklick zum Öffnen)",1,Material.COMPASS,ChatColor.GRAY+"Benutze dieses Item um dich in\n"+ChatColor.GRAY+"eine andere Welt zu teleportieren.");

        ItemStack item = new ItemStack(Material.PLAYER_HEAD,1,(byte)3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(p.getName());
        meta.setDisplayName(Main.themecolor+"Freunde"+ChatColor.GRAY+" (Rechtsklick zum Öffnen)");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"Benutze dieses Item um");
        lore.add(ChatColor.GRAY+"das Freundemenü zu öffnen.");
        lore.add(ChatColor.DARK_RED+"(Noch nicht verfügbar)");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv[8] = item;
        invs.put(p,inv);

    }

    public ItemStack getItem(String displayName, int amount, Material type, String lore) {
        ItemStack item = new ItemStack(type,amount);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(displayName);
        if (lore != null) { meta.setLore(Arrays.asList(lore.split("\n"))); }
        item.setItemMeta(meta);
        return item;
    }

    public boolean containsItem(Player p, ItemStack itemStack) {
        if (invs.containsKey(p)) {
            for (ItemStack i : invs.get(p)) {
                if (i != null && itemStack != null) {
                    if (i.getItemMeta() != null && itemStack.getItemMeta() != null) {
                        if (i.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
