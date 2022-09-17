package me.wilko.flightarmor.model;

import lombok.Getter;
import lombok.Setter;
import me.wilko.flightarmor.settings.PlayerData;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ArmorSet {

	@Getter
	@Setter
	private static ArmorSet firstTier;
	@Getter
	@Setter
	private static ArmorSet secondTier;

	private ArmorPiece helmet;
	private ArmorPiece chestplate;
	private ArmorPiece leggings;
	private ArmorPiece boots;

	private final Map<String, Double> attributes = new HashMap<>();

	@Getter
	private final Tier tier;
	@Getter
	private final ArmorPiece.Material material;

	public ArmorSet(Tier tier, ArmorPiece.Material material) {
		this.tier = tier;
		this.material = material;
	}

	/**
	 * Returns the particular type of armor piece of this set
	 *
	 * @return null if no piece was found of this type, which shouldn't ever happen
	 */
	public ArmorPiece getPiece(ArmorPiece.Type type) {

		if (type == ArmorPiece.Type.HELMET)
			return helmet;
		else if (type == ArmorPiece.Type.CHESTPLATE)
			return chestplate;
		else if (type == ArmorPiece.Type.LEGGINGS)
			return leggings;
		else if (type == ArmorPiece.Type.BOOTS)
			return boots;

		return null;
	}

	public void set(ArmorPiece.Type type, ArmorPiece piece) {

		if (type == ArmorPiece.Type.HELMET)
			helmet = piece;
		else if (type == ArmorPiece.Type.CHESTPLATE)
			chestplate = piece;
		else if (type == ArmorPiece.Type.LEGGINGS)
			leggings = piece;
		else if (type == ArmorPiece.Type.BOOTS)
			boots = piece;
	}

	public List<ArmorPiece> getAllPieces() {

		return Arrays.asList(
				helmet,
				chestplate,
				leggings,
				boots
		);
	}

	public void addAttribute(String enumKey, double val) {
		this.attributes.put(enumKey, val);
	}

	public void setAttributesFor(Player player) {

		PlayerData data = PlayerData.lookup(player);

		List<me.wilko.flightarmor.settings.Attribute> oldAttributes = new ArrayList<>();

		for (Map.Entry<String, Double> entry : attributes.entrySet()) {

			Attribute bukkitAttr = Attribute.valueOf(entry.getKey());
			AttributeInstance bukkitAttrInstance = player.getAttribute(bukkitAttr);

			// Player does not have this attribute
			if (bukkitAttrInstance == null)
				continue;

			// Saves the previous value of the attribute
			oldAttributes.add(new me.wilko.flightarmor.settings.Attribute(bukkitAttrInstance.getAttribute(), bukkitAttrInstance.getBaseValue(), new ArrayList<>(bukkitAttrInstance.getModifiers())));

			// Changes its base value
			player.getAttribute(bukkitAttr).setBaseValue(entry.getValue());
		}

		data.setOldAttributes(oldAttributes);
	}

	public static void resetAttributes(Player player) {

		PlayerData data = PlayerData.lookup(player);

		for (me.wilko.flightarmor.settings.Attribute attribute : data.getOldAttributes()) {

			// Sets base value
			player.getAttribute(attribute.getBukkitAttr()).setBaseValue(attribute.getBaseVal());

			for (AttributeModifier modifier : player.getAttribute(attribute.getBukkitAttr()).getModifiers()) {
				for (AttributeModifier oldModifier : attribute.getModifiers()) {

					if (modifier.getName().equals(oldModifier.getName())) {

						// Remove current modifier
						player.getAttribute(attribute.getBukkitAttr()).removeModifier(modifier);

						// Add old one
						player.getAttribute(attribute.getBukkitAttr()).addModifier(oldModifier);
					}
				}
			}
		}
	}

	/**
	 * Returns the armor set corresponding to the given set
	 *
	 * @return null if no armorSet of the given set exists
	 */
	@Nullable
	public static ArmorSet getSet(ItemStack[] armorSet) {

		ArmorSet set = null;
		for (ItemStack item : armorSet) {

			// If any of the slots are empty, return null
			if (item == null)
				return null;

			// Find a matching armor piece
			ArmorPiece piece = ArmorPiece.match(item);
			if (piece == null)
				return null;

			// Check that the pieces all belong to the same set
			if (set == null)
				set = piece.getBelongingSet();
			else if (set != piece.getBelongingSet())
				return null;
		}

		return set;
	}

	/**
	 * Gets a particular armor piece from a particular set
	 */
	@Nullable
	public static ArmorPiece getPiece(ArmorSet set, ArmorPiece.Type type) {
		return set.getPiece(type);
	}

	public enum Tier {

		VANILLA,
		ONE,
		TWO
	}
}
