package de.coderr.core.listener;

import de.coderr.core.Main;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener implements Listener
{
    public EntityExplodeListener()
    {
        Main main = Main.instance;

        if (!main.getConfig().contains("settings.creeperBlockDamage"))
        {
            main.getConfig().set("settings.creeperBlockDamage",true);
            main.saveConfig();
        }
    }

    @EventHandler
    public void onMob(EntityExplodeEvent event)
    {
        if (!Main.instance.getConfig().getBoolean("settings.creeperBlockDamage"))
        {
            if (event.getEntity() instanceof Creeper) {
                event.setCancelled(true);
            }
        }
    }
}
