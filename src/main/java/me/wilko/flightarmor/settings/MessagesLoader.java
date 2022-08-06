package me.wilko.flightarmor.settings;

import me.wilko.flightarmor.command.flightarmor.SpeedCommand;
import org.mineacademy.fo.settings.YamlStaticConfig;

public class MessagesLoader extends YamlStaticConfig {

	@Override
	protected void onLoad() throws Exception {
		loadConfiguration("messages.yml");
	}

	public static class FlySpeedCommands {

		private static void init() {
			setPathPrefix("flyspeed-command");

			SpeedCommand.CANNOT_USE = getString("cannot-use");
			SpeedCommand.INVALID_NUMBER = getString("invalid-number").replace("{max-speed}", String.valueOf(Settings.MAX_SPEED));
			SpeedCommand.SET_SPEED = getString("set-speed");
		}
	}
}
