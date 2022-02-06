package de.zeanon.zeanonutils;

import com.rylinaux.plugman.util.PluginUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;


class Helper {

	private static final ArrayList<String> downloadRequests = new ArrayList<>();

	private Helper() {

	}

	static void update(final Player p) {
		try {
			final File file = new File(ZeanonUtils.class.getProtectionDomain()
														.getCodeSource()
														.getLocation()
														.toURI()
														.getPath())
					.getCanonicalFile();
			try (final BufferedInputStream inputStream = new BufferedInputStream(new URL("https://github.com/Zeanon/ZeanonUtils/releases/latest/download/ZeanonUtils.jar").openStream())) {
				if (!file.exists()) {
					Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} else {
					try (final FileOutputStream outputStream = new FileOutputStream(file)) {
						final byte[] data = new byte[1024];
						int count;
						while ((count = inputStream.read(data, 0, 1024)) != -1) {
							outputStream.write(data, 0, count);
						}
					}
				}
			} catch (final IOException e) {
				p.sendMessage(ChatColor.DARK_PURPLE + "ZeanonUtils" + ChatColor.RED + " konnte nicht geupdatet werden.");
			}
			final PluginManager pm = Bukkit.getPluginManager();
			if (pm.getPlugin("PlugMan") != null && pm.isPluginEnabled(pm.getPlugin("PlugMan"))) {
				PluginUtil.reload(CommandHandler.plugin);
			} else {
				Bukkit.getServer().reload();
			}
			p.sendMessage(ChatColor.DARK_PURPLE + "ZeanonUtils" + ChatColor.RED + " wurde geupdatet.");
		} catch (final IOException | URISyntaxException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e.getCause());
		}
	}

    /*static ArrayList<File> getFolders(File folder, Boolean deep) { //NOSONAR
        ArrayList<File> files = new ArrayList<>(); //NOSONAR
        for (File file : Objects.notNull(folder.listFiles())) { //NOSONAR
            if (file.isDirectory()) { //NOSONAR
                files.add(file); //NOSONAR
                if (deep) { //NOSONAR
                    files.addAll(getFolders(file, true)); //NOSONAR
                } //NOSONAR
            } //NOSONAR
        } //NOSONAR
        return files; //NOSONAR
    }*/ //NOSONAR

	static void onList(final Player p, final String path) {
		final String[] extension = {"jar"};
		final Collection<File> rawFiles = FileUtils.listFiles(new File(path), extension, false);
		p.sendMessage(" ");
		p.sendMessage(ChatColor.DARK_GREEN + "=============================");
		for (final File file : rawFiles) {
			p.sendMessage(ChatColor.GREEN + file.getName());
		}
	}

	static void onListFolder(final Player p, final String path) {
		final File[] files = new File(path).listFiles(File::isDirectory);
		p.sendMessage(" ");
		p.sendMessage(ChatColor.DARK_GREEN + "=============================");
		for (final File file : Objects.requireNonNull(files)) {
			p.sendMessage(ChatColor.GREEN + file.getName());
		}
	}

	static void onDelete(final Player p, final String path, final String name) {
		final File file = new File(path + name + ".jar");
		if (!file.exists() || file.isDirectory() || name.equalsIgnoreCase("zeanonutils")) {
			p.sendMessage(ChatColor.GOLD + name + ".jar" + ChatColor.RED + " konnte leider nicht gelöscht werden.");
		} else {
			try {
				Files.delete(file.toPath());
				p.sendMessage(ChatColor.GOLD + name + ".jar" + ChatColor.RED + " wurde erfolgreich gelöscht.");
			} catch (final IOException e) {
				//Do nothing
			}
		}
	}

	static void onDeleteFolder(final Player p, final String path, final String name) {
		final File file = new File(path + name);
		if (!file.exists() || !file.isDirectory() || name.equalsIgnoreCase("zeanonutils")) {
			p.sendMessage(ChatColor.GOLD + name + ChatColor.RED + " konnte leider nicht gelöscht werden.");
		} else {
			try {
				FileUtils.deleteDirectory(file);
				p.sendMessage(ChatColor.GOLD + name + ChatColor.RED + " wurde erfolgreich gelöscht.");
			} catch (final IOException e) {
				p.sendMessage(ChatColor.GOLD + name + ChatColor.RED + " konnte leider nicht gelöscht werden.");
			}
		}
	}

	static void addDownloadRequest(final Player p) {
		if (!Helper.downloadRequests.contains(p.getUniqueId().toString())) {
			Helper.downloadRequests.add(p.getUniqueId().toString());
		}
	}

	static void removeDownloadRequest(final Player p) {
		Helper.downloadRequests.remove(p.getUniqueId().toString());
	}

	static boolean checkDownloadRequest(final Player p) {
		return Helper.downloadRequests.contains(p.getUniqueId().toString());
	}

	static void writeToFile(final File file, final BufferedInputStream inputStream) {
		try (final FileOutputStream outputStream = new FileOutputStream(file)) {
			if (!file.exists()) {
				Files.copy(inputStream, file.toPath());
			} else {
				final byte[] data = new byte[8192];
				int count;
				while ((count = inputStream.read(data, 0, 8192)) != -1) {
					outputStream.write(data, 0, count);
				}
			}
		} catch (final IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e.getCause());
			Bukkit.getLogger().log(Level.SEVERE, e.getCause().getMessage());
		}
	}
}