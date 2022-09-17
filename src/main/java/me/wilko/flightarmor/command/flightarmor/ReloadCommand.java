package me.wilko.flightarmor.command.flightarmor;

import me.wilko.flightarmor.FlightArmor;
import me.wilko.flightarmor.settings.MessagesLoader;
import me.wilko.flightarmor.settings.SetLoader;
import me.wilko.flightarmor.settings.Settings;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.settings.SimpleSettings;
import org.mineacademy.fo.settings.YamlStaticConfig;

public class ReloadCommand extends SimpleSubCommand {

	public ReloadCommand(SimpleCommandGroup parent) {
		super(parent, "reload");

		setPermission("flightarmor.reload");
	}

	@Override
	protected void onCommand() {
		SimpleSettings.resetSettingsCall();
		SimpleSettings.load(Settings.class);

		YamlStaticConfig.load(MessagesLoader.class);
		YamlStaticConfig.load(SetLoader.class);

		tellSuccess("Reloaded " + FlightArmor.getInstance().getName());

		if (this.isPlayer())
			CompSound.ORB_PICKUP.play(getPlayer());
	}
}
