package coffee.khyonieheart.synergy.api;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;

public class Items
{
	private static final NamespacedKey KEY = new NamespacedKey("synergy", "unique");

	public static ItemStack uniquify(
		@NotNull ItemStack item
	) {
		Objects.requireNonNull(item);

		PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
		dataContainer.set(KEY, PersistentDataType.STRING, UUID.randomUUID().toString());

		return item;
	}
}
