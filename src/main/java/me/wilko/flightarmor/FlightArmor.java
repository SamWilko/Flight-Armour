package me.wilko.flightarmor;

import org.mineacademy.fo.plugin.SimplePlugin;

public final class FlightArmor extends SimplePlugin {

	@Override
	protected void onPluginStart() {

	}

	@Override
	protected void onReloadablesStart() {

	}

	public static FlightArmor getInstance() {
		return (FlightArmor) SimplePlugin.getInstance();
	}
}
