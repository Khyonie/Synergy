package coffee.khyonieheart.synergy.mechanics.deathchest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.synergy.Synergy;

public class DeathChestListener implements Listener
{
	private static Map<String, List<ItemStack>> preservedTickets = new HashMap<>();

	@EventHandler(priority = EventPriority.HIGH)
	public void onDeath(
		PlayerDeathEvent event
	) {
		if (isDeathCausedByPlayer(event.getEntity().getLastDamageCause()))
		{
			event.setDroppedExp(0);
			event.setKeepLevel(true);
		}

		List<ItemStack> tickets = event.getDrops().stream()
			.filter(i -> DeathChestManager.isTicketItem(i))
			.toList();

		event.getDrops().removeAll(tickets);

		if (!preservedTickets.containsKey(event.getEntity().getUniqueId().toString()))
		{
			preservedTickets.put(event.getEntity().getUniqueId().toString(), new ArrayList<>());
		}

		preservedTickets.get(event.getEntity().getUniqueId().toString()).addAll(tickets);

		Location location = event.getEntity().getLocation();

		List<ItemStack> drops = new ArrayList<>(event.getDrops());
		List<Location> chestLocations = new ArrayList<>();
		while (!drops.isEmpty())
		{
			Block deathBlock = location.getBlock();

			// Locate appropriate block to replace
			int current = 0;
			int changeDirectionCount = 1;
			BlockFace direction = BlockFace.NORTH;
			Block targetBlock = deathBlock;
			long currentTime = System.currentTimeMillis();
			while (!targetBlock.isEmpty())
			{
				if (System.currentTimeMillis() - currentTime > 500)
				{
					// Screw it, replace the current block :thumbs_up:
					break;
				}

				targetBlock = targetBlock.getRelative(direction);
				current++;
				if (current == changeDirectionCount)
				{
					current = 0;
					direction = switch (direction)
					{
						case NORTH -> BlockFace.EAST;
						case EAST -> BlockFace.SOUTH;
						case SOUTH -> BlockFace.WEST;
						case WEST -> {
							changeDirectionCount++;
							yield BlockFace.NORTH;
						}
						default -> throw new IllegalStateException();
					};
				}

				if (changeDirectionCount > 15)
				{
					targetBlock = targetBlock.getRelative(BlockFace.UP);
					changeDirectionCount = 1;
				}
			}

			// FIXME Red This may introduce a problem if a player dies in midair above the end void
			while (targetBlock.getRelative(BlockFace.DOWN).isEmpty())
			{
				targetBlock = targetBlock.getRelative(BlockFace.DOWN);
			}

			targetBlock.setType(Material.CHEST);
			chestLocations.add(targetBlock.getLocation());

			Inventory chestInventory = ((Chest) targetBlock.getState()).getBlockInventory();
			drops = new ArrayList<>(chestInventory.addItem(drops.toArray(new ItemStack[drops.size()])).values());

			Chest tileData = (Chest) targetBlock.getState();
			tileData.setCustomName(Synergy.getName(event.getEntity()) + "'s Death Chest");
			tileData.update();
		}

		event.getDrops().clear();
		DeathChest deathChest;
		try {
			deathChest = new DeathChest(event.getEntity(), chestLocations.toArray(new Location[chestLocations.size()]));
			DeathChestManager.addDeathChest(deathChest);

			preservedTickets.get(event.getEntity().getUniqueId().toString()).add(deathChest.generateTicket());
		} catch (Exception e) {
			event.setKeepInventory(true);
			Message.send(event.getEntity(), "§cYou seem have triggered issue #22 (\"Death Chests Sometimes Fail with Zero Locations\"). You items will be given back to you, and you will keep your levels.");
			Message.send(event.getEntity(), "§cPlease submit a screenshot or text paste of this message to https://github.com/Khyonie/Synergy/issues/22 to assist in investigating this issue.");
			Message.send(event.getEntity(), "§c- Cause of death: " + event.getEntity().getLastDamageCause().getCause().name());
			Message.send(event.getEntity(), "§c- Current block: " + event.getEntity().getLocation().getBlock().getType().name());
		}
	}

	@EventHandler
	public void onRespawn(
		PlayerRespawnEvent event
	) {
		if (!preservedTickets.containsKey(event.getPlayer().getUniqueId().toString()))
		{
			return;
		}

		for (ItemStack item : preservedTickets.get(event.getPlayer().getUniqueId().toString()))
		{
			event.getPlayer().getInventory().addItem(item);
		}

		preservedTickets.remove(event.getPlayer().getUniqueId().toString());
	}

	private static boolean isDeathCausedByPlayer(
		EntityDamageEvent event
	) {
		if (!(event instanceof EntityDamageByEntityEvent))
		{
			return false;
		}

		EntityDamageByEntityEvent entityevent = (EntityDamageByEntityEvent) event;
		if (entityevent.getDamager() instanceof Projectile projectile)
		{
			return (projectile.getShooter() instanceof Player);
		}

		return (entityevent.getDamager() instanceof Player);
	}
}
