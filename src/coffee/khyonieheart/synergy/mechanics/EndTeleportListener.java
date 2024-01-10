package coffee.khyonieheart.synergy.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import coffee.khyonieheart.hyacinth.Message;

public class EndTeleportListener implements Listener
{
	@EventHandler
	public void onMove(
		PlayerMoveEvent event
	) {
		if (event.getTo().getWorld().getEnvironment() != Environment.THE_END)
		{
			return;
		}

		if (event.getTo().getBlockY() > -64)
		{
			return;
		}

		World overworld = Bukkit.getWorld("world");
		Location location = new Location(overworld, event.getTo().getBlockX(), overworld.getMaxHeight(), event.getTo().getBlockZ());
		event.getPlayer().teleport(location);
		Message.send(event.getPlayer(), "§7You have fallen out of the end.");
	}
}
