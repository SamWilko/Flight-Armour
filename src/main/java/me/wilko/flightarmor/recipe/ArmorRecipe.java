package me.wilko.flightarmor.recipe;

import lombok.Data;
import me.wilko.flightarmor.model.ArmorPiece;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.*;

@Data
public class ArmorRecipe {

	private static final List<ArmorRecipe> RECIPES = new ArrayList<>();

	private ItemStack result;
	private List<String> rows = new ArrayList<>();
	private Map<Character, ItemStack> ingredients = new HashMap<>();
	private List<ItemStack> matrix = new ArrayList<>();

	public ArmorRecipe(ItemStack result) {
		this.result = result;
	}

	public void shape(String row1, String row2, String row3) {
		rows.add(row1);
		rows.add(row2);
		rows.add(row3);
	}

	public void setIngredient(char key, ItemStack item) {
		ingredients.put(key, item);
	}

	public void setMatrix() {

		for (String row : rows) {
			for (char c : row.toCharArray()) {

				ItemStack item = ingredients.get(c);

				matrix.add(item);
			}
		}
	}

	public List<ItemStack> getMatrix() {
		return matrix;
	}

	public static void addRecipe(ArmorRecipe recipe) {
		RECIPES.add(recipe);
	}

	public static ArmorRecipe findMatching(ItemStack[] matrix) {

		RECIPE:
		// Search through every registered recipe
		for (ArmorRecipe recipe : RECIPES) {

			// Then search through every item in the recipe
			for (int index = 0; index < recipe.getMatrix().size(); index++) {

				ItemStack matrixItem = matrix[index];
				ItemStack recipeItem = recipe.getMatrix().get(index);

				if (matrixItem == null)
					continue RECIPE;

				// if both the items are pieces of armor, check that if they have the tier key, then they are of the same tier
				if ((ArmorPiece.isArmorPiece(matrixItem) && ArmorPiece.isArmorPiece(recipeItem))) {

					if (ArmorPiece.getType(matrixItem) != ArmorPiece.getType(recipeItem))
						continue RECIPE;

					if (CompMetadata.hasMetadata(matrixItem, "tier") && CompMetadata.hasMetadata(recipeItem, "tier")) {
						if (ArmorPiece.matchTier(recipeItem, matrixItem))
							continue;
						else
							continue RECIPE;

					} else if (CompMetadata.hasMetadata(matrixItem, "tier") && !CompMetadata.hasMetadata(recipeItem, "tier"))
						continue RECIPE;
					else if (CompMetadata.hasMetadata(recipeItem, "tier") && !CompMetadata.hasMetadata(matrixItem, "tier"))
						continue RECIPE;

				} else if (ArmorPiece.isArmorPiece(matrixItem) && !ArmorPiece.isArmorPiece(recipeItem))
					continue RECIPE;

				else if (ArmorPiece.isArmorPiece(recipeItem) && !ArmorPiece.isArmorPiece(matrixItem))
					continue RECIPE;

				// Check the two items are of the same material and the matrix item amount is greater than or equal to recipe item amount
				if ((!(matrixItem.getType() == recipeItem.getType())) || matrixItem.getAmount() < recipeItem.getAmount())
					continue RECIPE;
			}

			// If every item is the same, this is the one
			return recipe;
		}

		return null;
	}

	public static ArmorRecipe find(ArmorPiece piece) {

		for (ArmorRecipe recipe : RECIPES) {

			if (CompMetadata.hasMetadata(recipe.getResult(), "tier")
					&& Objects.equals(CompMetadata.getMetadata(recipe.getResult(), "tier"), piece.getBelongingSet().getTier().name()))
				return recipe;

		}

		return null;
	}
}
