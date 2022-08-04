package me.wilko.flightarmor.settings;

import org.mineacademy.fo.settings.SimpleSettings;

public final class Settings extends SimpleSettings {

	public static Boolean HIDE_TAGS;

	private static void init() {
		HIDE_TAGS = getBoolean("hide-tags");
	}
}
