package me.wilko.flightarmor.listener;

import me.wilko.flightarmor.model.ArmorPiece;
import me.wilko.flightarmor.model.ArmorSet;
import me.wilko.flightarmor.recipe.ArmorRecipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;

@AutoRegister
public final class PlayerListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {

		// Check that the clicker is a player
		if (!(event.getWhoClicked() instanceof Player))
			return;

		Player clicker = (Player) event.getWhoClicked();
		Inventory clickedInv = event.getClickedInventory();

		if (clickedInv instanceof CraftingInventory) {

			Common.runLater(1, () -> {

				CraftingInventory craftInv = (CraftingInventory) clickedInv;

				// Check that they are not clicking on the result to prevent duplication
				if (event.getSlotType() == InventoryType.SlotType.RESULT)
					return;

				// Get the item in the middle of the matrix
				ItemStack middleItem = craftInv.getItem(5);
				if (middleItem == null || middleItem.getType() == Material.AIR)
					return;

				// See if it's a flight armor piece
				ArmorPiece piece = ArmorPiece.match(middleItem);

				if (piece == null) {

					// Check if it's a vanilla armor piece
					for (ArmorPiece.Type type : ArmorPiece.Type.values()) {

						if (middleItem.getType().name().contains(type.name()) && middleItem.getType().name().contains(ArmorSet.getFirstTier().getMaterial().name())) {

							ArmorRecipe recipe = ArmorSet.getFirstTier().getPiece(type).getRecipe();

							if (recipe.getMatrix().compare(craftInv.getMatrix())) {

								craftInv.setResult(recipe.getResult().build());
							}
						}
					}

				} else {

					// Get the recipe of this piece
					ArmorRecipe recipe = ArmorSet.getSecondTier().getPiece(piece.getType()).getRecipe();

					// Check the event matrix against the piece recipe matrix
					if (recipe.getMatrix().compare(craftInv.getMatrix())) {

						// Sets the result of the crafting if the matrix is complete
						craftInv.setResult(recipe.getResult().build());
					}
				}
			});

		} else if (clickedInv instanceof PlayerInventory) {

			PlayerInventory playerInv = (PlayerInventory) clickedInv;

			// Check that they are wearing a full set of armor
			ArmorSet set = ArmorSet.getSet(playerInv.getArmorContents());
			if (set == null)
				return;

			if (set.getTier() == ArmorSet.Tier.ONE) {
				//TODO logic
			} else if (set.getTier() == ArmorSet.Tier.TWO) {
				//TODO logic
			}
		}
	}
}
