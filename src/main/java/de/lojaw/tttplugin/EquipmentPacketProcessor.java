package de.lojaw.tttplugin;

import com.comphenix.protocol.events.PacketEvent;
import com.mojang.datafixers.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class EquipmentPacketProcessor {
    protected abstract void process(PacketEvent event);

    /**
     * Re-sends the PacketPlayOutEntityEquipment packet to all players, for this particular player
     * @param player The player to send the equipment packet on behalf of
     */
    public static void refreshEquipmentOfPlayerForAllPlayers(Player player) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Pair<Object, Object>> equipmentPairList = new ArrayList<>();
        Constructor<?> packetConstructor = NMSHelper.getNMSClass("PacketPlayOutEntityEquipment")
                .getDeclaredConstructor(int.class, List.class);

        for (ItemSlotConverter slotConverter : ItemSlotConverter.values()) {
            equipmentPairList.add(new Pair<>(
                    slotConverter.toNMSEnum(),
                    slotConverter.getMinecraftItemStack(player)
            ));
        }

        Object packet = packetConstructor.newInstance(player.getEntityId(), equipmentPairList);

        Bukkit.getOnlinePlayers().forEach(online -> {
            if (online.equals(player)) return;
            NMSHelper.sendPacket(online, packet);
        });
    }

    public static void refreshEquipmentOfAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            try {
                refreshEquipmentOfPlayerForAllPlayers(player);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public void onEnable() {
        refreshEquipmentOfAllPlayers();
    }
}