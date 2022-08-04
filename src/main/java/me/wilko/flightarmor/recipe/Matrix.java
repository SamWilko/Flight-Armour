package me.wilko.flightarmor.recipe;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.ItemUtil;

import java.util.HashMap;
import java.util.Map;

public class Matrix {

	private final Map<Position, ItemStack> matrix = new HashMap<>();

	/**
	 * Compares this custom matrix against a given craft inventory matrix.
	 *
	 * @return true if all the ingredients match
	 */
	public boolean compare(ItemStack[] craftMatrix) {

		for (int i = 0; i < craftMatrix.length; i++) {

			ItemStack craftItem = craftMatrix[i];
			ItemStack matrixItem = matrix.get(Position.of(i));

			System.out.println("craftItem: " + craftItem);
			System.out.println("matrixItem: " + matrixItem);
			if ((!ItemUtil.isSimilar(matrixItem, craftItem)) || (matrixItem.getAmount() != craftItem.getAmount()))
				return false;
		}

		return true;
	}

	public void put(Position position, ItemStack item) {
		matrix.put(position, item);
	}

	@Override
	public String toString() {
		return matrix.toString();
	}

	public enum Position {

		TOP_LEFT(0),
		TOP_MIDDLE(1),
		TOP_RIGHT(2),
		MIDDLE_LEFT(3),
		MIDDLE(4),
		MIDDLE_RIGHT(5),
		BOTTOM_LEFT(6),
		BOTTOM_MIDDLE(7),
		BOTTOM_RIGHT(8);

		@Getter
		private final int matrixPos;

		Position(int matrixPos) {
			this.matrixPos = matrixPos;
		}

		@Nullable
		public static Position of(int i) {

			for (Position position : Position.values()) {
				if (position.getMatrixPos() == i)
					return position;
			}

			return null;
		}
	}
}
