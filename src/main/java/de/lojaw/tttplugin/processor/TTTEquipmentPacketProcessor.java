package de.lojaw.tttplugin.processor;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.mojang.datafixers.util.Pair;
import de.lojaw.tttplugin.EquipmentPacketProcessor;
import de.lojaw.tttplugin.ItemSlotConverter;
import de.lojaw.tttplugin.NMSHelper;
import de.lojaw.tttplugin.TTTPlugin_1_16_2;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public class TTTEquipmentPacketProcessor extends EquipmentPacketProcessor {
    private final Player tttPlayer;
    private final TTTPlugin_1_16_2 plugin;

    public TTTEquipmentPacketProcessor(Player tttPlayer, TTTPlugin_1_16_2 plugin) {
        this.tttPlayer = tttPlayer;
        this.plugin = plugin;
    }

    @Override
    protected void process(PacketEvent event) {
        Player player = event.getPlayer();
        PacketContainer packet = event.getPacket();
        int entityId = packet.getIntegers().read(0);

        plugin.getLogger().info("Verarbeite Paket für Spieler: " + player.getName());
        plugin.getLogger().info("Spieler-UUID: " + player.getUniqueId());
        plugin.getLogger().info("TTT-Spieler: " + tttPlayer.getName());
        plugin.getLogger().info("TTT-Spieler-UUID: " + tttPlayer.getUniqueId());
        plugin.getLogger().info("Paket-Entitäts-ID: " + entityId);
        plugin.getLogger().info("TTT-Spieler-Entitäts-ID: " + tttPlayer.getEntityId());
        plugin.getLogger().info("TTT-Spieler-Entitäts Name: " + tttPlayer.getName());

        if (player.getUniqueId().equals(tttPlayer.getUniqueId())) {
            plugin.getLogger().info("Spieler entspricht TTT-Spieler");
            packet.getIntegers().write(0, tttPlayer.getEntityId());

            List<Pair<Object, Object>> slotStack = (List<Pair<Object, Object>>) packet.getModifier().read(1);
            List<Pair<Object, Object>> modifiedSlotStack = new ArrayList<>();

            for (Pair<Object, Object> pair : slotStack) {
                if (pair.getFirst().equals(ItemSlotConverter.CHEST.toNMSEnum())) {
                    ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                    LeatherArmorMeta redMeta = (LeatherArmorMeta) chestplate.getItemMeta();
                    //redMeta.setColor(Color.RED);
                    redMeta.setColor(Color.GREEN);
                    chestplate.setItemMeta(redMeta);
                    modifiedSlotStack.add(new Pair<>(pair.getFirst(), NMSHelper.toMinecraftItemStack(chestplate)));
                    plugin.getLogger().info("Brustplatte für " + tttPlayer.getName() + " wurde auf rot gesetzt");
                } else {
                    modifiedSlotStack.add(pair);
                }
            }

            packet.getModifier().write(1, modifiedSlotStack);
        }
    }
}
