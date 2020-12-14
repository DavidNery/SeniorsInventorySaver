package me.dery.seniorsinventorysaver.builders.itemstack.impl;

import me.dery.seniorsinventorysaver.builders.itemstack.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class SimpleItemStackBuilder extends ItemStackBuilder {

    protected final Material TYPE;

    protected byte itemData;

    protected HashMap<Enchantment, Integer> enchantments;

    public SimpleItemStackBuilder(Material type, byte data) {
        this.TYPE = type;
        itemData = data;

        enchantments = new HashMap();
    }

    public SimpleItemStackBuilder(Material type) {
        this(type, (byte) 0);
    }

    public SimpleItemStackBuilder withEnchantment(Enchantment enchant, int level) {
        enchantments.put(enchant, level);
        return this;
    }

    @Override
    public ItemStack build() {
        item = new ItemStack(TYPE, itemQuantity, itemData);

        Iterator<Entry<Enchantment, Integer>> it = enchantments.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Enchantment, Integer> entry = it.next();

            item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
        }

        return super.build();
    }

}
