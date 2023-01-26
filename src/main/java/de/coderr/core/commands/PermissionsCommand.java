package de.coderr.core.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.HashSet;
import java.util.Set;

public class PermissionsCommand implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            Player p = (Player) sender;

            if (p.hasPermission("coderrcore.command.permissions"))
            {
                Set<PermissionAttachmentInfo> permissions = new HashSet<PermissionAttachmentInfo>(p.getEffectivePermissions());
                for (PermissionAttachmentInfo permissionInfo : permissions)
                {
                    String permission = permissionInfo.getPermission();
                    p.sendMessage(ChatColor.YELLOW + permission);
                }
            }
            else
            {
                p.sendMessage(ChatColor.RED + "Du hast nicht die passenden Rechte!");
            }

        }

        return true;
    }
}
