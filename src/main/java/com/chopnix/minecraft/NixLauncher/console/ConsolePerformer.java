package com.chopnix.minecraft.NixLauncher.console;

import java.util.Hashtable;

import com.chopnix.minecraft.NixLauncher.NuxLauncher;
import com.chopnix.minecraft.NixLauncher.Performer;
import com.chopnix.minecraft.NixLauncher.launch.GameLauncher;
import com.chopnix.minecraft.NixLauncher.utils.MinecraftLogin;
import com.chopnix.minecraft.NixLauncher.utils.Updater;

public final class ConsolePerformer implements Performer {

	// Main
	ConsoleRender mainConsole;
	NuxLauncher launcher;
	// Minecraft
	Hashtable<String, String> loggingInfo;

	public ConsolePerformer(NuxLauncher MainLauncher) {
		mainConsole = new ConsoleRender();
		launcher = MainLauncher;
		doLogin();
	}

	public void doLogin() {

		mainConsole.Log("Username :");
		String username = mainConsole.GetInput();
		mainConsole.Log("Password :");
		String password = mainConsole.GetInput();
		
		Thread t = new Thread(new MinecraftLogin(launcher, this, username, password)); //TODO: handle download errors
		t.start();
	}

	public void doUpdate() {
		Thread t = new Thread(new Updater(this)); //TODO: handle download errors
		t.start();
	}

	public void doLaunchMinecraft() {
		GameLauncher.main(loggingInfo);
	}

	public void changeProgress(String status, int progress) {
		mainConsole.Log(status + " " + progress + "%");
	}

	public void downloadsFinished() {
		mainConsole.Log("Update done, launching game ...");	
		doLaunchMinecraft();
	}

	public void authFinishedSuccess(Hashtable<String, String> loggingInfo) {
		this.loggingInfo = loggingInfo;
		
		mainConsole.Log("Successfully logged in player " + loggingInfo.get("username"));
		mainConsole.Log(" - Session ID : " + loggingInfo.get("sessionid"));
		mainConsole.Log(" - Download ticket : " + loggingInfo.get("downloadticket"));
		mainConsole.Log(" - Latest version : " + loggingInfo.get("latestversion"));

		doUpdate();
	}

	public void authFinishedFail(String reason) {
		mainConsole.Log("Login failed, please retry : " + reason);	
		doLogin();
	}
}
