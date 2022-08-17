package me.wilko.flightarmor.settings;

import me.wilko.flightarmor.model.ArmorPiece;
import me.wilko.flightarmor.model.ArmorSet;
import me.wilko.flightarmor.recipe.ArmorRecipe;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlStaticConfig;

import java.util.List;
import java.util.Map;

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
			ArmorPiece.Material setMaterial = ArmorPiece.Material.CHAINMAIL;

			ArmorSet armorSet = new ArmorSet(tier, setMaterial);

			// Gets whether or not the pieces in this set will glow
			boolean glowing = getBoolean("glow");

			// Goes through helmet, chestplate, leggings, and boots section
			for (ArmorPiece.Type type : ArmorPiece.Type.values()) {

				setPathPrefix((tier == ArmorSet.Tier.ONE ? "tier-one." : "tier-two.") + type.name().toLowerCase());

				// Create this new piece
				ArmorPiece piece = new ArmorPiece(
						type,
						getString("name"),
						getStringList("lore"),
						glowing
				);

				// Adds attributes
				for (Map.Entry<String, Object> entry : getMap("attributes").entrySet()) {

					String attrString = entry.getKey().toUpperCase().replace("-", "_");

					// Check is valid attribute
					try {
						Attribute.valueOf(attrString);
					} catch (Exception ex) {
						Common.warning("(armor-sets.yml) " + getPathPrefix() + ".attributes: '" + entry.getKey() + "' is an invalid attribute ...skipping");
						continue;
					}

					// Check if value is valid
					double val;
					try {
						val = (double) entry.getValue();
					} catch (Exception ex) {
						try {
							val = (double) (int) entry.getValue();
						} catch (Exception ex2) {
							Common.warning("(armor-sets.yml) " + getPathPrefix() + ".attributes: '" + entry.getValue() + "' is an invalid number ...skipping");
							continue;
						}
					}

					piece.addAttribute(attrString, val);
				}

				// Register the piece in it's parent set
				armorSet.set(type, piece);
				piece.setBelongingSet(armorSet);

				// Creates the recipe for this piece
				setPathPrefix((tier == ArmorSet.Tier.ONE ? "tier-one." : "tier-two.") + "recipe");

				ArmorRecipe recipe = new ArmorRecipe(piece.build());

				// Sets the shape for the recipe
				List<String> shapeList = getStringList("shape");
				recipe.shape(shapeList.get(0), shapeList.get(1), shapeList.get(2));

				// Load variables
				for (String key : getMap("variables").keySet()) {

					ItemStack item = ItemCreator.of(CompMaterial.fromMaterial(Material.valueOf(getString("variables." + key + ".material").toUpperCase())))
							.amount(getInteger("variables." + key + ".amount")).make();

					recipe.setIngredient(key.charAt(0), item);
				}

				// Load A variable
				ItemStack armorIngredient;
				if (tier == ArmorSet.Tier.ONE) {

					// Makes a vanilla equivalent of the piece
					armorIngredient = ItemCreator.of(
							CompMaterial.fromMaterial(Material.valueOf(String.join("_",
									armorSet.getMaterial().name(), piece.getType().name())))).make();
				} else {

					armorIngredient = ArmorSet.getFirstTier().getPiece(type).build();
				}

				recipe.setIngredient('A', armorIngredient);
				recipe.setMatrix();

				// Add recipe
				ArmorRecipe.addRecipe(recipe);
			}

			// Adds full set attributes
			setPathPrefix(tier == ArmorSet.Tier.ONE ? "tier-one" : "tier-two");

			for (String key : getMap("full-set-attributes").keySet()) {
				setPathPrefix((tier == ArmorSet.Tier.ONE ? "tier-one" : "tier-two") + ".full-set-attributes");

				try {
					Attribute.valueOf(key.toUpperCase().replace("-", "_"));
				} catch (Exception ex) {
					continue;
				}

				double val;
				try {
					val = Double.parseDouble(getString(key));
				} catch (Exception ex) {
					continue;
				}

				armorSet.addAttribute(key.toUpperCase().replace("-", "_"), val);
			}

			if (tier == ArmorSet.Tier.ONE)
				ArmorSet.setFirstTier(armorSet);
			else
				ArmorSet.setSecondTier(armorSet);
		}
	}
}
