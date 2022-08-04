package me.wilko.flightarmor.recipe;

import lombok.Data;
import me.wilko.flightarmor.model.ArmorPiece;

@Data
public class ArmorRecipe {

	private final Matrix matrix;
	private final ArmorPiece result;
}
