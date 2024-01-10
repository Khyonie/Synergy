package coffee.khyonieheart.synergy.crafting.recipe;

import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.hyacinth.module.marker.PreventAutoLoad;

@PreventAutoLoad
public abstract class ComplexRecipe
{
	private ItemStack result;
	private String identifier;

	public ComplexRecipe(
		String identifier
	) {
		this.identifier = identifier;
	}

	public abstract boolean matches(
		ItemStack[] matrix
	);

	public ComplexRecipe setResult(
		ItemStack result
	) {
		this.result = result;
		return this;
	}

	public ItemStack getResult()
	{
		return this.result;
	}

	public String getIdentifier()
	{
		return this.identifier;
	}
}
