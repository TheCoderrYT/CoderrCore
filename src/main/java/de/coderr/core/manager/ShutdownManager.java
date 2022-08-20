package de.coderr.core.manager;

import de.coderr.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalTime;
import java.util.Objects;

public class ShutdownManager
{
    public ShutdownManager()
    {
        Main main = Main.instance;
        if (!main.getConfig().contains("shutdown")) {
            main.getConfig().set("shutdown.enabled",false);
            main.getConfig().set("shutdown.time","24:00");
            main.saveConfig();
        }

    }

    public void checkShutdown()
    {
        int hour = LocalTime.now().getHour();
        int minute = LocalTime.now().getMinute();

        if(hour == getShutdownHour() && minute == getShutdownMinute())
        {
            Bukkit.shutdown();
        }
        else if(hour+1 == getShutdownHour() && minute == getShutdownMinute())
        {
            for (Player p : Bukkit.getOnlinePlayers())
            {
                p.sendMessage(Main.themecolor + "Der Server schließt in einer Stunde");
            }
        }
        else if(hour == getShutdownHour() && minute+5 == getShutdownMinute())
        {
            for (Player p : Bukkit.getOnlinePlayers())
            {
                p.sendMessage(Main.themecolor + "Der Server schließt in fünf Minuten");
            }
        }
        else if(hour == getShutdownHour() && minute+1 == getShutdownMinute())
        {
            for (Player p : Bukkit.getOnlinePlayers())
            {
                p.sendMessage(Main.themecolor + "Der Server schließt in einer Minuten");
            }
        }

    }

    private int getShutdownHour()
    {
        String[] str = Objects.requireNonNull(Main.instance.getConfig().getString("shutdown.time")).split(":");
        return Integer.parseInt(str[0]);
    }
    private int getShutdownMinute()
    {
        String[] str = Objects.requireNonNull(Main.instance.getConfig().getString("shutdown.time")).split(":");
        return Integer.parseInt(str[1]);
    }
}
