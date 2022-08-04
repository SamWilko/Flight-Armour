package me.wilko.flightarmor.listener;

import me.wilko.flightarmor.model.ArmorSet;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;

@AutoRegister
public final class PlayerListener implements Listener {

	@EventHandler
	public void crafting(InventoryClickEvent event) {

	}

	@EventHandler
	public void armor(InventoryClickEvent event) {

		if (!(event.getWhoClicked() instanceof Player))
			return;

		if (!(event.getClickedInventory() instanceof PlayerInventory))
			return;

		Player player = (Player) event.getWhoClicked();
		PlayerInventory inventory = (PlayerInventory) event.getClickedInventory();

		// Should we check armor?
		if ((event.getSlotType() != InventoryType.SlotType.ARMOR && isArmorPiece(event.getCurrentItem()) && event.getClick().isShiftClick())
				|| (event.getSlotType() == InventoryType.SlotType.ARMOR && !(event.getCurrentItem().getType() == Material.AIR && !isArmorPiece(player.getItemOnCursor())))) {

			// Check armor. Have to run after a tick because player's armor doesn't update straight away
			Common.runLater(1, () -> {
				ArmorSet set = ArmorSet.getSet(player.getInventory().getArmorContents());

				// Player is wearing a full set
				if (set != null) {

					// Allow flight
					player.setAllowFlight(true);
					player.setFlying(true);

				} else {

					// Disallow flight
					player.setFlying(false);
					player.setAllowFlight(false);
				}
			});
		}
	}

	private boolean isArmorPiece(@Nullable ItemStack item) {

		if (item == null)
			return false;

		return item.getType().name().contains("HELMET") ||
				item.getType().name().contains("CHESTPLATE") ||
				item.getType().name().contains("LEGGINGS") ||
				item.getType().name().contains("BOOTS");
	}
}
