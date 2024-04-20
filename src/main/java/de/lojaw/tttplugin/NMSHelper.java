package de.lojaw.tttplugin;

import com.comphenix.protocol.events.PacketContainer;
import com.mojang.datafixers.util.Pair;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class NMSHelper {

    @SneakyThrows
    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = getHandle(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Fehler beim Senden des Pakets", e);
        }
    }

    @SneakyThrows
    public static Object getHandle(Player player) {
        try {
            return player.getClass().getMethod("getHandle").invoke(player);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Fehler beim Abrufen des Handles", e);
        }
    }

    @SneakyThrows
    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("NMS-Klasse nicht gefunden: " + name, e);
        }
    }

    @SneakyThrows
    public static Class<?> getCraftBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("CraftBukkit-Klasse nicht gefunden: " + name, e);
        }
    }

    public static String getVersion() {
        // org.bukkit.craftbukkit.v1_16_R2.CraftServer
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    private static Method minecraftItemStack = null;

    @SneakyThrows
    public static Object toMinecraftItemStack(ItemStack itemStack) {
        try {
            if (minecraftItemStack == null) {
                minecraftItemStack = NMSHelper.getCraftBukkitClass("inventory.CraftItemStack")
                        .getMethod("asNMSCopy", ItemStack.class);
            }
            return minecraftItemStack.invoke(null, itemStack);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Fehler beim Konvertieren des ItemStacks", e);
        }
    }

    public static void setEquipmentInPacket(PacketContainer packet, ItemSlotConverter slot, ItemStack item) {
        Object nmsItemStack = toMinecraftItemStack(item);
        List<Pair<Object, Object>> pairList = packet.getSpecificModifier(List.class).read(0);

        for (Pair<Object, Object> pair : pairList) {
            if (pair.getFirst().equals(slot.toNMSEnum())) {
                pairList.remove(pair);
                break;
            }
        }

        pairList.add(new Pair<>(slot.toNMSEnum(), nmsItemStack));
        packet.getSpecificModifier(List.class).write(0, pairList);
    }
}