package com.therandomlabs.curseapi.minecraft;

import java.nio.file.Path;
import java.nio.file.Paths;
import com.therandomlabs.utils.platform.Platform;

public final class Minecraft {
	private Minecraft() {}

	public static Path getDirectory() {
		switch(Platform.getOS()) {
		case WINDOWS:
			return Paths.get(System.getenv("APPDATA"), ".minecraft");
		case MAC_OS_X:
			return Paths.get("~/Library/Application Support/minecraft");
		default:
			return Paths.get("~/.minecraft");
		}
	}
}
