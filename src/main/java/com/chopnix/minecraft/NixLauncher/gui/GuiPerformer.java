package com.chopnix.minecraft.NixLauncher.gui;

import java.util.Hashtable;

import javax.swing.SwingUtilities;

import com.chopnix.minecraft.NixLauncher.NuxLauncher;
import com.chopnix.minecraft.NixLauncher.Performer;
import com.chopnix.minecraft.NixLauncher.launch.GameLauncher;
import com.chopnix.minecraft.NixLauncher.utils.MinecraftLogin;
import com.chopnix.minecraft.NixLauncher.utils.Updater;


public class GuiPerformer implements Performer {

	// Main
	MainFrame mainWindow;
	NuxLauncher launcher;
	// Minecraft
    Hashtable<String, String> loggingInfo;

	public GuiPerformer(NuxLauncher MainLauncher) {
		// Loading main classes
		launcher = MainLauncher;
		mainWindow = new MainFrame(this);
		//logger = new MinecraftLogin(launcher, this);
	}

	public void doLogin() {
		mainWindow.setProgressBarView(true);
		mainWindow.setButtonEnabled(false);
		Thread t = new Thread(new MinecraftLogin(launcher, this, mainWindow.getUsername(), mainWindow.getPassword()));
		t.start();
	}

	public void doUpdate() {
		Updater updater = new Updater(this);
		if(updater.checkForUpdate()) {
			mainWindow.setButtonText("Last Played ...");
			mainWindow.setButtonEnabled(false);
			Thread t = new Thread(new Updater(this));
			t.start();
		} else {
			downloadsFinished();
		}
	}

	public void doLaunchMinecraft() {
		mainWindow.dispose();
		GameLauncher.main(loggingInfo);
	}

	public void changeProgress(final String status, final int progress) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainWindow.setStatus(status);
				mainWindow.setProgression(progress);
			}
		});
	}
	
	public void downloadsFinished() {
		mainWindow.setButtonText("play");
		mainWindow.setStatus("Game day, ready to launch.");
		mainWindow.setButtonEnabled(true);
		mainWindow.setProgression(100);
	}
	
	public void authFinishedSuccess(Hashtable<String, String> loggingInfo) {
		mainWindow.writeRemember();
		this.loggingInfo = loggingInfo;
		mainWindow.setStatus("Welcome, " + loggingInfo.get("username") + "."); //TODO: can't see. Add button update or remove that
		mainWindow.setLogged(true);
		doUpdate();
	}
	
	public void authFinishedFail(String reason) {
		mainWindow.setButtonEnabled(true);
		mainWindow.setStatus("Error when connecting : " + reason + ". Retry.");
		mainWindow.setProgression(0);
	}
}
