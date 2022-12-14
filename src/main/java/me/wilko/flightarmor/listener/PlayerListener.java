package me.wilko.flightarmor.listener;

import me.wilko.flightarmor.model.ArmorPiece;
import me.wilko.flightarmor.model.ArmorSet;
import me.wilko.flightarmor.recipe.ArmorRecipe;
import me.wilko.flightarmor.settings.PlayerData;
import me.wilko.flightarmor.settings.Settings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.menu.model.ItemCreator;

import java.util.ArrayList;
import java.util.List;

@AutoRegister
public final class PlayerListener implements Listener {

	@EventHandler
	public void craftPrepare(PrepareItemCraftEvent event) {

		ArmorRecipe recipe = ArmorRecipe.findMatching(event.getInventory().getMatrix());

		if (recipe == null)
			return;

		Player player = (Player) event.getView().getPlayer();

		ArmorPiece resultPiece = ArmorPiece.match(recipe.getResult());
		ArmorSet.Tier pieceTier = resultPiece.getBelongingSet().getTier();

		if (pieceTier == ArmorSet.Tier.ONE && !PlayerUtil.hasPerm(player, "flightarmor.craft.1"))
			return;

		if (pieceTier == ArmorSet.Tier.TWO && !PlayerUtil.hasPerm(player, "flightarmor.craft.2"))
			return;

		event.getInventory().setResult(recipe.getResult());
	}

	@EventHandler
	public void getCraftResult(InventoryClickEvent event) {

		if (!(event.getClickedInventory() instanceof CraftingInventory) || event.getSlotType() != InventoryType.SlotType.RESULT)
			return;

		CraftingInventory inventory = (CraftingInventory) event.getClickedInventory();

		ArmorPiece piece = ArmorPiece.match(event.getCurrentItem());

		if (piece == null)
			return;

		ArmorRecipe recipe = ArmorRecipe.find(piece);

		List<ItemStack> newMatrix = new ArrayList<>();
		for (int i = 0; i < inventory.getMatrix().length; i++) {

			ItemStack crafterItem = inventory.getMatrix()[i];
			ItemStack recipeItem = recipe.getMatrix().get(i);

			if (crafterItem == null) {
				newMatrix.add(null);
				continue;
			}

			crafterItem = ItemCreator.of(crafterItem).amount(crafterItem.getAmount() - recipeItem.getAmount()).make();
			newMatrix.add(crafterItem);
		}

		inventory.setMatrix(newMatrix.toArray(new ItemStack[0]));

		Player player = (Player) event.getWhoClicked();

		if (event.getClick().isShiftClick())
			player.getInventory().addItem(piece.build());
		else
			player.setItemOnCursor(piece.build());
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

					PlayerData data = PlayerData.lookup(player);

					// Allow flight
					player.setAllowFlight(true);
					player.setFlying(true);
					set.setAttributesFor(player);

					if (set.getTier() == ArmorSet.Tier.ONE)
						player.setFlySpeed((float) (Settings.DEFAULT_SPEED / 10f));
					else
						player.setFlySpeed((float) data.getFlySpeed());

				} else {

					// Disallow flight
					player.setFlying(false);
					player.setAllowFlight(false);

					ArmorSet.resetAttributes(player);
				}
			});
		}
	}

	@EventHandler
	public void armorEquip(PlayerInteractEvent event) {

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();

			ArmorPiece piece = ArmorPiece.match(player.getInventory().getItemInMainHand());

			if (piece == null)
				return;

			Common.runLater(1, () -> {

				ArmorSet set = ArmorSet.getSet(player.getInventory().getArmorContents());

				if (set == null)
					return;

				PlayerData data = PlayerData.lookup(player);

				player.setAllowFlight(true);
				player.setFlying(true);
				set.setAttributesFor(player);

				if (set.getTier() == ArmorSet.Tier.ONE)
					player.setFlySpeed((float) (Settings.DEFAULT_SPEED / 10f));
				else
					player.setFlySpeed((float) data.getFlySpeed());
			});
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PlayerData data = PlayerData.lookup(player);

		// Gets the set they are wearing
		ArmorSet set = ArmorSet.getSet(player.getInventory().getArmorContents());

		if (set == null)
			return;

		// Allow flight and set attributes
		player.setAllowFlight(true);
		set.setAttributesFor(player);

		// Set flight speed
		if (set.getTier() == ArmorSet.Tier.ONE)
			player.setFlySpeed((float) (Settings.DEFAULT_SPEED / 10f));
		else
			player.setFlySpeed((float) data.getFlySpeed());
	}

	private boolean isArmorPiece(@Nullable ItemStack item) {

		if (item == null)
			return false;

		return item.getType().name().contains("HELMET") ||
				item.getType().name().contains("CHESTPLATE") ||
				item.getType().name().contains("LEGGINGS") ||
				item.getType().name().contains("BOOTS");
	}

	private ArmorPiece.Type getType(ItemStack item) {

		String itemName = item.getType().name().toLowerCase();

		if (itemName.contains("helmet"))
			return ArmorPiece.Type.HELMET;
		if (itemName.contains("chestplate"))
			return ArmorPiece.Type.CHESTPLATE;
		if (itemName.contains("leggings"))
			return ArmorPiece.Type.LEGGINGS;
		if (itemName.contains("boots"))
			return ArmorPiece.Type.BOOTS;

		return null;
	}
}
