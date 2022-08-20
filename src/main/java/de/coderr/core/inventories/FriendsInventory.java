package de.coderr.core.inventories;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public class FriendsInventory implements CommandExecutor, Listener
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED+"Command noch nicht verfügbar");
        return true;
    }
}
