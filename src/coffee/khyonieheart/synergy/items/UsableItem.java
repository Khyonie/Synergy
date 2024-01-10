package coffee.khyonieheart.synergy.items;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import coffee.khyonieheart.hyacinth.module.marker.PreventAutoLoad;

@PreventAutoLoad
public interface UsableItem
{
	public static NamespacedKey KEY = new NamespacedKey("synergy", "usableitem");

	public void use(
		Player player,
		ItemStack item,
		PlayerInteractEvent event,
		EquipmentSlot hand
	);

	public static boolean isUsableItem(
		ItemStack item
	) {
		if (item == null)
		{
			return false;
		}
		return item.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.STRING);
	}

	public static UsableItem getAsUsableItem(
		ItemStack item
	) {
		return UsableItemManager.getItem(item.getItemMeta().getPersistentDataContainer().get(KEY, PersistentDataType.STRING));
	}
}
