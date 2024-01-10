package coffee.khyonieheart.synergy.mechanics.party;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import coffee.khyonieheart.synergy.Synergy;

public class PartyListener implements Listener
{
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDisconnect(
		PlayerQuitEvent event
	) {
		if (Synergy.getPartyManager().isInParty(event.getPlayer()))
		{
			Synergy.getPartyManager().getParty(event.getPlayer()).leaveParty(event.getPlayer());
		}
	}
}
