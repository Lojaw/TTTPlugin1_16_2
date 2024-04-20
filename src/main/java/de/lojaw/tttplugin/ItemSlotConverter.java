package de.lojaw.tttplugin;

import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public enum ItemSlotConverter {
    HEAD, CHEST, LEGS, FEET, MAINHAND, OFFHAND;

    public Object getMinecraftItemStack(Player player) {
        return NMSHelper.toMinecraftItemStack(getBukkitItemStack(player));
    }

    public ItemStack getBukkitItemStack(Player player) {
        PlayerInventory inventory = player.getInventory();
        switch (this) {
            case HEAD: return inventory.getHelmet();
            case CHEST: return inventory.getChestplate();
            case LEGS: return inventory.getLeggings();
            case FEET: return inventory.getBoots();
            case MAINHAND: return inventory.getItemInMainHand();
            case OFFHAND: return inventory.getItemInOffHand();
        }
        return null;
    }

    @SneakyThrows
    public Object toNMSEnum() {
        try {
            return NMSHelper.getNMSClass("EnumItemSlot").getField(name()).get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Behandle die Exceptions entsprechend, z.B. durch Protokollieren oder Werfen einer benutzerdefinierten Exception
            throw new RuntimeException("Fehler beim Abrufen des NMS-Enums", e);
        }
    }
}