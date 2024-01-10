package coffee.khyonieheart.synergy.crafting;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.hyacinth.killswitch.Feature;
import coffee.khyonieheart.hyacinth.killswitch.FeatureIdentifier;
import coffee.khyonieheart.synergy.api.gatedevents.GatedBranch;
import coffee.khyonieheart.synergy.api.gatedevents.GatedEventHandler;
import coffee.khyonieheart.synergy.api.gatedevents.GatedListener;

@FeatureIdentifier("enhancedCrafting")
public class CraftOverrideListener implements GatedListener, Feature
{
	private static boolean isEnabled = true; // Fuck it we ball
	private static Map<Player, EnhancedCrafter> activeCraftingTables = new HashMap<>();

	@GatedEventHandler(branch = GatedBranch.NIGHTLY)
	public void onCraftPrepare(
		PrepareItemCraftEvent event
	) {
		if (!isEnabled)
		{
			return;
		}

		CraftingInventory inventory = event.getInventory();
		if (inventory.getResult() != null)
		{
			inventory.setResult(null);
		}

		EnhancedCrafter crafter = getCrafter((Player) event.getView().getPlayer());

		if (crafter == null)
		{
			return;
		}

		crafter.attemptCrafting(inventory.getMatrix(), inventory);
	}

	// Crafter instancing
	//-------------------------------------------------------------------------------- 

	private static EnhancedCrafter getCrafter(
		Player player
	) {
		return activeCraftingTables.get(player);
	}

	@GatedEventHandler(branch = GatedBranch.NIGHTLY)
	public void onCraftingTableOpen(
		InventoryOpenEvent event
	) {
		if (!(event.getView().getTopInventory() instanceof CraftingInventory))
		{
			return;
		}

		Message.send(event.getPlayer(), "§aEnhanced crafter opened");
		activeCraftingTables.put((Player) event.getPlayer(), new EnhancedCrafter());
	}

	@GatedEventHandler(branch = GatedBranch.NIGHTLY)
	public void onCraftingTableClose(
		InventoryCloseEvent event
	) {
		if (activeCraftingTables.containsKey(event.getPlayer()))
		{
			// Drop all items
			// FIXME Red Test this
			Message.send(event.getPlayer(), "§aEnhanced crafter closed");
			for (ItemStack i : ((CraftingInventory) event.getView().getTopInventory()).getMatrix())
			{
				if (i == null)
				{
					continue;
				}
				event.getPlayer().getInventory().addItem(i);
			}

			activeCraftingTables.get(event.getPlayer()).close();
			activeCraftingTables.remove(event.getPlayer());
		}
	}

	// Feature toggles
	//-------------------------------------------------------------------------------- 

	@Override
	public boolean isEnabled(
		String id
	) {
		return switch (id) {
			case "enhancedCrafting" -> isEnabled;
			default -> false;
		};
	}

	@Override
	public boolean kill(
		String id
	) {
		return switch (id) {
			case "enhancedCrafting" -> {
				if (!isEnabled)
				{
					yield false;
				}

				yield !(isEnabled = false);
			}
			default -> false;
		};
	}

	@Override
	public boolean reenable(
		String id
	) {
		return switch (id) {
			case "enhancedCrafting" -> {
				if (isEnabled)
				{
					yield false;
				}

				yield isEnabled = false;
			}
			default -> false;
		};
	}
}
