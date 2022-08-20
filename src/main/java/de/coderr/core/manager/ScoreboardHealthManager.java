package de.coderr.core.manager;

import de.coderr.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardHealthManager implements Listener
{
    public ScoreboardManager sm;
    public Scoreboard board;
    public Objective obj;
    private final boolean healthVisible;

    public ScoreboardHealthManager()
    {
        Main main = Main.instance;
        if(!main.getConfig().contains("settings.healthVisible"))
        {
            main.getConfig().set("settings.healthVisible", true);
            main.saveConfig();
        }
        healthVisible = main.getConfig().getBoolean("settings.healthVisible");

        sm = Bukkit.getScoreboardManager();
        assert sm != null;
        board = sm.getNewScoreboard();
        obj = board.registerNewObjective("aaa","bbb");
        obj.setDisplayName(ChatColor.RED + "♥");
        obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    public void updateScoreboard(Player p, int heart)
    {
        obj.getScore(p).setScore(heart);
        p.setScoreboard(board);
    }

    public int getHealth(Player p)
    {
        return (int) StrictMath.ceil(getDamageable(p).getHealth());
    }

    public Damageable getDamageable(Player p)
    {
        return p;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event)
    {
        if (healthVisible)
        {
            if (event.getEntity() instanceof Player)
            {
                Player p = (Player) event.getEntity();
                Main.scoreboardHealthManager.updateScoreboard(p,(int) (Main.scoreboardHealthManager.getHealth(p) - event.getDamage()));
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        if (healthVisible)
        {
            Player p = event.getPlayer();

            Main.scoreboardHealthManager.updateScoreboard(p, Main.scoreboardHealthManager.getHealth(p));
        }
    }

    @EventHandler
    public void onRegain(EntityRegainHealthEvent event)
    {
        if (healthVisible)
        {
            if (event.getEntity() instanceof Player)
            {
                Player p = (Player) event.getEntity();
                Main.scoreboardHealthManager.updateScoreboard(p,(int) (Main.scoreboardHealthManager.getHealth(p) + event.getAmount()));
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event)
    {
        if (healthVisible)
        {
            Main.scoreboardHealthManager.updateScoreboard(event.getPlayer(), 20);
        }
    }

}
