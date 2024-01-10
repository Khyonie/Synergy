package coffee.khyonieheart.synergy.mechanics.deathchest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

public class DeathChestManager
{
	public static final NamespacedKey KEY = new NamespacedKey("synergy", "deathchest");

	private static List<DeathChest> knownDeathChests = new ArrayList<>();

	public static void addDeathChest(
		DeathChest chest
	) {
		knownDeathChests.add(chest);
	}

	public static boolean isTicketItem(
		@Nullable ItemStack item
	) {
		if (item == null)
		{
			return false;
		}

		ItemMeta meta = item.getItemMeta();
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

		return dataContainer.has(KEY, PersistentDataType.STRING);
	}

	public boolean isDeathChest(
		@NotNull Block block
	) {
		Objects.requireNonNull(block);

		for (DeathChest dc : knownDeathChests)
		{
			for (Location loc : dc.locations())
			{
				if (loc.equals(block.getLocation()))
				{
					return true;
				}
			}
		}

		return false;
	}
}
