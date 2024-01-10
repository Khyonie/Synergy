package coffee.khyonieheart.synergy.mechanics.party;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import coffee.khyonieheart.synergy.Synergy;

public class PartyPVPListener implements Listener
{
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerPVP(
		EntityDamageByEntityEvent event
	) {
		Entity damageeEntity = event.getEntity();

		if (!(damageeEntity instanceof Player))
		{
			return;
		}

		Player damagee = (Player) damageeEntity;

		Entity damagerEntity = event.getDamager();
		Player damagerPlayer;

		if (damagerEntity instanceof Projectile projectile)
		{
			if (!(projectile.getShooter() instanceof Player))
			{
				return;
			}

			damagerPlayer = (Player) projectile.getShooter();
		} else {
			if (!(damagerEntity instanceof Player player))
			{
				return;
			}

			damagerPlayer = (Player) damagerEntity;
		}

		if (!Synergy.getPartyManager().isInParty(damagee) || !Synergy.getPartyManager().isInParty(damagerPlayer))
		{
			return;
		}

		Party party = Synergy.getPartyManager().getParty(damagee);

		if (Synergy.getPartyManager().getParty(damagerPlayer).equals(party))
		{
			event.setCancelled(true);
		}
	}
}
