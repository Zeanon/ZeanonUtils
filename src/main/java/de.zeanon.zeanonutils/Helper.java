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
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;


class Helper {

	private static final ArrayList<String> downloadRequests = new ArrayList<>();

	static void update(Player p, String path) {
		String fileName = null;
		try {
			fileName = new File(ZeanonUtils.class.getProtectionDomain()
												 .getCodeSource()
												 .getLocation()
												 .toURI()
												 .getPath())
					.getName();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		try {
			File file = new File(path + fileName);
			BufferedInputStream inputStream = null;
			FileOutputStream outputStream = null;
			try {
				inputStream = new BufferedInputStream(new URL("https://github.com/Zeanon/ZeanonUtils/releases/latest/download/ZeanonUtils.jar").openStream());
				if (!file.exists()) {
					Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} else {
					outputStream = new FileOutputStream(file);
					final byte[] data = new byte[1024];
					int count;
					while ((count = inputStream.read(data, 0, 1024)) != -1) {
						outputStream.write(data, 0, count);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				p.sendMessage(ChatColor.DARK_PURPLE + "ZeanonUtils" + ChatColor.RED + " konnte nicht geupdatet werden.");
				return;
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
				PluginUtil.reload(CommandHandler.plugin);
			} else {
				Bukkit.getServer().reload();
			}
			p.sendMessage(ChatColor.DARK_PURPLE + "ZeanonUtils" + ChatColor.RED + " wurde geupdatet.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /*static ArrayList<File> getFolders(File folder, Boolean deep) {
        ArrayList<File> files = new ArrayList<>();
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                files.add(file);
                if (deep) {
                    files.addAll(getFolders(file, true));
                }
            }
        }
        return files;
    }*/

	static void onList(Player p, String path) {
		String[] extension = {"jar"};
		Collection<File> rawFiles = FileUtils.listFiles(new File(path), extension, false);
		p.sendMessage(" ");
		p.sendMessage(ChatColor.DARK_GREEN + "=============================");
		for (File file : rawFiles) {
			p.sendMessage(ChatColor.GREEN + file.getName());
		}
	}

	static void onListFolder(Player p, String path) {
		File[] files = new File(path).listFiles(File::isDirectory);
		p.sendMessage(" ");
		p.sendMessage(ChatColor.DARK_GREEN + "=============================");
		for (File file : Objects.requireNonNull(files)) {
			p.sendMessage(ChatColor.GREEN + file.getName());
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	static void onDelete(Player p, String path, String name) {
		File file = new File(path + name + ".jar");
		if (!file.exists() || file.isDirectory() || name.equalsIgnoreCase("zeanonutils")) {
			p.sendMessage(ChatColor.GOLD + name + ".jar" + ChatColor.RED + " konnte leider nicht gelöscht werden.");
		} else {
			file.delete();
			p.sendMessage(ChatColor.GOLD + name + ".jar" + ChatColor.RED + " wurde erfolgreich gelöscht.");
		}
	}

	static void onDeleteFolder(Player p, String path, String name) {
		File file = new File(path + name);
		if (!file.exists() || !file.isDirectory() || name.equalsIgnoreCase("zeanonutils")) {
			p.sendMessage(ChatColor.GOLD + name + ChatColor.RED + " konnte leider nicht gelöscht werden.");
		} else {
			try {
				FileUtils.deleteDirectory(file);
				p.sendMessage(ChatColor.GOLD + name + ChatColor.RED + " wurde erfolgreich gelöscht.");
			} catch (IOException e) {
				e.printStackTrace();
				p.sendMessage(ChatColor.GOLD + name + ChatColor.RED + " konnte leider nicht gelöscht werden.");
			}
		}
	}


	static void addDownloadRequest(Player p) {
		if (!Helper.downloadRequests.contains(p.getUniqueId().toString())) {
			Helper.downloadRequests.add(p.getUniqueId().toString());
		}
	}

	static void removeDownloadRequest(Player p) {
		Helper.downloadRequests.remove(p.getUniqueId().toString());
	}

	static boolean checkDownloadRequest(Player p) {
		return Helper.downloadRequests.contains(p.getUniqueId().toString());
	}


	static void writeToFile(final File file, final BufferedInputStream inputStream) {
		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			if (!file.exists()) {
				Files.copy(inputStream, file.toPath());
			} else {
				final byte[] data = new byte[8192];
				int count;
				while ((count = inputStream.read(data, 0, 8192)) != -1) {
					outputStream.write(data, 0, count);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}