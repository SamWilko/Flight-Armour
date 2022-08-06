package me.wilko.flightarmor.command.flightarmor;

import me.wilko.flightarmor.model.ArmorSet;
import me.wilko.flightarmor.settings.PlayerData;
import me.wilko.flightarmor.settings.Settings;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.ArrayList;
import java.util.List;

public class SpeedCommand extends SimpleSubCommand {

	public static String CANNOT_USE;
	public static String INVALID_NUMBER;
	public static String SET_SPEED;

	public SpeedCommand(SimpleCommandGroup parent) {
		super(parent, "flightspeed|speed");

		setPermission("flightarmor.flyspeed");

		setMinArguments(1);
	}

	@Override
	protected void onCommand() {
		checkConsole();

		ArmorSet set = ArmorSet.getSet(getPlayer().getInventory().getArmorContents());

		if (set == null || set.getTier() == ArmorSet.Tier.ONE) {
			Common.tellNoPrefix(getPlayer(), CANNOT_USE);
			return;
		}

		int speed;
		try {
			speed = Integer.parseInt(args[0]);

			if (speed < 1 || speed > Settings.MAX_SPEED) {
				Common.tellNoPrefix(getPlayer(), INVALID_NUMBER);
				return;
			}

		} catch (Exception ex) {
			Common.tellNoPrefix(getPlayer(), INVALID_NUMBER);
			return;
		}

		getPlayer().setFlySpeed((float) speed / 10f);

		PlayerData data = PlayerData.lookup(getPlayer());
		data.setFlySpeed(getPlayer().getFlySpeed());

		Common.tellNoPrefix(getPlayer(), SET_SPEED.replace("{speed}", String.valueOf(speed)));
	}

	@Override
	protected List<String> tabComplete() {
		if (args.length == 1) {

			List<String> options = new ArrayList<>();

			for (int i = 1; i <= Settings.MAX_SPEED; i++) {
				options.add(String.valueOf(i));
			}

			return options;
		}

		return NO_COMPLETE;
	}
}
