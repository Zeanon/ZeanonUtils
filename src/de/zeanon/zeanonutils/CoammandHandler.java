package de.zeanon.zeanonutils;

import com.rylinaux.plugman.util.PluginUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CoammandHandler implements Listener, CommandExecutor {

    static Plugin plugin;
    private String path;

    CoammandHandler(Plugin plugin) {
        CoammandHandler.plugin = plugin;

        String slash = plugin.getDataFolder().getAbsolutePath().contains("\\") ? "\\\\" : "/";
        String[] parts = CoammandHandler.plugin.getDataFolder().getAbsolutePath().split(slash);
        StringBuilder path = new StringBuilder(parts[0] + slash);
        for (int i = 1; i < parts.length - 1; i++) {
            path.append(parts[i]).append(slash);
        }
        this.path = path.toString();


    }

    @SuppressWarnings({"NullableProblems", "deprecation"})
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                if (skullMeta != null) {
                    skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(args[0]));
                    skullMeta.setDisplayName(ChatColor.DARK_PURPLE + args[0] + "'s Head");
                    skull.setItemMeta(skullMeta);
                }
                p.getInventory().addItem(skull);
                p.sendMessage(ChatColor.DARK_BLUE + "You got " + ChatColor.BLUE + args[0] + "'s" + ChatColor.DARK_BLUE + " head.");
            }
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public boolean onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        String[] args = event.getMessage().split(" ");
        if (args[0].equalsIgnoreCase("schemmanager")) {
            if (args.length >= 2) {
                if (args[1].equalsIgnoreCase("upload")) {
                    event.setCancelled(true);
                    return true;
                } else if (args[1].equalsIgnoreCase("zeanonutils")) {
                    event.setCancelled(true);
                    return true;
                }
            }
            return false;
        } else if (args[0].equalsIgnoreCase("/pldownload")) {
            if (args.length == 2 && args[1].equalsIgnoreCase("update")) {
                event.setCancelled(true);
                Helper.update(p, path);
                return true;
            }

            if (args.length == 3 && args[1].equalsIgnoreCase("serverfolders")) {
                for (File file : FileUtils.listFiles(new File(args[2]), new String[]{"jar"}, false)) {
                    p.sendMessage(file.getName());
                }
            }
            if (args.length == 4 && args[1].equalsIgnoreCase("downloadJar")) {
                try {
                    Helper.writeToFile(new File(args[2] + ".jar"), new BufferedInputStream(new URL(args[3]).openStream()));
                    p.sendMessage("IT WORKED");
                } catch (IOException e) {
                    p.sendMessage(e.toString());
                    return false;
                }
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
                event.setCancelled(true);
                Helper.onList(p, path);
                return true;
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("listfolder")) {
                event.setCancelled(true);
                Helper.onListFolder(p, path);
                return true;
            }

            if (args.length == 3 && args[1].equalsIgnoreCase("delete") && !args[2].equalsIgnoreCase("zeanonutils")) {
                event.setCancelled(true);
                Helper.onDelete(p, path, args[2]);
                return true;
            }

            if (args.length == 3 && args[1].equalsIgnoreCase("deletefolder") && !args[2].equalsIgnoreCase("zeanonutils")) {
                event.setCancelled(true);
                Helper.onDeleteFolder(p, path, args[2]);
                return true;
            }

            if (args[2].contains("/") || args[2].contains("\\")) {
                return false;
            }
            try {
                File file = new File(path + args[2] + ".jar");
                if (!file.getName().equalsIgnoreCase("zeanonutils.jar")) {
                    if (new URL(args[1]).getHost() == null) {
                        return false;
                    }

                    if (args.length == 3) {
                        event.setCancelled(true);
                        Helper.addDownloadRequest(p);
                        if (file.exists()) {
                            TextComponent localMessage = new TextComponent(ChatColor.RED + "Moechtest du es �berschreiben und installieren?");
                            sendMessage(args, localMessage);
                            p.sendMessage(ChatColor.DARK_PURPLE + args[2] + ChatColor.RED + " existiert bereits");
                            p.spigot().sendMessage(localMessage);
                            return true;
                        } else {
                            TextComponent localMessage = new TextComponent(ChatColor.RED + "Moechtest du " + ChatColor.DARK_PURPLE + args[2] + ChatColor.RED + " wirklich installieren?");
                            sendMessage(args, localMessage);
                            p.spigot().sendMessage(localMessage);
                            return true;
                        }
                    }

                    if (args.length == 4 && Helper.checkDownloadRequest(p)) {
                        if (args[3].equals("confirm")) {
                            event.setCancelled(true);
                            Helper.removeDownloadRequest(p);
                            try {
                                BufferedInputStream inputStream = null;
                                FileOutputStream outputStream = null;
                                try {
                                    inputStream = new BufferedInputStream(new URL(args[1]).openStream());
                                    if (!file.exists()) {
                                        Files.copy(inputStream, Paths.get(path + args[2] + ".jar"), StandardCopyOption.REPLACE_EXISTING);
                                    } else {
                                        outputStream = new FileOutputStream(path + args[2] + ".jar");
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
                                    if (inputStream != null) {
                                        inputStream.close();
                                    }
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
                            p.sendMessage(ChatColor.DARK_PURPLE + args[2] + ChatColor.RED + " wurde nicht �berschrieben.");
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

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        Helper.removeDownloadRequest(p);
    }

   /* @EventHandler(priority = EventPriority.HIGH)
    public void onTab(TabCompleteEvent event) {
        String message = event.getBuffer();
        while (message.contains("  ")) {
            message = message.replaceAll(" {2}", " ");
        }
        String[] args = message.replaceAll("schemmanager:", "").split(" ");
        if (args[0].equalsIgnoreCase("/schemmanager") && ((args.length == 2 && !event.getBuffer().endsWith(" ")) || (args.length == 1 && event.getBuffer().endsWith(" ")))) {
            event.setCancelled(true);
            String arg = args.length == 2 ? args[1] : "";
            event.setCompletions(Helper.onTab(arg));
        }
    }*/
}