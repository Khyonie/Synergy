package coffee.khyonieheart.synergy.mechanics;

import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;

public class WakeUpHealListener implements Listener
{
	@EventHandler
	public void onWakeUp(
		PlayerBedLeaveEvent event
	) {
		if (event.getBed().getWorld().getTime() < 12000)
		{
			event.getPlayer().setHealth(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		}
	}
}
