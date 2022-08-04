package me.wilko.flightarmor.settings;

import me.wilko.flightarmor.model.ArmorPiece;
import me.wilko.flightarmor.model.ArmorSet;
import me.wilko.flightarmor.recipe.ArmorRecipe;
import me.wilko.flightarmor.recipe.Matrix;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlStaticConfig;

public class SetLoader extends YamlStaticConfig {

	@Override
	protected void onLoad() throws Exception {
		loadConfiguration("armor-sets.yml");
	}

	private static void init() {

		for (int tierIndex = 1; tierIndex <= 2; tierIndex++) {

			ArmorSet.Tier tier = tierIndex == 1 ? ArmorSet.Tier.ONE : ArmorSet.Tier.TWO;
			setPathPrefix(tierIndex == 1 ? "tier-one" : "tier-two");

			// Gets the armor material for this set
			ArmorPiece.Material setMaterial = ArmorPiece.Material.valueOf(getString("type").toUpperCase());

			ArmorSet armorSet = new ArmorSet(tier, setMaterial);

			// Gets whether or not the pieces in this set will glow
			boolean glowing = getBoolean("glow");

			// Goes through helmet, chestplate, leggings, and boots section
			for (ArmorPiece.Type type : ArmorPiece.Type.values()) {

				setPathPrefix("tier-one." + type.name().toLowerCase());

				// Create this new piece
				ArmorPiece piece = new ArmorPiece(
						type,
						getString("name"),
						getStringList("lore"),
						glowing
				);

				// Register the piece in it's parent set
				armorSet.set(type, piece);
				piece.setBelongingSet(armorSet);

				// Creates the recipe for this piece
				Matrix matrix = new Matrix();
				for (Matrix.Position position : Matrix.Position.values()) {
					setPathPrefix((tierIndex == 1 ? "tier-one" : "tier-two") + ".recipe." + position.name().toLowerCase().replace('_', '-'));

					String materialString = getString("item-material");

					if (materialString.equalsIgnoreCase("armor")) {

						ItemStack item;

						// If we're currently going through tier 1, set the armor ingredient to just a vanilla armor piece
						if (tier == ArmorSet.Tier.ONE) {
							item = ItemCreator.of(
									CompMaterial.valueOf(String.join("_", setMaterial.name(), type.name()).toUpperCase()
									)).make();

							// If it's tier 2, then set the armor ingredient to the tier 1 equivalence of the type	
						} else
							item = ArmorSet.getPiece(ArmorSet.getFirstTier(), type).build();

						matrix.put(position, item);

					} else {

						CompMaterial material = CompMaterial.valueOf(materialString.toUpperCase());

						matrix.put(position, ItemCreator.of(
										material
								)
								.amount(getInteger("amount"))
								.make());
					}
				}

				ArmorRecipe recipe = new ArmorRecipe(matrix, piece);
				piece.setRecipe(recipe);
			}

			if (tier == ArmorSet.Tier.ONE)
				ArmorSet.setFirstTier(armorSet);
			else
				ArmorSet.setSecondTier(armorSet);
		}
	}
}
