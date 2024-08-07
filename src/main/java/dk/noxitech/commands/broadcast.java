package dk.noxitech.commands;

import dk.noxitech.Color;
import dk.noxitech.main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;

public class broadcast implements CommandExecutor {
    private final main plugin;

    public broadcast(main plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("broadcast")) {
            if (args.length == 0) {
                sender.sendMessage(Color.getColored("&cYou lack an argument!"));
                return false;
            }

            String prefix = plugin.getConfig().getString("prefix");
            String MessageColor = plugin.getConfig().getString("MessageColor");
            String TitlePrefix = plugin.getConfig().getString("TitlePrefix");
            String TitleMsg = plugin.getConfig().getString("TitleMsg");
            StringBuilder message = new StringBuilder();
            for (String part : args) {
                message.append(part).append(" ");
            }
            String broadcastMessage = Color.getColored(prefix+"\n"+MessageColor+" " + message.toString().trim());

            Bukkit.getServer().broadcastMessage(broadcastMessage);

            String title = Color.getColored(TitlePrefix);
            String subtitle = Color.getColored(TitleMsg);

            for (Player player : Bukkit.getOnlinePlayers()) {
                sendTitle(player, title, subtitle, 10, 70, 20);
                playBroadcastSound(player);
            }
            return true;
        }
        return false;
    }

    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

            Class<?> packetPlayOutTitle = Class.forName(getNMSClass("PacketPlayOutTitle"));
            Class<?> enumTitleAction = Class.forName(getNMSClass("PacketPlayOutTitle$EnumTitleAction"));
            Class<?> iChatBaseComponent = Class.forName(getNMSClass("IChatBaseComponent"));
            Class<?> chatComponentText = Class.forName(getNMSClass("ChatComponentText"));

            Constructor<?> titleConstructor = packetPlayOutTitle.getConstructor(enumTitleAction, iChatBaseComponent, int.class, int.class, int.class);
            Object titleComponent = chatComponentText.getConstructor(String.class).newInstance(title);
            Object subtitleComponent = chatComponentText.getConstructor(String.class).newInstance(subtitle);

            Object titlePacket = titleConstructor.newInstance(enumTitleAction.getEnumConstants()[0], titleComponent, fadeIn, stay, fadeOut);
            Object subtitlePacket = titleConstructor.newInstance(enumTitleAction.getEnumConstants()[1], subtitleComponent, fadeIn, stay, fadeOut);

            playerConnection.getClass().getMethod("sendPacket", Class.forName(getNMSClass("Packet"))).invoke(playerConnection, titlePacket);
            playerConnection.getClass().getMethod("sendPacket", Class.forName(getNMSClass("Packet"))).invoke(playerConnection, subtitlePacket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getNMSClass(String className) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return "net.minecraft.server." + version + "." + className;
    }
    private void playBroadcastSound(Player player) {
        try {
            Sound sound = Sound.valueOf("NOTE_PLING");
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            player.sendMessage("Error: The sound 'NOTE_PLING' does not exist.");
        }
    }
}