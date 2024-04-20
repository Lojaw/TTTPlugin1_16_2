//package de.lojaw.tttplugin;
//
//import com.comphenix.protocol.PacketType;
//import com.comphenix.protocol.ProtocolLibrary;
//import com.comphenix.protocol.events.PacketContainer;
//import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
//import org.bukkit.Bukkit;
//import org.bukkit.Color;
//import org.bukkit.Material;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.EquipmentSlot;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.LeatherArmorMeta;
//
//import java.lang.reflect.InvocationTargetException;
//
//public class GiveChestplatesCommand implements CommandExecutor {
//    private final TTTPlugin_1_16_5 plugin;
//
//    public GiveChestplatesCommand(TTTPlugin_1_16_5 plugin) {
//        this.plugin = plugin;
//    }
//
//    @Override
//    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//        Bukkit.getServer().broadcastMessage("Command wurde abgeschickt");
//        Player lojaw = Bukkit.getPlayerExact("Lojaw");
//        Player lomenaron = Bukkit.getPlayerExact("Lomenaron");
//
//        if (lojaw != null && lomenaron != null) {
//            ItemStack grayChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
//            LeatherArmorMeta grayMeta = (LeatherArmorMeta) grayChestplate.getItemMeta();
//            grayMeta.setColor(Color.GRAY);
//            grayChestplate.setItemMeta(grayMeta);
//
//            lojaw.getInventory().setChestplate(grayChestplate);
//            lomenaron.getInventory().setChestplate(grayChestplate);
//
//            // Sende ein Paket an Lomenaron, um die Brustplatte rot erscheinen zu lassen
//            try {
//                sendRedChestplatePacket(lomenaron);
//            } catch (InvocationTargetException e) {
//                throw new RuntimeException(e);
//            }
//
//            // Entferne die Brustplatten nach 10 Sekunden
//            Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                if (lojaw.isOnline()) {
//                    lojaw.getInventory().setChestplate(null);
//                }
//                if (lomenaron.isOnline()) {
//                    lomenaron.getInventory().setChestplate(null);
//                    // Sende ein Paket an Lomenaron, um die rote Brustplatte zu entfernen
//                    try {
//                        sendRemoveChestplatePacket(lomenaron);
//                    } catch (InvocationTargetException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }, 200L); // 20 Ticks pro Sekunde, also 200 Ticks für 10 Sekunden
//        }
//
//        return true;
//    }
//
//    private void sendRedChestplatePacket(Player player) throws InvocationTargetException {
//        ItemStack redChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
//        LeatherArmorMeta redMeta = (LeatherArmorMeta) redChestplate.getItemMeta();
//        redMeta.setColor(Color.RED);
//        redChestplate.setItemMeta(redMeta);
//
//        PacketContainer packet = plugin.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
//        packet.getIntegers().write(0, player.getEntityId());
//        packet.getItemSlots().write(0, ItemSlot.CHEST);
//        packet.getItemModifier().write(0, redChestplate);
//        plugin.getProtocolManager().sendServerPacket(player, packet);
//    }
//
//    private void sendRemoveChestplatePacket(Player player) throws InvocationTargetException {
//        PacketContainer packet = plugin.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
//        packet.getIntegers().write(0, player.getEntityId());
//        packet.getItemSlots().write(0, ItemSlot.CHEST);
//        packet.getItemModifier().write(0, null);
//        plugin.getProtocolManager().sendServerPacket(player, packet);
//    }
//}
