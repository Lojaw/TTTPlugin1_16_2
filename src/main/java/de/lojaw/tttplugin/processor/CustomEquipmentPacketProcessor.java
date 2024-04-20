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

public class CustomEquipmentPacketProcessor extends EquipmentPacketProcessor {
    private final Player player;
    private final Color color;

    public CustomEquipmentPacketProcessor(Player player, Color color) {
        this.player = player;
        this.color = color;
    }

    @Override
    protected void process(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        int entityId = packet.getIntegers().read(0);

        if (event.getPlayer().getUniqueId().equals(player.getUniqueId())) {
            packet.getIntegers().write(0, player.getEntityId());

            List<Pair<Object, Object>> slotStack = (List<Pair<Object, Object>>) packet.getModifier().read(1);
            List<Pair<Object, Object>> modifiedSlotStack = new ArrayList<>();

            for (Pair<Object, Object> pair : slotStack) {
                if (pair.getFirst().equals(ItemSlotConverter.CHEST.toNMSEnum())) {
                    ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                    LeatherArmorMeta armorMeta = (LeatherArmorMeta) chestplate.getItemMeta();
                    armorMeta.setColor(color);
                    chestplate.setItemMeta(armorMeta);
                    modifiedSlotStack.add(new Pair<>(pair.getFirst(), NMSHelper.toMinecraftItemStack(chestplate)));
                } else {
                    modifiedSlotStack.add(pair);
                }
            }

            packet.getModifier().write(1, modifiedSlotStack);
        }
    }
}
