package de.coderr.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventorySeeCommand implements CommandExecutor, Listener
{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            Player p = (Player) sender;
            if (p.hasPermission("tablist.command.invact") || p.hasPermission("tablist.command.invsee"))
            {
                if (args.length == 1)
                {
                    if (Bukkit.getPlayer(args[0]).isOnline())
                    {
                        if (Bukkit.getPlayer(args[0]) != p) {
                            p.openInventory(Bukkit.getPlayer(args[0]).getInventory());

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    // TODO: Event wird nicht aktiviert

    @EventHandler
    public void onInvClick(InventoryClickEvent event)
    {
        for (Player a : Bukkit.getOnlinePlayers())
        {
            if (a.getInventory() == event.getInventory())
            {
                if (event.getClick() == ClickType.SHIFT_LEFT)
                {
                    event.setCancelled(true);
                }
                else if (event.getWhoClicked() != a)
                {
                    if (!event.getWhoClicked().hasPermission("tablist.command.invact"))
                    {
                        event.setCancelled(true);
                    }
                }
                break;
            }
        }
    }


}
