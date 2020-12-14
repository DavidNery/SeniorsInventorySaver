package me.dery.seniorsinventorysaver;

import me.dery.seniorsinventorysaver.commands.InventoriesCommand;
import me.dery.seniorsinventorysaver.listeners.ChatListeners;
import me.dery.seniorsinventorysaver.listeners.InventoryListeners;
import me.dery.seniorsinventorysaver.listeners.PlayerListeners;
import me.dery.seniorsinventorysaver.managers.InventoryManager;
import me.dery.seniorsinventorysaver.managers.PlayerManager;
import me.dery.seniorsinventorysaver.managers.impl.PlayerManagerImpl;
import me.dery.seniorsinventorysaver.runnables.PlayerSaverRunnable;
import me.dery.seniorsinventorysaver.storage.IStorage;
import me.dery.seniorsinventorysaver.storage.impl.MySQLStorage;
import me.dery.seniorsinventorysaver.storage.models.InventoryStorage;
import me.dery.seniorsinventorysaver.storage.models.PlayerStorage;
import me.dery.seniorsinventorysaver.wrappers.InventoryWrapper;
import me.dery.seniorsinventorysaver.wrappers.impl.PlayerInventoryWrapper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

public class SeniorsInventorySaver extends JavaPlugin {

    private IStorage storage;
    private PlayerStorage playerStorage;
    private InventoryStorage inventoryStorage;

    private PlayerManager playerManager;
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        final ConsoleCommandSender sender = getServer().getConsoleSender();

        if (!new File(getDataFolder(), "config.yml").exists()) {
            sender.sendMessage("First start time of plugin.");
            sender.sendMessage("Please, configure your MySQL settings in config.yml");
            saveDefaultConfig();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        sender.sendMessage("Initializing plugin...");

        long startTime = System.currentTimeMillis();
        ConfigurationSection mysqlSection = getConfig().getConfigurationSection("MySQL");
        try {
            sender.sendMessage("Trying to connect to MySQL...");
            storage = new MySQLStorage(
                    mysqlSection.getString("host"), mysqlSection.getString("database"),
                    mysqlSection.getString("user"), mysqlSection.getString("password")
            );
            storage.openConnection();

            this.playerStorage = new PlayerStorage(storage);
            this.inventoryStorage = new InventoryStorage(storage);

            storage.closeConnection(true);
            sender.sendMessage("Successfully connected!");
        } catch (SQLException | ClassNotFoundException e) {
            sender.sendMessage("Failed to connect to MySQL due: " + e.getCause().getMessage());
            sender.sendMessage("Have you configured correctly your credentials?");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        InventoryWrapper.registerWrapper(PlayerInventoryWrapper.class);
        InventoryWrapper.registerInventoryWrapper("CraftInventoryPlayer", PlayerInventoryWrapper.class);

        playerManager = new PlayerManagerImpl(this);
        inventoryManager = new InventoryManager(this);

        registerCommands();
        registerListeners();
        registerRunnables();

        sender.sendMessage(String.format("Plugin took %dms to initialize.", System.currentTimeMillis() - startTime));
    }

    @Override
    public void onDisable() {
        try {
            if (storage != null) {
                storage.openConnection();
                inventoryStorage.removeOldInventories(getConfig().getInt("RemoveInventoriesAfter"));
                storage.closeConnection(true);
            }
        } catch (SQLException | ClassNotFoundException ignored) {
        }
    }

    public PlayerStorage getPlayerStorage() {
        return playerStorage;
    }

    public InventoryStorage getInventoryStorage() {
        return inventoryStorage;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    private void registerCommands() {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            Class<?> craftServer = Class.forName("org.bukkit.craftbukkit." + version + ".CraftServer");
            Method getCommandMap = craftServer.getDeclaredMethod("getCommandMap");

            CommandMap commandMap = (CommandMap) getCommandMap.invoke(getServer());
            commandMap.register(getDescription().getName(), new InventoriesCommand(this));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void registerListeners() {
        new PlayerListeners(this);
        new InventoryListeners(this);
        new ChatListeners(this);
    }

    private void registerRunnables() {
        // save player inventory each 60 seconds
        new PlayerSaverRunnable(this).runTaskTimerAsynchronously(this, 20 * 60, 20 * 60);
    }
}
