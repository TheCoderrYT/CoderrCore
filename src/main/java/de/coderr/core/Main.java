package de.coderr.core;

import de.coderr.core.commands.InventorySeeCommand;
import de.coderr.core.commands.PermissionsCommand;
import de.coderr.core.commands.PositionCommand;
import de.coderr.core.commands.TPSCommand;
import de.coderr.core.inventories.FriendsInventory;
import de.coderr.core.inventories.LobbyInventory;
import de.coderr.core.inventories.WorldSettingsInventory;
import de.coderr.core.inventories.WorldTeleportInventory;
import de.coderr.core.listener.*;
import de.coderr.core.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Main extends JavaPlugin {
    public static Main instance;
    public static boolean maintrance;
    public static ChatColor themecolor;
    public static ChatColor fontcolor;
    public static String consoleprefix;
    public static String ingameprefix;
    private String startuptime;

    public static WorldManager worldManager;
    public static RankManager rankManager;
    public static AFKListener afkListener;
    public static ShutdownManager shutdownManager;
    public static LagManager lagManager;
    public static MinecraftCommandManager minecraftCommandManager;
    public static TeleporterManager teleporterManager;
    public static TablistManager tablistManager;
    public static ScoreboardHealthManager scoreboardHealthManager;

    public static InventorySeeCommand inventorySeeCommand;
    public static TPSCommand tpsCommand;
    public static PermissionsCommand permissionsCommand;
    public static PositionCommand positionCommand;

    public static EntityExplodeListener entityExplodeListener;
    public static JoinListener joinListener;
    public static LeaveListener leaveListener;
    public static StopReloadListener stopReloadListener;
    public static SignListener signListener;
    public static BedListener bedListener;
    public static ChatListener chatListener;

    public static WorldSettingsInventory worldSettingsInventory;
    public static LobbyInventory lobbyInventory;
    public static WorldTeleportInventory worldTeleportInventory;
    public static FriendsInventory friendsInventory;

    // Jump and Run Extension  ✓✓✓
    // Timer Extension ✓✓✓
    // Webside Extension
    // Spawnplatform Extension

    @Override
    public void onEnable() {
        instance = this;
        maintrance = false;

        List<String> header = new ArrayList<>();
        header.add("*** CoderrCore Configuration ***");
        header.add(" ------------------------------ ");
        header.add("* theme.primarycolor: Defines the highlight color of chat outputs and other gui text outputs. (Default: GOLD)");
        header.add("* theme.fontcolor: Defines the default fontcolor of chat outputs and other gui text outputs. (Default: GRAY)");
        header.add("* world.testworld: Defines the worldname of the testworld, which can configurate in worlds.yml. (Default: testworld)");
        header.add("* world.lobby: Defines the worldname of the lobby, which can configurate in lobby.yml. (Default: lobby)");
        header.add("* tablist.header: Defines the text above the playerlist in the tablist. (Default: &t&lFlorian´s Servernetzwerk");
        header.add("* tablist.footer: Defines the text below the playerlist in the tablist. (Default: &8>>> &r&t&n&owww.foerster.cologne &r&8<<<");
        header.add("* settings.timeToAFK: Defines the time when a player should display as AFK when he don´t move. (Default: 5)");
        header.add("* settings.healthVisible: Toggle if the health of a player should display above his head. (Default: true");
        header.add("* settings.sleepingMessage: Toggle if other players should inform when a player sleep. (Default: true");
        header.add("* settings.creeperBlockDamage: Toggle if creeper can destroy blocks. (Default: true)");
        header.add("* shutdown.enabled: Toggle if the server should shut down at a special time. (Deafult: false)");
        header.add("* shutdown.time: Defines the time when the server shuts down if it is enabled. (Default: 24:00");
        header.add(" ------------------------------ ");
        StringBuilder headerString = new StringBuilder();
        for (String headerLine : header) {
            headerString.append(headerLine).append("\n");
        }
        this.getConfig().options().header(headerString.toString());
        this.saveConfig();
        if (!this.getConfig().contains("theme")) {
            this.getConfig().set("theme.primarycolor","GOLD");
            this.getConfig().set("theme.fontcolor","GRAY");
            this.saveConfig();
        }

        themecolor = ChatColor.valueOf(this.getConfig().getString("theme.primarycolor"));
        fontcolor = ChatColor.valueOf(this.getConfig().getString("theme.fontcolor"));
        consoleprefix = "[CoderrCore] ";
        ingameprefix = ChatColor.DARK_GRAY + "[" + themecolor + "Server" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;

        DateTimeFormatter dtf4 = DateTimeFormatter.ofPattern("HH:mm");
        startuptime = dtf4.format(LocalDateTime.now());

        worldManager = new WorldManager();
        tablistManager = new TablistManager();
        scoreboardHealthManager = new ScoreboardHealthManager();
        rankManager = new RankManager();
        shutdownManager = new ShutdownManager();
        afkListener = new AFKListener();
        bedListener = new BedListener();
        inventorySeeCommand = new InventorySeeCommand();
        entityExplodeListener = new EntityExplodeListener();
        tpsCommand = new TPSCommand();
        joinListener = new JoinListener();
        leaveListener = new LeaveListener();
        chatListener = new ChatListener();
        stopReloadListener = new StopReloadListener();
        positionCommand = new PositionCommand();
        permissionsCommand = new PermissionsCommand();
        lagManager = new LagManager();
        minecraftCommandManager = new MinecraftCommandManager();
        worldSettingsInventory = new WorldSettingsInventory();
        teleporterManager = new TeleporterManager();
        lobbyInventory = new LobbyInventory();
        worldTeleportInventory = new WorldTeleportInventory();
        friendsInventory = new FriendsInventory();
        signListener = new SignListener();
        worldTeleportInventory = new WorldTeleportInventory();

        Bukkit.getPluginManager().registerEvents(joinListener,this);
        Bukkit.getPluginManager().registerEvents(leaveListener,this);
        Bukkit.getPluginManager().registerEvents(chatListener,this);
        Bukkit.getPluginManager().registerEvents(afkListener,this);
        if(this.getConfig().getBoolean("settings.sleepingMessage")) {
            Bukkit.getPluginManager().registerEvents(bedListener,this); }
        Bukkit.getPluginManager().registerEvents(stopReloadListener,this);
        Bukkit.getPluginManager().registerEvents(inventorySeeCommand,this);
        Bukkit.getPluginManager().registerEvents(entityExplodeListener,this);
        Bukkit.getPluginManager().registerEvents(minecraftCommandManager,this);
        Bukkit.getPluginManager().registerEvents(worldManager,this);
        Bukkit.getPluginManager().registerEvents(worldSettingsInventory,this);
        Bukkit.getPluginManager().registerEvents(tablistManager,this);
        Bukkit.getPluginManager().registerEvents(teleporterManager,this);
        Bukkit.getPluginManager().registerEvents(lobbyInventory,this);
        Bukkit.getPluginManager().registerEvents(worldTeleportInventory,this);
        Bukkit.getPluginManager().registerEvents(friendsInventory,this);
        Bukkit.getPluginManager().registerEvents(signListener,this);

        Objects.requireNonNull(this.getCommand("world")).setExecutor(worldManager);
        Objects.requireNonNull(this.getCommand("world")).setTabCompleter(worldManager);
        Objects.requireNonNull(this.getCommand("lobby")).setExecutor(worldManager);
        Objects.requireNonNull(this.getCommand("l")).setExecutor(worldManager);
        Objects.requireNonNull(this.getCommand("testworld")).setExecutor(worldManager);

        Objects.requireNonNull(this.getCommand("pos")).setExecutor(positionCommand);
        Objects.requireNonNull(this.getCommand("pos")).setTabCompleter(positionCommand);
        Objects.requireNonNull(this.getCommand("rank")).setExecutor(rankManager);
        Objects.requireNonNull(this.getCommand("rank")).setTabCompleter(rankManager);
        Objects.requireNonNull(this.getCommand("permissions")).setExecutor(permissionsCommand);
        Objects.requireNonNull(this.getCommand("invsee")).setExecutor(inventorySeeCommand);
        Objects.requireNonNull(this.getCommand("settings")).setExecutor(worldSettingsInventory);
        Objects.requireNonNull(this.getCommand("defaultgamemode")).setExecutor(worldSettingsInventory);
        Objects.requireNonNull(this.getCommand("teleporter")).setExecutor(teleporterManager);
        Objects.requireNonNull(this.getCommand("worlds")).setExecutor(worldTeleportInventory);
        Objects.requireNonNull(this.getCommand("friend")).setExecutor(friendsInventory);
        Objects.requireNonNull(this.getCommand("maintrance")).setExecutor(rankManager);


        afkListener.setRun(true);
        afkListener.startTimer();
        if (getConfig().getBoolean("settings.healthVisible")) {
            for (Player a : Bukkit.getOnlinePlayers()) {
                scoreboardHealthManager.updateScoreboard(a, scoreboardHealthManager.getHealth(a));
            }
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, lagManager,100L,1L);

    }

    @Override
    public void onDisable() {
        worldManager.onDisable();
        for (Player a : Bukkit.getOnlinePlayers()) {
            a.kickPlayer(themecolor + "Der Server fährt nun herunter.\n" + themecolor + "Die aktuellen Onlinezeiten sind: " + startuptime + " - " + this.getConfig().getString("shutdown.time"));
        }
        afkListener.setRun(false);
        worldManager.store();
        tablistManager.stopTimer();
    }
}
