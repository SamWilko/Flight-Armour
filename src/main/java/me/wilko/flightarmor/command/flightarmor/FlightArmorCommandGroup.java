package me.wilko.flightarmor.command.flightarmor;

import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommandGroup;

@AutoRegister
public final class FlightArmorCommandGroup extends SimpleCommandGroup {

	public FlightArmorCommandGroup() {
		super("flightarmor|fa");
	}

	@Override
	protected void registerSubcommands() {
		registerSubcommand(new GetSetCommand(this));
		registerSubcommand(new SpeedCommand(this));
		registerSubcommand(new ReloadCommand(this));
	}
}
