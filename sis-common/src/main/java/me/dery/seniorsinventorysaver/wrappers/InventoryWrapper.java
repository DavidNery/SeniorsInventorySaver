package me.dery.seniorsinventorysaver.wrappers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Class that will wrap the inventory through different
 * minecraft servers builds (Spigot, Nukkit...)<br/>
 * <p>
 * For this project, it is unusable, because it is only for Spigot.
 * But it's good when you have a plugin that works in different MS builds
 */
public abstract class InventoryWrapper {

    private static final HashMap<String, Class<?>> registeredWrappers = new HashMap<>();

    protected final int id;
    protected final UUID owner;
    protected final String date;

    protected InventoryWrapper(int id, UUID owner, String date) {
        this.id = id;
        this.owner = owner;
        this.date = date;

        registeredWrappers.putIfAbsent(this.getClass().getSimpleName(), this.getClass());
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

    /**
     * @return the inventory Base64 serialized
     */
    public abstract Optional<String> getSerialized();

    protected abstract void parseSerialized(InputStream inputStream);

    public static void registerWrapper(Class<? extends InventoryWrapper> wrapper) {
        registeredWrappers.putIfAbsent(wrapper.getSimpleName(), wrapper);
    }

    public static void registerInventoryWrapper(String inventory, Class<? extends InventoryWrapper> wrapper) {
        registeredWrappers.putIfAbsent(inventory, wrapper);
    }

    /**
     * @return the InventoryWrapper
     */
    public static InventoryWrapper fromString(int id, UUID owner, String date, String base64EncodedInventory) throws IOException {
        try {
            byte[] decodedInventory = Base64.getDecoder().decode(base64EncodedInventory);

            ObjectInputStream dataInputStream = new ObjectInputStream(
                    new ByteArrayInputStream(decodedInventory)
            );

            Class<?> wrapperClass = registeredWrappers.get(dataInputStream.readUTF());
            if (wrapperClass == null) throw new RuntimeException("Wrapper not registered");

            Constructor<?> constructor = wrapperClass.getConstructor(int.class, UUID.class, String.class);
            InventoryWrapper wrapper = (InventoryWrapper) constructor.newInstance(id, owner, date);
            wrapper.parseSerialized(new ByteArrayInputStream(decodedInventory));

            dataInputStream.close();

            return wrapper;
        } catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new IOException("Failed to decode inventory");
        }
    }

    public static <I> InventoryWrapper fromInventory(int id, UUID owner, String date, I inventory) throws IOException {
        try {
            String wrapperName = inventory.getClass().getSimpleName();
            Class<?> wrapperClass = registeredWrappers.get(wrapperName);
            if (wrapperClass == null)
                throw new RuntimeException("Wrapper \"" + wrapperName + "\" not registered");

            Constructor<?> constructor = wrapperClass.getConstructors()[1];

            return (InventoryWrapper) constructor.newInstance(inventory, id, owner, date);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IOException("Failed to decode inventory due " + e.getCause().getMessage());
        }
    }

}
