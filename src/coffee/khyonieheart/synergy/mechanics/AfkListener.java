package coffee.khyonieheart.synergy.mechanics;

import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import coffee.khyonieheart.hyacinth.Gradient;
import coffee.khyonieheart.hyacinth.Gradient.GradientGroup;
import coffee.khyonieheart.synergy.Synergy;
import coffee.khyonieheart.synergy.api.gatedevents.GatedBranch;
import coffee.khyonieheart.synergy.api.gatedevents.GatedEventHandler;
import coffee.khyonieheart.synergy.api.gatedevents.GatedListener;

public class AfkListener implements GatedListener
{
	@GatedEventHandler(branch = GatedBranch.BETA)
	public void onMove(
		PlayerMoveEvent event
	) {
		if (Synergy.getAfkPlayers().contains(event.getPlayer()))
		{
			event.getPlayer().spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You are no longer AFK.", "#FFAA00", "#FFFFFF")));
			Synergy.getAfkPlayers().remove(event.getPlayer());
			Synergy.getTabListManager().update(event.getPlayer());
		}
	}

	@GatedEventHandler(branch = GatedBranch.BETA, priority = EventPriority.LOW)
	public void onDamage(
		EntityDamageEvent event
	) {
		if (event.getEntity() instanceof Player player)
		{
			if (Synergy.getAfkPlayers().contains(player))
			{
				event.setCancelled(true);
			}
		}
	}


	@GatedEventHandler(branch = GatedBranch.BETA)
	public void onTarget(
		EntityTargetLivingEntityEvent event
	) {
		if (event.getTarget() instanceof Player player)
		{
			if (Synergy.getAfkPlayers().contains(player))
			{
				event.setCancelled(true);
			}
		}
	}
}
