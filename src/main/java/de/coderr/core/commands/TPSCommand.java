package de.coderr.core.commands;

import de.coderr.core.manager.LagManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class TPSCommand implements CommandExecutor
{
    private int warningCooldown = 8;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0)
        {
            sender.sendMessage(getTPSMessage());
        }

        return true;
    }

    public String getTPSMessage()
    {
        double tps = LagManager.getTPS();
        DecimalFormat tpsFormat = new DecimalFormat("#.##");
        tps = Math.round( tps * 100 ) / 100.;

        if (tps < 17.5 || tps > 22) {
            if (warningCooldown == 0) {
                for (Player a : Bukkit.getOnlinePlayers()) {
                    a.sendMessage(ChatColor.YELLOW + "Warnung: Es entstehen Lags!");
                }
                warningCooldown = 8;
            }
            else
            {
                warningCooldown--;
            }
        }

        String msg = "";
        if (tps > 20)
        {
            msg = ChatColor.DARK_AQUA + tpsFormat.format(tps);
        }
        else if (tps == 20)
        {
            msg = ChatColor.DARK_GREEN + tpsFormat.format(tps);
        }
        else if (tps > 18.5 && tps < 20)
        {
            msg = ChatColor.GREEN + tpsFormat.format(tps);
        }
        else if (tps > 14 && tps <= 18.5)
        {
            msg = ChatColor.YELLOW + tpsFormat.format(tps);
        }
        else if (tps > 9 && tps <= 14)
        {
            msg = ChatColor.RED + tpsFormat.format(tps);
        }
        else if (tps <= 9)
        {
            msg = ChatColor.DARK_RED + tpsFormat.format(tps);
        }
        else
        {
            msg = ChatColor.RED + "TPS not found";
        }
        return msg;
    }
}
