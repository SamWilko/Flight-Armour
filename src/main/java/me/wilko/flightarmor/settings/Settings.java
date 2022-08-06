package me.wilko.flightarmor.settings;

import org.mineacademy.fo.Common;
import org.mineacademy.fo.settings.SimpleSettings;

public final class Settings extends SimpleSettings {

	public static Boolean HIDE_TAGS;
	public static Integer MAX_SPEED;
	public static Integer DEFAULT_SPEED;

	private static void init() {
		HIDE_TAGS = getBoolean("hide-tags");

		// Max fly speed for tier 2 armor

		int maxSpeed = 10;
		try {
			maxSpeed = Integer.parseInt(getString("max-fly-speed"));

			if (maxSpeed < 1 || maxSpeed > 10) {
				maxSpeed = 10;
				Common.warning("(settings.yml) max-fly-speed must be at least 1, or lower than or equal to 10");
			}
		} catch (Exception ex) {
			Common.warning("(settings.yml) max-fly-speed must be an integer between 1 and 10");
		}

		MAX_SPEED = maxSpeed;

		int defaultSpeed = 1;
		try {
			defaultSpeed = Integer.parseInt(getString("default-fly-speed"));

			if (defaultSpeed < 1 || defaultSpeed > 10) {
				defaultSpeed = 1;
				Common.warning("(settings.yml) default-fly-speed must be at least 1, or lower than or equal to 10");
			}
		} catch (Exception ex) {
			Common.warning("(settings.yml) default-fly-speed must be an integer between 1 and 10");
		}

		DEFAULT_SPEED = defaultSpeed;
	}
}
