package me.wilko.flightarmor.command.flightarmor;

import me.wilko.flightarmor.model.ArmorPiece;
import me.wilko.flightarmor.model.ArmorSet;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.remain.CompSound;

import java.util.Arrays;
import java.util.List;

public class GetSetCommand extends SimpleSubCommand {

	protected GetSetCommand(SimpleCommandGroup parent) {
		super(parent, "getset");

		setPermission("flightarmor.getset");
		setMinArguments(1);
		setUsage("<Set tier>");

		setPermissionMessage("&cYou do not have permission to use this command");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		if (!args[0].equals("1") && !args[0].equals("2"))

			//TODO message
			tellError("{0} is an invalid argument!");

		int tier = Integer.parseInt(args[0]);

		for (ArmorPiece piece : tier == 1 ? ArmorSet.getFirstTier().getAllPieces() : ArmorSet.getSecondTier().getAllPieces()) {
			getPlayer().getInventory().addItem(piece.build());
		}

		CompSound.ITEM_PICKUP.play(getPlayer());
		tellSuccess("You received the Tier " + tier + " Flight Armor set!");
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1)
			return Arrays.asList("1", "2");


		return NO_COMPLETE;
	}
}
