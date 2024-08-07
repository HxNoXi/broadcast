package dk.noxitech;

import dk.noxitech.commands.broadcast;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.ResourceBundle;

public final class main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("Enabling Broadcast Plugin...");
        getLogger().info("Registering commands...");
        Objects.requireNonNull(getCommand("broadcast")).setExecutor(new broadcast(this));
        saveDefaultConfig();
    }


    @Override
    public void onDisable() {
    }
}