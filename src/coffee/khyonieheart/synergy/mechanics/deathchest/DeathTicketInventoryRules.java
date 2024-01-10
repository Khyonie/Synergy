package coffee.khyonieheart.synergy.mechanics.deathchest;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class DeathTicketInventoryRules implements Listener
{
	// TODO Test this
	@EventHandler
	public void onInventoryInteract(
		PrepareItemCraftEvent event
	) {
		for (ItemStack i : event.getInventory().getMatrix())
		{
			if (DeathChestManager.isTicketItem(i))
			{
				event.getInventory().setResult(null);
				break;
			}
		}
	}
}
