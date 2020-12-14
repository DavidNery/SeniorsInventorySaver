package me.dery.seniorsinventorysaver.builders.itemstack.impl;

import me.dery.seniorsinventorysaver.builders.itemstack.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class HeadBuilder extends ItemStackBuilder {

    private final String owner;

    public HeadBuilder(String owner) {
        this.owner = owner;
    }

    @Override
    public ItemStack build() {
        item = new ItemStack(Material.SKULL_ITEM, itemQuantity, (byte) 3);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(owner);

        item.setItemMeta(meta);

        return super.build();
    }

}
