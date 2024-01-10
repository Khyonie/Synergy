package coffee.khyonieheart.synergy.crafting.material;

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import coffee.khyonieheart.hyacinth.api.RuntimeConditions;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Range;

public class ComplexMaterial implements EnhancedMaterial
{
	static NamespacedKey COMPLEX_MATERIAL_ID = new NamespacedKey("synergy", "complexmaterialid");
	public static MultiComplexMaterial ALL_PLANKS_MATERIAL = new MultiComplexMaterial(1, Tag.PLANKS);

	private final Material material;
	private final int count;
	private String key;

	public ComplexMaterial(
		@NotNull Material material,
		@Range(minimum = 1, maximum = 64) int count
	) {
		this.material = Objects.requireNonNull(material);
		this.count = RuntimeConditions.requireWithinRange(count, 1, 64);
	}

	ComplexMaterial(
		@Range(minimum = 1, maximum = 64) int count
	) {
		this.count = RuntimeConditions.requireWithinRange(count, 1, 64);
		this.material = null;
	}

	public ComplexMaterial setKey(
		@NotNull String key
	) {
		this.key = Objects.requireNonNull(key);
		return this;
	}

	public static ItemStack setMaterialKey(
		@NotNull ItemStack item,
		@NotNull ComplexMaterial material
	) {
		Objects.requireNonNull(item);

		item.getItemMeta().getPersistentDataContainer().set(COMPLEX_MATERIAL_ID, PersistentDataType.STRING, material.key);

		return item;
	}

	public static ComplexMaterial adapt(
		Material material
	) {
		return new ComplexMaterial(material, 1);
	}

	@Override
	public boolean matches(
		ItemStack item
	) {
		if (item == null && this.material != Material.AIR)
		{
			return false;
		}

		if ((isComplexMaterial(item) && this.key == null) || (!isComplexMaterial(item) && this.key != null))
		{
			return false;
		}

		if (item.getAmount() < this.count)
		{
			return false;
		}

		if (isComplexMaterial(item))
		{
			return item.getType() == this.material && item.getItemMeta().getPersistentDataContainer().get(COMPLEX_MATERIAL_ID, PersistentDataType.STRING).equals(this.key);
		}

		return item.getType() == this.material;
	}

	public static boolean isComplexMaterial(
		ItemStack item
	) {
		if (item == null)
		{
			return false;
		}

		return item.getItemMeta().getPersistentDataContainer().has(COMPLEX_MATERIAL_ID, PersistentDataType.STRING);
	}
}
