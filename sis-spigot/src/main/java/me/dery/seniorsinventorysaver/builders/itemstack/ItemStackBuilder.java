package me.dery.seniorsinventorysaver.builders.itemstack;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ItemStackBuilder {

    protected ItemStack item;

    protected String itemName;

    protected int itemQuantity = 1;

    protected List<String> itemLore;

    public ItemStackBuilder withName(String name) {
        if (name != null)
            itemName = ChatColor.translateAlternateColorCodes('&', name);

        return this;
    }

    public ItemStackBuilder withLore(List<String> lore) {
        itemLore = lore.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toList());

        return this;
    }

    public ItemStackBuilder withLore(String... lore) {
        withLore(Arrays.asList(lore));

        return this;
    }

    public ItemStackBuilder withQuantity(int quantity) {
        itemQuantity = quantity;

        return this;
    }

    public ItemStack build() {
        ItemMeta meta = item.getItemMeta();
        if (itemName != null)
            meta.setDisplayName(itemName);
        if (itemLore != null && itemLore.size() >= 1)
            meta.setLore(itemLore);

        item.setItemMeta(meta);

        return item;
    }

}
