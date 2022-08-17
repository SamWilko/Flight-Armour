package me.wilko.flightarmor.settings;

import org.mineacademy.fo.Common;
import org.mineacademy.fo.settings.SimpleSettings;

public final class Settings extends SimpleSettings {

	public static Boolean HIDE_TAGS;
	public static Double MAX_SPEED;
	public static Double DEFAULT_SPEED;

	private static void init() {
		HIDE_TAGS = getBoolean("hide-tags");

		// Max fly speed for tier 2 armor

		double maxSpeed = 10;
		try {
			maxSpeed = Double.parseDouble(getString("max-fly-speed"));

			if (maxSpeed < 0.5 || maxSpeed > 10) {
				maxSpeed = 10;
				Common.warning("(settings.yml) max-fly-speed must be at least 0.5, or lower than or equal to 10");
			}
		} catch (Exception ex) {
			Common.warning("(settings.yml) max-fly-speed must be a number between 0.5 and 10");
		}

		MAX_SPEED = maxSpeed;

		double defaultSpeed = 1;
		try {
			defaultSpeed = Double.parseDouble(getString("default-fly-speed"));

			if (defaultSpeed < 0.5 || defaultSpeed > 10) {
				defaultSpeed = 1;
				Common.warning("(settings.yml) default-fly-speed must be at least 0.5, or lower than or equal to 10");
			}
		} catch (Exception ex) {
			Common.warning("(settings.yml) default-fly-speed must be an integer between 0.5 and 10");
		}

		DEFAULT_SPEED = defaultSpeed;
	}
}
