package coffee.khyonieheart.synergy.crafting.rules;

import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.hyacinth.module.marker.PreventAutoLoad;

@PreventAutoLoad
public interface CraftingRule
{
	public boolean match(
		ItemStack[] matrix,
		CraftingInventory inventory
	);
}
