package me.wilko.flightarmor.settings;

import lombok.Getter;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.*;

public class PlayerData extends YamlConfig {

	private static final Map<UUID, PlayerData> PLAYER_DATA = new HashMap<>();

	@Getter
	private final UUID uuid;

	@Getter
	private double flySpeed = 0.1D;

	@Getter
	private List<Attribute> oldAttributes;

	public PlayerData(UUID uuid) {
		this.uuid = uuid;

		loadConfiguration(NO_DEFAULT, "players/" + uuid + ".yml");
	}

	@Override
	protected void onLoad() {
		setPathPrefix(null);

		this.flySpeed = getDouble("fly-speed", 0.1D);

		List<Attribute> attributes = new ArrayList<>();
		for (String attrName : getMap("old-attributes").keySet()) {
			setPathPrefix("old-attributes." + attrName);

			org.bukkit.attribute.Attribute bukkitAttr = org.bukkit.attribute.Attribute.valueOf(attrName);
			double baseVal = getDouble("base-val");

			List<AttributeModifier> modifiers = new ArrayList<>();
			for (String modifierName : getMap("modifiers").keySet()) {
				modifiers.add(AttributeModifier.deserialize(getMap(modifierName).asMap()));
			}

			attributes.add(new Attribute(bukkitAttr, baseVal, modifiers));
		}

		this.oldAttributes = attributes;
	}

	@Override
	protected void onSave() {
		set("fly-speed", this.flySpeed);

		for (Attribute attribute : this.oldAttributes) {

			set("old-attributes." + attribute.getBukkitAttr().name() + ".base-val", attribute.getBaseVal());

			List<Map<String, Object>> modifierMap = new ArrayList<>();
			for (AttributeModifier modifier : attribute.getModifiers()) {
				modifierMap.add(modifier.serialize());
			}

			set("old-attributes." + attribute.getBukkitAttr().name() + ".modifiers", modifierMap);
		}
	}

	public void setFlySpeed(double speed) {
		this.flySpeed = speed;

		save();
	}

	public void setOldAttributes(List<Attribute> attributes) {
		this.oldAttributes = attributes;

		save();
	}

	public static PlayerData lookup(Player player) {
		return lookup(player.getUniqueId());
	}

	public static PlayerData lookup(UUID uuid) {

		if (PLAYER_DATA.containsKey(uuid))
			return PLAYER_DATA.get(uuid);

		PlayerData newData = new PlayerData(uuid);
		PLAYER_DATA.put(uuid, newData);

		return newData;
	}
}
