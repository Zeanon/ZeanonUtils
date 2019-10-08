package de.zeanon.zeanonutils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ZeanonUtils extends JavaPlugin {

    private CoammandHandler cmds = new CoammandHandler(this);

    @Override
    public void onEnable() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(cmds, this);
        Objects.requireNonNull(getCommand("playerheads")).setExecutor(cmds);
        System.out.println("[" + this.getName() + "] " + this + " successfully launched...");
    }
}