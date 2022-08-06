package me.wilko.flightarmor.settings;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData extends YamlConfig {

	private static final Map<UUID, PlayerData> PLAYER_DATA = new HashMap<>();

	@Getter
	private final UUID uuid;

	@Getter
	private double flySpeed = 0.1D;

	@Getter
	private SerializedMap oldAttributes = new SerializedMap();

	public PlayerData(UUID uuid) {
		this.uuid = uuid;

		loadConfiguration(NO_DEFAULT, "players/" + uuid + ".yml");
	}

	@Override
	protected void onLoad() {
		this.flySpeed = getDouble("fly-speed", 0.1D);
		this.oldAttributes = getMap("old-attributes");
	}

	@Override
	protected void onSave() {
		set("fly-speed", this.flySpeed);
		set("old-attributes", this.oldAttributes);
	}

	public void setFlySpeed(double speed) {
		this.flySpeed = speed;

		save();
	}

	public void setOldAttributes(Map<String, Double> attributes) {
		this.oldAttributes = new SerializedMap();

		for (Map.Entry<String, Double> entry : attributes.entrySet()) {
			this.oldAttributes.put(entry.getKey(), entry.getValue());
		}

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
