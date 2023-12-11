package coffee.khyonieheart.synergy.mechanics;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EquipmentSlot;

import coffee.khyonieheart.synergy.Synergy;

public class DoubleJumpListener implements Listener
{
	@EventHandler
	public void onPlayerFly(
		PlayerToggleFlightEvent event
	) {
		if (!Synergy.getProfileManager().getProfile(event.getPlayer()).getEnableDoubleJump())
		{
			return;
		}

		if (event.getPlayer().getInventory().getItem(EquipmentSlot.CHEST) != null)
		{
			if (event.getPlayer().getInventory().getItem(EquipmentSlot.CHEST).getType() == Material.ELYTRA)
			{
				return;
			}
		}

		event.setCancelled(true);

		if (!event.getPlayer().getAllowFlight())
		{
			return;
		}

		if (event.isFlying())
		{
			event.getPlayer().setVelocity(event.getPlayer().getVelocity().multiply(1.05).setY(1));
			event.getPlayer().setAllowFlight(false);
		}
	}

	@SuppressWarnings("deprecation") // It's fine. I don't really care if the client spoofs being on the ground
	@EventHandler
	public void onPlayerMove(
		PlayerMoveEvent event
	) {
		if (!Synergy.getProfileManager().getProfile(event.getPlayer()).getEnableDoubleJump())
		{
			return;
		}

		if (event.getPlayer().getInventory().getItem(EquipmentSlot.CHEST) != null)
		{
			if (event.getPlayer().getInventory().getItem(EquipmentSlot.CHEST).getType() == Material.ELYTRA)
			{
				return;
			}
		}

		if (event.getPlayer().isOnGround())
		{
			event.getPlayer().setAllowFlight(true);
		}
	}
}
