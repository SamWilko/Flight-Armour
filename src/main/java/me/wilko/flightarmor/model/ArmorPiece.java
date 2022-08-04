package me.wilko.flightarmor.model;

import lombok.Data;
import me.wilko.flightarmor.recipe.ArmorRecipe;
import me.wilko.flightarmor.settings.Settings;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

@Data
public class ArmorPiece {

	private final Type type;
	private final String name;
	private final List<String> lore;
	private final boolean glow;

	private ArmorSet belongingSet;

	private ArmorRecipe recipe;

	@Nullable
	public ItemStack build() {

		try {
			CompMaterial itemMat = CompMaterial.valueOf(String.join("_", belongingSet.getMaterial().name(), type.name()).toUpperCase());

			return ItemCreator.of(
							itemMat,
							name,
							lore
					).glow(glow).hideTags(Settings.HIDE_TAGS)
					.make();

		} catch (Exception ex) {
			Common.error(ex, "Something went wrong while trying to find the material of '" + String.join("_", belongingSet.getMaterial().name(), type.name()).toUpperCase() + "'");
			return null;
		}
	}

	@Nullable
	public static ArmorPiece match(@Nullable ItemStack item) {

		for (int tier = 1; tier <= 2; tier++) {

			for (ArmorPiece piece : tier == 1 ? ArmorSet.getFirstTier().getAllPieces() : ArmorSet.getSecondTier().getAllPieces()) {

				if (ItemUtil.isSimilar(item, piece.build()))
					return piece;
			}
		}

		return null;
	}

	public enum Material {

		LEATHER,
		CHAINMAIL,
		IRON,
		GOLD,
		DIAMOND
	}

	public enum Type {

		HELMET,
		CHESTPLATE,
		LEGGINGS,
		BOOTS
	}
}
