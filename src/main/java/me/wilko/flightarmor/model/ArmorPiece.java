package me.wilko.flightarmor.model;

import lombok.Data;
import me.wilko.flightarmor.settings.Settings;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ArmorPiece {
	private final Type type;
	private final String name;
	private final List<String> lore;
	private final boolean glow;
	private final Map<String, Double> attributes = new HashMap<>();

	private ArmorSet belongingSet;


	public ArmorPiece(Type type, String name, List<String> lore, boolean glow) {
		this.type = type;
		this.name = name;
		this.lore = lore;
		this.glow = glow;
	}

	@Nullable
	public ItemStack build() {

		try {
			CompMaterial itemMat = CompMaterial.valueOf(String.join("_", belongingSet.getMaterial().name(), type.name()).toUpperCase());


			ItemStack armor = ItemCreator.of(
							itemMat,
							name,
							lore
					).glow(glow)
					.hideTags(Settings.HIDE_TAGS)
					.unbreakable(true)
					.make();

			ItemMeta meta = armor.getItemMeta();

			if (meta == null) {
				Common.warning("Could not get item meta for armor piece '" + this.type.name() + " tier " + this.belongingSet.getTier() + "' because ItemMeta is null!");

			} else {
				for (Map.Entry<String, Double> entry : this.attributes.entrySet()) {

					AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), entry.getKey(), entry.getValue(), AttributeModifier.Operation.ADD_NUMBER, getSlot());
					meta.addAttributeModifier(Attribute.valueOf(entry.getKey()), modifier);
				}

				armor.setItemMeta(meta);
			}

			return armor;

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

	public void addAttribute(String attrString, double val) {
		this.attributes.put(attrString, val);
	}

	private EquipmentSlot getSlot() {

		switch (this.type) {
			case HELMET:
				return EquipmentSlot.HEAD;
			case CHESTPLATE:
				return EquipmentSlot.CHEST;
			case LEGGINGS:
				return EquipmentSlot.LEGS;
			case BOOTS:
				return EquipmentSlot.FEET;
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
