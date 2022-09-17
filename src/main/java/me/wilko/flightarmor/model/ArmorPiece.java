package me.wilko.flightarmor.model;

import lombok.Data;
import me.wilko.flightarmor.settings.Settings;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ArmorPiece {
	private final Type type;
	private final String name;
	private final Integer modelData;
	private final List<String> lore;
	private final boolean glow;
	private final Map<String, Double> attributes = new HashMap<>();

	private ArmorSet belongingSet;


	public ArmorPiece(Type type, String name, Integer modelData, List<String> lore, boolean glow) {
		this.type = type;
		this.name = name;
		this.modelData = modelData;
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
					.modelData(modelData)
					.hideTags(Settings.HIDE_TAGS)
					.unbreakable(true)
					.make();

			armor = CompMetadata.setMetadata(armor, "tier", belongingSet.getTier().name());
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

	public static boolean isArmorPiece(@NotNull ItemStack item) {
		org.bukkit.Material material = item.getType();

		return material.name().contains("HELMET") ||
				material.name().contains("CHESTPLATE") ||
				material.name().contains("LEGGINGS") ||
				material.name().contains("BOOTS");
	}

	public static Type getType(ItemStack item) {
		String materialName = item.getType().name();

		if (materialName.contains("HELMET"))
			return Type.HELMET;
		if (materialName.contains("CHESTPLATE"))
			return Type.CHESTPLATE;
		if (materialName.contains("LEGGINGS"))
			return Type.LEGGINGS;
		if (materialName.contains("BOOTS"))
			return Type.BOOTS;

		return null;
	}

	public static boolean matchTier(@NotNull ItemStack a, @NotNull ItemStack b) {
		return CompMetadata.getMetadata(a, "tier").equals(CompMetadata.getMetadata(b, "tier"));
	}

	@Nullable
	public static ArmorPiece match(@Nullable ItemStack item) {

		if (item == null)
			return null;

		if (CompMetadata.hasMetadata(item, "tier")) {
			ArmorSet.Tier tier = ArmorSet.Tier.valueOf(CompMetadata.getMetadata(item, "tier"));

			return ArmorSet.getPiece(tier == ArmorSet.Tier.ONE ? ArmorSet.getFirstTier() : ArmorSet.getSecondTier(), ArmorPiece.getType(item));
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
