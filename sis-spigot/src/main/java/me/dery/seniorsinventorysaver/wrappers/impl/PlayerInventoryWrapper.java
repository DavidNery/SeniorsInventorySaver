package me.dery.seniorsinventorysaver.wrappers.impl;

import me.dery.seniorsinventorysaver.wrappers.InventoryWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class PlayerInventoryWrapper extends InventoryWrapper {

    private ItemStack[] armorContents, inventoryContents;

    public PlayerInventoryWrapper(int id, UUID owner, String date) {
        super(id, owner, date);
    }

    public PlayerInventoryWrapper(PlayerInventory inventory, int id, UUID owner, String date) {
        this(id, owner, date);

        this.armorContents = inventory.getArmorContents();
        this.inventoryContents = inventory.getContents();
    }

    public int getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getDate() {
        return date;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }

    public ItemStack[] getInventoryContents() {
        return inventoryContents;
    }

    @Override
    public Optional<String> getSerialized() {
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(byteOutputStream);

            objectOutputStream.writeUTF("PlayerInventoryWrapper");
            objectOutputStream.writeObject(armorContents);
            objectOutputStream.writeObject(inventoryContents);

            Optional<String> optional = Optional.of(Base64.getEncoder().encodeToString(byteOutputStream.toByteArray()));

            byteOutputStream.close();
            objectOutputStream.close();

            return optional;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    protected void parseSerialized(InputStream inputStream) {
        try {
            BukkitObjectInputStream stream = new BukkitObjectInputStream(inputStream);
            stream.readUTF();
            this.armorContents = (ItemStack[]) stream.readObject();
            this.inventoryContents = (ItemStack[]) stream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
