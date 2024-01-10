package coffee.khyonieheart.synergy.crafting.material;

import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.hyacinth.util.marker.Nullable;

public interface EnhancedMaterial
{
	public boolean matches(
		@Nullable ItemStack item
	);
}
