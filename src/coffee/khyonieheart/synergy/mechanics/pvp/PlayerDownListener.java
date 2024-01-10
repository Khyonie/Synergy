package coffee.khyonieheart.synergy.mechanics.pvp;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.synergy.Synergy;

public class PlayerDownListener implements Listener
{
	private static Map<Player, Player> downedPlayers = new HashMap<>(); // Map of killed/killers

	@EventHandler
	public void onPvpKill(
		EntityDamageByEntityEvent event
	) {
		if (!(event.getEntity() instanceof Player))
		{
			return;
		}

		Player downed = (Player) event.getEntity();

		// Check for lethal damage
		if (downed.getHealth() - event.getFinalDamage() > 0)
		{
			return;
		}

		Player killer = getPlayerKiller(event);

		if (downedPlayers.containsKey(downed))
		{
			// Execute
			downedPlayers.remove(downed);
			return;
		}

		// Down
		event.setDamage(0);
		downed.setHealth(1); // Half a heart
		downedPlayers.put(downed, killer);
		downed.sendTitle("§cDown", "§3Defeated by " + Synergy.getName(killer), 10, 70, 20);
		for (Player p : Bukkit.getOnlinePlayers())
		{
			Message.send(p, Synergy.getName(downed) + " was defeated by " + Synergy.getName(killer));
		}
	}

	// Utility methods
	//-------------------------------------------------------------------------------- 

	private static Player getPlayerKiller(
		EntityDamageByEntityEvent event
	) {
		if (event.getDamager() instanceof Projectile projectile)
		{
			if (projectile.getShooter() instanceof Player)
			{
				return (Player) projectile.getShooter();
			}
		}

		if (event.getDamager() instanceof Player)
		{
			return (Player) event.getDamager();
		}

		return null;
	}
}
