package de.lojaw.tttplugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import de.lojaw.tttplugin.processor.AllEquipmentPacketProcessor;
import de.lojaw.tttplugin.processor.CustomEquipmentPacketProcessor;
import de.lojaw.tttplugin.processor.HeldEquipmentPacketProcessor;
import de.lojaw.tttplugin.processor.TTTEquipmentPacketProcessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class TTTPlugin_1_16_2 extends JavaPlugin {
    private EquipmentPacketProcessor equipmentPacketProcessor;

    private static final List<Color> COLORS = Arrays.asList(
            Color.WHITE, Color.SILVER, Color.GRAY, Color.BLACK, Color.RED, Color.MAROON,
            Color.YELLOW, Color.OLIVE, Color.LIME, Color.GREEN, Color.AQUA, Color.TEAL,
            Color.BLUE, Color.NAVY, Color.FUCHSIA, Color.PURPLE, Color.ORANGE
    );

    private static final List<Color> COLORS2 = Arrays.asList(
            Color.RED, Color.BLUE, Color.RED, Color.BLUE, Color.RED, Color.BLUE,
            Color.RED, Color.BLUE, Color.RED, Color.BLUE, Color.RED, Color.BLUE,
            Color.RED, Color.BLUE, Color.RED, Color.BLUE, Color.RED
    );

    private static final List<Color> COLORS3 = Arrays.asList(
            Color.RED, Color.GREEN, Color.RED, Color.GREEN, Color.RED, Color.GREEN,
            Color.RED, Color.GREEN, Color.RED, Color.GREEN, Color.RED, Color.GREEN,
            Color.RED, Color.GREEN, Color.RED, Color.GREEN, Color.RED
    );

    private static final List<Color> COLORS4 = Arrays.asList(
            Color.GREEN, Color.BLUE, Color.GREEN, Color.BLUE, Color.GREEN, Color.BLUE,
            Color.GREEN, Color.BLUE, Color.GREEN, Color.BLUE, Color.GREEN, Color.BLUE,
            Color.GREEN, Color.BLUE, Color.GREEN, Color.BLUE, Color.GREEN
    );

    private static final List<Color> COLORS5 = Arrays.asList(
            Color.GRAY, Color.RED, Color.GRAY, Color.RED, Color.GRAY, Color.RED,
            Color.GRAY, Color.RED, Color.GRAY, Color.RED, Color.GRAY, Color.RED,
            Color.GRAY, Color.RED, Color.GRAY, Color.RED, Color.GRAY
    );

    @Override
    public void onEnable() {
        ProtocolLibrary.getProtocolManager()
                .addPacketListener(new PacketAdapter(this, PacketType.Play.Server.ENTITY_EQUIPMENT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (equipmentPacketProcessor == null) return;
                        equipmentPacketProcessor.process(event);
                    }
                });

        // Ständig Tag einstellen
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                Bukkit.getWorlds().forEach(world -> world.setTime(6000)); // 6000 entspricht 6:00 Uhr (Mittag)
            }
        }, 0, 20); // Alle 20 Ticks (1 Sekunde) aktualisieren

        // Verhindern, dass es regnet
        Bukkit.getWorlds().forEach(world -> world.setStorm(false));
        Bukkit.getWorlds().forEach(world -> world.setThundering(false));

        // Inventar jedes Spielers leeren
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
        }
    }

    public void setEquipmentPacketProcessor(EquipmentPacketProcessor equipmentPacketProcessor) {
        this.equipmentPacketProcessor = equipmentPacketProcessor;
        if (equipmentPacketProcessor != null) {
            equipmentPacketProcessor.onEnable();
        } else {
            EquipmentPacketProcessor.refreshEquipmentOfAllPlayers();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("colorsequencelimited")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                player.sendMessage(ChatColor.GREEN + "Die Farbe deiner Lederbrustplatte wird sich nun in einer Sequenz ändern.");

                final List<List<Color>> colorLists = Arrays.asList(COLORS2, COLORS3, COLORS4, COLORS5);
                final AtomicInteger colorListIndex = new AtomicInteger(0);
                final AtomicInteger colorIndex = new AtomicInteger(0);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        List<Color> currentColorList = colorLists.get(colorListIndex.get());

                        if (colorIndex.get() < currentColorList.size()) {
                            setEquipmentPacketProcessor(new CustomEquipmentPacketProcessor(player, currentColorList.get(colorIndex.get())));
                            colorIndex.incrementAndGet();
                            this.runTaskLater(TTTPlugin_1_16_2.this, 3); // 3 Ticks = 150ms
                        } else {
                            colorListIndex.incrementAndGet();
                            colorIndex.set(0);

                            if (colorListIndex.get() < colorLists.size()) {
                                this.runTaskLater(TTTPlugin_1_16_2.this, 3); // 3 Ticks = 150ms
                            } else {
                                this.cancel();
                            }
                        }
                    }
                }.runTaskTimer(this, 0, 3); // Verzögerung: 0 Ticks, Periode: 3 Ticks (150ms)
            } else {
                sender.sendMessage(ChatColor.RED + "Dieser Befehl kann nur von Spielern ausgeführt werden.");
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("colorsequence")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                player.sendMessage(ChatColor.GREEN + "Die Farbe deiner Lederbrustplatte wird sich nun in einer Sequenz ändern.");

                new BukkitRunnable() {
                    int colorIndex = 0;

                    @Override
                    public void run() {
                        if (colorIndex < COLORS.size()) {
                            setEquipmentPacketProcessor(new CustomEquipmentPacketProcessor(player, COLORS.get(colorIndex)));
                            colorIndex++;
                            this.runTaskLater(TTTPlugin_1_16_2.this, 3); // 3 Ticks = 150ms
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(this, 0, 3); // Verzögerung: 0 Ticks, Periode: 3 Ticks (150ms)
            } else {
                sender.sendMessage(ChatColor.RED + "Dieser Befehl kann nur von Spielern ausgeführt werden.");
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("cc")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                setEquipmentPacketProcessor(new CustomEquipmentPacketProcessor(player, Color.RED));
                player.sendMessage(ChatColor.GREEN + "Du hast jetzt eine rote Lederbrustplatte erhalten.");
            } else {
                sender.sendMessage(ChatColor.RED + "Dieser Befehl kann nur von Spielern ausgeführt werden.");
            }
            return true;
        }


        if (command.getName().equalsIgnoreCase("ccrepeat")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                player.sendMessage(ChatColor.GREEN + "Du hast jetzt eine rote Lederbrustplatte erhalten.");

                new BukkitRunnable() {
                    int packetsSent = 0;

                    @Override
                    public void run() {
                        if (packetsSent < 5) {
                            setEquipmentPacketProcessor(new CustomEquipmentPacketProcessor(player, Color.RED));
                            packetsSent++;
                            this.runTaskLater(TTTPlugin_1_16_2.this, 4); // 4 Ticks = 200ms
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(this, 0, 4); // Verzögerung: 0 Ticks, Periode: 4 Ticks (200ms)
            } else {
                sender.sendMessage(ChatColor.RED + "Dieser Befehl kann nur von Spielern ausgeführt werden.");
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("ci")) {
            if (sender.hasPermission("tttplugin.clearinventories")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.getInventory().clear();
                }
                sender.sendMessage(ChatColor.GREEN + "Die Inventare aller Spieler wurden geleert.");
            } else {
                sender.sendMessage(ChatColor.RED + "Du hast keine Berechtigung, diesen Befehl auszuführen.");        }
            return true;
        }

        if (command.getName().equalsIgnoreCase("ttt")) {

            if (args.length == 0) {
                // Gebe allen Spielern eine graue Brustplatte
                ItemStack grayChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                LeatherArmorMeta grayMeta = (LeatherArmorMeta) grayChestplate.getItemMeta();
                grayMeta.setColor(Color.GRAY);
                grayChestplate.setItemMeta(grayMeta);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.getInventory().setChestplate(grayChestplate);
                }

                sender.sendMessage(ChatColor.GREEN + "Alle Spieler haben nun eine graue Lederbrustplatte erhalten.");
                return true;
            }


            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Bitte gib einen Spielernamen an.");
                return true;
            }

            String playerName = args[0];
            Player tttPlayer = Bukkit.getPlayer(playerName);

            if (tttPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Der Spieler " + playerName + " wurde nicht gefunden.");
                return true;
            }

            // Gebe allen Spielern eine graue Brustplatte
            ItemStack grayChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta grayMeta = (LeatherArmorMeta) grayChestplate.getItemMeta();
            grayMeta.setColor(Color.GRAY);
            grayChestplate.setItemMeta(grayMeta);

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.getInventory().setChestplate(grayChestplate);
            }

            // Sende allen Spielern ein Paket, um die Brustplatte entsprechend anzupassen
            setEquipmentPacketProcessor(new TTTEquipmentPacketProcessor(tttPlayer, this));

            tttPlayer.sendMessage(ChatColor.GREEN + "Du bist nun der TTT-Spieler. Deine Brustplatte ist rot, während die anderen Spieler graue Brustplatten haben.");
            sender.sendMessage(ChatColor.GREEN + "TTT-Spiel gestartet. " + tttPlayer.getName() + " ist der TTT-Spieler.");
            getLogger().info("TTT-Spiel gestartet. " + tttPlayer.getName() + " ist der TTT-Spieler.");

            return true;
        }

        String context = args.length < 1 ? "none" : args[0];

        if (context.equalsIgnoreCase("none")) {
            setEquipmentPacketProcessor(null);
            sender.sendMessage(ChatColor.GREEN + "No longer hiding any equipment.");
        } else if (context.equalsIgnoreCase("held")) {
            setEquipmentPacketProcessor(new HeldEquipmentPacketProcessor());
            sender.sendMessage(ChatColor.YELLOW + "Now hiding only held equipment.");
        } else if (context.equalsIgnoreCase("all")) {
            setEquipmentPacketProcessor(new AllEquipmentPacketProcessor());
            sender.sendMessage(ChatColor.RED + "Now hiding all equipment.");
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "/hide [none | held | all]");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length <= 1) {
            return Arrays.asList("none", "held", "all");
        }

        return new ArrayList<>();
    }
}
