package coffee.khyonieheart.synergy.items;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.hyacinth.module.marker.PreventAutoLoad;

@PreventAutoLoad
public class UsableItemListener implements Listener
{
	@EventHandler
	public void onUse(
		PlayerInteractEvent event
	) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
		{
			return;
		}

		EquipmentSlot slot = EquipmentSlot.HAND;
		ItemStack item = event.getPlayer().getInventory().getItem(slot);	

		if (item == null)
		{
			slot = EquipmentSlot.OFF_HAND;
			item = event.getPlayer().getInventory().getItem(slot);
		}

		if (item == null)
		{
			return;
		}

		if (!UsableItem.isUsableItem(item))
		{
			return;
		}

		try {
			UsableItem.getAsUsableItem(item).use(event.getPlayer(), item, event, slot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
