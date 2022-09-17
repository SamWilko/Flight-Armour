package me.wilko.flightarmor.settings;

import lombok.Data;
import org.bukkit.attribute.AttributeModifier;

import java.util.List;

@Data
public class Attribute {

	private final org.bukkit.attribute.Attribute bukkitAttr;
	private final double baseVal;
	private final List<AttributeModifier> modifiers;

	public Attribute(org.bukkit.attribute.Attribute bukkitAttr, double baseVal, List<AttributeModifier> modifiers) {
		this.bukkitAttr = bukkitAttr;
		this.baseVal = baseVal;
		this.modifiers = modifiers;
	}
}
