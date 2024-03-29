package com.chopnix.minecraft.NixLauncher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.apache.commons.io.IOUtils;

import com.chopnix.minecraft.NixLauncher.NuxLauncher;
import com.chopnix.minecraft.NixLauncher.Performer;
import com.chopnix.minecraft.NixLauncher.yml.YAMLNode;
import com.chopnix.minecraft.NixLauncher.yml.YAMLProcessor;

public class Updater implements Runnable {
	private static YAMLProcessor config;
	private static YAMLProcessor repo;
	private DownloadsManager dlManager;
	private Performer performer;

	public Updater(Performer performer) {
		this.performer = performer;
	}

	public void run() {
		dlManager = new DownloadsManager(performer);

		updateAll("repository.highest");
		updateAll("repository.high");
		updateAll("repository.normal");
		updateOptional("repository.optional");

		dlManager.startDownloads();
	}

	public boolean checkForUpdate() {
		repo = NuxLauncher.getRepo();
		config = NuxLauncher.getConfig();

		if (repo.getInt("repository.version") > config.getInt("repository.version", 0)) {
			return true;
		} else {
			return false;
		}

	}

	private void updateAll(String path) {
		Map<String, YAMLNode> files = repo.getNodes(path);
		for (String index : files.keySet()) {
			YAMLNode file = files.get(index);
			String destination = getDestination(file.getString("source").replace("$os$", Utils.getOSName()), file.getString("destination"), file.getString("mode"));
			dlManager.addDownload(file.getString("source").replace("$os$", Utils.getOSName()), destination, file.getString("md5"), file.getString("name"), path + "." + index);
		}
	}

	private void updateOptional(String path) {
		Map<String, YAMLNode> files = repo.getNodes(path);
		for (String index : files.keySet()) {
			YAMLNode file = files.get(index);
			if (config.getBoolean("optional." + index + ".enabled", false)) {
				String destination = getDestination(file.getString("source").replace("$os$", Utils.getOSName()), file.getString("destination"), file.getString("mode"));
				dlManager.addDownload(file.getString("source").replace("$os$", Utils.getOSName()), destination, file.getString("md5"), file.getString("name"), path + "." + index);
			}
		}
	}

	private String getDestination(final String source, final String destination, final String action) {
		if (action.equalsIgnoreCase("copy")) {
			return Utils.getWorkingDir().toString() + "/tmp/" + destination;
		} else if (action.equalsIgnoreCase("extract")) {
			String[] out = source.split("/");
			return Utils.getWorkingDir().toString() + "/tmp/" + out[out.length - 1];
		} else if (action.equalsIgnoreCase("jarupdate")) {
			String[] out = source.split("/");
			return Utils.getWorkingDir().toString() + "/tmp/" + out[out.length - 1];
		} else {
			return Utils.getWorkingDir().toString() + "/tmp/" + destination;
		}
	}

	public static void doAction(Downloader download) throws FileNotFoundException, IOException {
		YAMLNode file = repo.getNode(download.getDownloadId());

		if (file.getString("mode").equalsIgnoreCase("copy")) {
			// Nothing to do
		} else if (file.getString("mode").equalsIgnoreCase("extract")) {
			File dlFile = download.getOutFile();

			JarInputStream inputStream = new JarInputStream(new FileInputStream(dlFile));

			ZipEntry entry = inputStream.getNextEntry();
			while (entry != null) {
				if (!entry.getName().contains("META-INF")) {
					if (entry.isDirectory()) {
						File dir = new File(Utils.getWorkingDir().toString() + "/tmp/" + file.getString("destination") + "/" + entry.getName());
						if (!dir.exists()) {
							dir.mkdirs();
						}
					} else {
						FileOutputStream outputStream = new FileOutputStream(Utils.getWorkingDir().toString() + "/tmp/" + file.getString("destination") + "/" + entry.getName());
						IOUtils.copy(inputStream, outputStream);
						outputStream.close();
					}
				}
				entry = inputStream.getNextEntry();
			}
			inputStream.close();
			dlFile.delete();
		} else if (file.getString("mode").equalsIgnoreCase("jarupdate")) {
			File dlFile = download.getOutFile();

			// What will contain the new jar
			File newJar = new File(Utils.getWorkingDir().toString() + "/tmp/" + file.getString("destination") + ".tmp");
			JarOutputStream outputStream = new JarOutputStream(new FileOutputStream(newJar));

			// The mod to add
			JarInputStream inputStream = new JarInputStream(new FileInputStream(dlFile));

			ArrayList<String> list = new ArrayList<String>();

			ZipEntry entry = inputStream.getNextEntry();
			while (entry != null) {
				outputStream.putNextEntry(new ZipEntry(entry.getName()));
				IOUtils.copy(inputStream, outputStream);
				list.add(entry.getName());
				entry = inputStream.getNextEntry();
			}
			inputStream.close();
			dlFile.delete();

			// The old jar, e.g. the original minecraft.jar
			File oldJar = new File(Utils.getWorkingDir().toString() + "/tmp/" + file.getString("destination"));
			JarInputStream oldStream = new JarInputStream(new FileInputStream(oldJar));

			entry = oldStream.getNextEntry();
			while (entry != null) {
				if (!list.contains(entry.getName())) {
					outputStream.putNextEntry(new ZipEntry(entry.getName()));
					IOUtils.copy(oldStream, outputStream);
				}
				entry = oldStream.getNextEntry();
			}
			oldStream.close();

			outputStream.flush();
			outputStream.close();

			oldJar.delete();
			newJar.renameTo(oldJar);
		}
	}

	public static void downloadsFinished() {
		config.setProperty("repository.version", repo.getInt("repository.version"));
		config.save();
	}
}
