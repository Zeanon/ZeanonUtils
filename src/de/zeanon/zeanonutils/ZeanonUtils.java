package de.zeanon.zeanonutils;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


class ZeanonUtils extends JavaPlugin {

	private final CommandHandler cmds = new CommandHandler(this);

	@Override
	public void onEnable() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(cmds, this);
		Objects.requireNonNull(getCommand("playerheads")).setExecutor(cmds);
		Objects.requireNonNull(getCommand("zeanonutils")).setExecutor(cmds);
		System.out.println("[" + this.getName() + "] " + this + " successfully launched...");
	}
}