package de.coderr.core.listener;

import de.coderr.core.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener
{
    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        event.setQuitMessage(Main.themecolor + event.getPlayer().getName() + " hat den Server verlassen");

    }
}
