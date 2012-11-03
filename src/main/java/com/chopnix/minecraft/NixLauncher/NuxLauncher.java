package com.chopnix.minecraft.NixLauncher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import com.chopnix.minecraft.NixLauncher.console.ConsolePerformer;
import com.chopnix.minecraft.NixLauncher.exceptions.InvalidLauncherModeException;
import com.chopnix.minecraft.NixLauncher.gui.GuiPerformer;
import com.chopnix.minecraft.NixLauncher.utils.Downloader;
import com.chopnix.minecraft.NixLauncher.utils.Utils;
import com.chopnix.minecraft.NixLauncher.yml.YAMLFormat;
import com.chopnix.minecraft.NixLauncher.yml.YAMLProcessor;

public class NuxLauncher {

	static String nuxLauncherVersion = "indev";
	static Integer minecraftLauncherVersion = 13;
	static YAMLProcessor config;
	static YAMLProcessor repo;

	public NuxLauncher(String Mode) {
		try {
			File configFile = new File(Utils.getWorkingDir(), "config.yml");
			if (!configFile.exists()) {
				configFile.createNewFile();
			}
			config = new YAMLProcessor(configFile, false, YAMLFormat.EXTENDED);
			config.load();

			downloadRepo();

			if (Mode.equals("console")) {
                        new ConsolePerformer(this);
			} else if (Mode.equals("gui")) {
                        new GuiPerformer(this);
			} else {
				throw new InvalidLauncherModeException();
			}

		} catch (Exception e) {
		}
	}

	public Integer getMinecraftLauncherVersion() {
		return minecraftLauncherVersion;
	}

	public static void downloadRepo() {
		try {
			File repoFile = new File(Utils.getWorkingDir(), "repo.yml");
			Downloader repoDL;
			if (config.getBoolean("testrepo", false)) {
				repoDL = new Downloader("https://dl.dropbox.com/u/27471347/up/repo.yml", repoFile.getAbsolutePath());
			} else {
				repoDL = new Downloader("https://dl.dropbox.com/u/27471347/up/repo-test.yml", repoFile.getAbsolutePath());
			}
			repoDL.start();

			if (!repoFile.exists()) {
				repoFile.createNewFile();
			}

			repo = new YAMLProcessor(repoFile, false, YAMLFormat.EXTENDED);
			repo.load();
		} catch (MalformedURLException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public static YAMLProcessor getConfig() {
		return config;
	}

	public static YAMLProcessor getRepo() {
		return repo;
	}
}
