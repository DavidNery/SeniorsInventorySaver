package me.dery.seniorsinventorysaver.commands;

import me.dery.seniorsinventorysaver.SeniorsInventorySaver;
import me.dery.seniorsinventorysaver.managers.InventoryManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InventoriesCommand extends Command {

    private final InventoryManager inventoryManager;

    public InventoriesCommand(SeniorsInventorySaver instance) {
        super("inventories");
        setDescription("See loaded players' inventories");

        this.inventoryManager = instance.getInventoryManager();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if (!(sender instanceof Player)) return false;

        if (!sender.hasPermission("inventorysaver.view")) {
            sender.sendMessage("§c§lINVENTORYSAVER §7You don't have permission to see inventories.");
            return false;
        }
        
        inventoryManager.openMainInventory((Player) sender, 1);

        return true;
    }
}
