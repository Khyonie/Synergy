package coffee.khyonieheart.synergy.mechanics.deathchest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.google.gson.annotations.Expose;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.synergy.api.Items;

public class DeathChest
{
	@Expose private List<TinyLocation> locations = new ArrayList<>();
	@Expose private String ownerUUID;
	@Expose private String ticketUUID;
	@Expose boolean locked = false;

	public DeathChest(
		@NotNull Player player,
		@NotNull Location... locations
	) {
		Objects.requireNonNull(locations);
		if (locations.length == 0)
		{
			throw new IllegalArgumentException("At least one location must be specified");
		}

		for (Location loc : locations)
		{
			this.locations.add(new TinyLocation(loc));
		}

		this.ownerUUID = player.getUniqueId().toString();
		this.ticketUUID = UUID.randomUUID().toString();
	}

	public String getTicketUUID()
	{
		return this.ticketUUID;
	}

	public boolean isLocked()
	{
		return this.locked;
	}

	public boolean isAuthorizedPlayer(
		Player player
	) {
		for (ItemStack item : player.getInventory().getContents())
		{
			if (matchesTicket(item))
			{
				return true;
			}
		}

		return player.getUniqueId().toString().equals(this.ownerUUID);
	}

	public boolean matchesTicket(
		ItemStack item
	) {
		PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();

		return dataContainer.get(DeathChestManager.KEY, PersistentDataType.STRING).equals(this.ticketUUID);
	}

	public List<Location> locations()
	{
		return this.locations.stream()
			.map(s -> s.getLocation())
			.toList();
	}

	public ItemStack generateTicket()
	{
		ItemStack item = new ItemStack(Material.PAPER);

		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName("§eDeath Ticket");

		String uuid = UUID.randomUUID().toString();
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		dataContainer.set(DeathChestManager.KEY, PersistentDataType.STRING, ownerUUID + "#" + uuid);

		List<String> lore = new ArrayList<>();
		for (int i = 0; i < locations.size(); i++)
		{
			lore.add("§7Chest #" + (i + 1) + ": " + this.locations.get(i));
		}

		if (this.isLocked())
		{
			lore.add("");
			lore.add("§7§oThese death chests are");
			lore.add("§7§olocked, and require this");
			lore.add("§7§oticket to be unlocked by");
			lore.add("§7§oother players.");
		}

		meta.setLore(lore);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.DURABILITY, 0);

		Items.uniquify(item);

		return item;
	}

	public static class TinyLocation
	{
		public TinyLocation(
			@NotNull Location location
		) {
			Objects.requireNonNull(location);
			this.x = location.getBlockX();
			this.y = location.getBlockY();
			this.z = location.getBlockZ();

			this.world = location.getWorld().getName();
		}

		@Expose
		private int x, y, z;
		@Expose
		private String world;

		@NotNull
		public Location getLocation()
		{
			return new Location(Bukkit.getWorld(world), x, y, z);
		}

		@Override
		public String toString()
		{
			return this.x + ", " + this.y + ", " + this.z + " in world " + this.world;
		}
	}
}
