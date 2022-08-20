package de.coderr.core.manager;

import de.coderr.core.Main;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;

public class MinecraftCommandManager implements Listener
{
    private final Map<Player, String> lastTeleportCommand = new HashMap<>();

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
            if (event.getTo().getWorld() != event.getFrom().getWorld()) {
                Player p = event.getPlayer();
                if (lastTeleportCommand.containsKey(p)) {
                    boolean ignore = false;
                    String[] args = lastTeleportCommand.get(p).split(" ");
                    for (String arg : args) {
                        if (arg.equalsIgnoreCase("multidimensional")) {
                            ignore = true;
                        }
                    }
                    if (!ignore) {
                        ComponentBuilder message = new ComponentBuilder(ChatColor.GRAY + "" +ChatColor.ITALIC + "[Hier klicken, wenn wirklich multidimensional teleportiert werden soll]");
                        BaseComponent[] msg = message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, lastTeleportCommand.get(p) + " multidimensional")).create();
                        event.getPlayer().spigot().sendMessage(msg);
                        event.setCancelled(true);
                    }
                } else {
                    event.getPlayer().sendMessage(ChatColor.RED + "Nicht akzeptierter Teleportcommand");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onGamemodeChange(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().contains("/gamemode")) {
            String[] args = event.getMessage().split(" ");
            try {
                if (Bukkit.getPlayer(args[2]).isOnline()) {
                    if (!event.getPlayer().isOp()) {
                        event.setCancelled(true);
                    }
                }
            } catch (Exception ignored) { }
        } else if (event.getMessage().contains("/kill")) {
            String[] args = event.getMessage().split(" ");
            if (args.length >= 2) {
                if (args[1].equalsIgnoreCase("@e")) {
                    event.setCancelled(true);
                    World w = event.getPlayer().getWorld();
                    for (Entity e : w.getEntities()) {
                        if (e instanceof Mob) {
                            Mob m = (Mob) e;
                            m.setHealth(0);
                        } else if (e instanceof HumanEntity) {
                            HumanEntity h = (HumanEntity) e;
                            h.setHealth(0);
                        } else {
                            e.remove();
                        }
                    }
                    event.getPlayer().sendMessage(Main.ingameprefix + Main.fontcolor + "Alle Entities wurden in dieser Welt gelöscht.");
                }
            }
        } else if (event.getMessage().contains("/teleport")) {
            lastTeleportCommand.put(event.getPlayer(),event.getMessage());
        }
    }
}
