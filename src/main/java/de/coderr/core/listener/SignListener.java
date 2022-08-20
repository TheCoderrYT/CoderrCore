package de.coderr.core.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener
{
    @EventHandler
    public void onSign(SignChangeEvent event) {
        int counter = 0;
        if (event.getLine(0).equals("&info")) {
            event.setLine(0,ChatColor.UNDERLINE+"Color Codes");
            event.setLine(1,ChatColor.BLACK+"&0 "
                    +ChatColor.DARK_BLUE+"&1 "
                    +ChatColor.DARK_GREEN+"&2 "
                    +ChatColor.DARK_AQUA+"&3 "
                    +ChatColor.DARK_RED+"&4");
            event.setLine(2,ChatColor.DARK_PURPLE+"&5 "
                    +ChatColor.GOLD+"&6 "
                    +ChatColor.GRAY+"&7 "
                    +ChatColor.DARK_GRAY+"&8 "
                    +ChatColor.GREEN+"&a");
            event.setLine(3,ChatColor.AQUA+"&b "
                    +ChatColor.RED+"&c "
                    +ChatColor.LIGHT_PURPLE+"&d "
                    +ChatColor.YELLOW+"&e "
                    +ChatColor.WHITE+"&f");
        } else {
            for (String line : event.getLines()) {
                if (line.contains("&0")) {
                    line = line.replaceAll("&0", ChatColor.BLACK + "");
                }
                if (line.contains("&1")) {
                    line = line.replaceAll("&1", ChatColor.DARK_BLUE + "");
                }
                if (line.contains("&2")) {
                    line = line.replaceAll("&2", ChatColor.DARK_GREEN + "");
                }
                if (line.contains("&3")) {
                    line = line.replaceAll("&3", ChatColor.DARK_AQUA + "");
                }
                if (line.contains("&4")) {
                    line = line.replaceAll("&4", ChatColor.DARK_RED + "");
                }
                if (line.contains("&5")) {
                    line = line.replaceAll("&5", ChatColor.DARK_PURPLE + "");
                }
                if (line.contains("&6")) {
                    line = line.replaceAll("&6", ChatColor.GOLD + "");
                }
                if (line.contains("&7")) {
                    line = line.replaceAll("&7", ChatColor.GRAY + "");
                }
                if (line.contains("&8")) {
                    line = line.replaceAll("&8", ChatColor.DARK_GRAY + "");
                }
                if (line.contains("&a")) {
                    line = line.replaceAll("&a", ChatColor.GREEN + "");
                }
                if (line.contains("&b")) {
                    line = line.replaceAll("&b", ChatColor.AQUA + "");
                }
                if (line.contains("&c")) {
                    line = line.replaceAll("&c", ChatColor.RED + "");
                }
                if (line.contains("&d")) {
                    line = line.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
                }
                if (line.contains("&e")) {
                    line = line.replaceAll("&e", ChatColor.YELLOW + "");
                }
                if (line.contains("&f")) {
                    line = line.replaceAll("&f", ChatColor.WHITE + "");
                }


                event.setLine(counter, line);
                counter++;
            }
        }
    }
}
