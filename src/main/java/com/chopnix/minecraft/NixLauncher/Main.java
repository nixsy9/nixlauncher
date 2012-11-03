package com.chopnix.minecraft.NixLauncher;

import com.chopnix.minecraft.NixLauncher.utils.Utils;

public class Main {

	static NuxLauncher launcher;

	public static void main(String[] args) {
		// TODO : args

		Utils.init("nuxos");
		launcher = new NuxLauncher("gui");
	}
}
