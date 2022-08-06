package me.wilko.flightarmor.recipe;

import lombok.Data;
import me.wilko.flightarmor.model.ArmorPiece;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.ItemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

				// Check the two items are the same and the matrix item amount is greater than or equalto recipe item amount
				if ((!ItemUtil.isSimilar(matrixItem, recipeItem)) || matrixItem.getAmount() < recipeItem.getAmount())
					continue RECIPE;
			}

			// If every item is the same, this is the one
			return recipe;
		}

		return null;
	}

	public static ArmorRecipe find(ArmorPiece piece) {

		for (ArmorRecipe recipe : RECIPES) {

			if (ItemUtil.isSimilar(recipe.getResult(), piece.build()))
				return recipe;
		}

		return null;
	}
}
