package de.zeanon.zeanonutils;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class ZeanonUtils extends JavaPlugin {

	private final CommandHandler cmds = new CommandHandler(this);

	@Override
	public void onEnable() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(this.cmds, this);
		Objects.requireNonNull(this.getCommand("playerheads")).setExecutor(this.cmds);
		Objects.requireNonNull(this.getCommand("zeanonutils")).setExecutor(this.cmds);
		System.out.println("[" + this.getName() + "] " + this + " successfully launched...");
	}
}