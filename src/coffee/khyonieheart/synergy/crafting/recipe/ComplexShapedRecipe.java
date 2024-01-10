package coffee.khyonieheart.synergy.crafting.recipe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.module.marker.PreventAutoLoad;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.synergy.crafting.material.ComplexMaterial;

@PreventAutoLoad
public class ComplexShapedRecipe extends ComplexRecipe
{
	private Map<Character, ComplexMaterial> materialMap = new HashMap<>();
	private String[] recipe = new String[] { "   ", "   ", "   " };

	public ComplexShapedRecipe(
		String identifier,
		String... recipe
	) {
		super(identifier);
		if (recipe.length == 0)
		{
			throw new IllegalArgumentException("At least one recipe line must be provided");
		}
		this.recipe = Arrays.copyOf(recipe, 3);

		setShape(recipe);
	}

	public ComplexShapedRecipe setShape(
		String... shape
	) {
		if (shape.length == 0)
		{
			throw new IllegalArgumentException("At least one recipe line must be provided");
		}

		
		// Attempt to fix null, incomplete, or excessively-sized recipe lines
		for (int i = 0; i < 3; i++)
		{
			if (recipe[i] == null)
			{
				recipe[i] = "   ";
				continue;
			}

			while (recipe[i].length() < 3)
			{
				recipe[i] = recipe[i] + " ";
			}

			if (recipe[i].length() > 3) 
			{
				recipe[i] = recipe[i].substring(0, 3);
			}
		}

		return this;
	}

	@Override
	public boolean matches(
		ItemStack[] matrix
	) {
		for (int i = 0; i < 3; i++)
		{
			String line = this.recipe[i];
			for (int o = 0; o < 3; o++)
			{
				ComplexMaterial material = this.materialMap.get(line.charAt(o));
				if (material == null)
				{
					if (matrix[(i * 3) + o] != null)
					{
						return false;
					}
					continue;
				}

				if (!material.matches(matrix[(i * 3) + o]))
				{
					return false;
				}
			}
		}
		return true;
	}

	public ComplexShapedRecipe setIngredient(
		char id,
		ComplexMaterial material
	) {
		this.materialMap.put(id, material);
		return this;
	}

	@Override
	public ComplexShapedRecipe setResult(
		ItemStack item
	) {
		super.setResult(item);
		return this;
	}
	
	public static ComplexShapedRecipe adapt(
		@NotNull ShapedRecipe recipe
	) {
		ComplexShapedRecipe complexRecipe = new ComplexShapedRecipe("BUKKIT_SHAPED#" + recipe.getResult().getType().name(), recipe.getShape());
		for (char c : recipe.getIngredientMap().keySet())
		{
			// Oak planks workaround
			ItemStack material = recipe.getIngredientMap().get(c);
			if (material != null)
			{
				if (material.getType() == Material.OAK_PLANKS && !recipe.getResult().getType().name().contains("OAK"))
				{
					complexRecipe.setIngredient(c, ComplexMaterial.ALL_PLANKS_MATERIAL);
					continue;
				}
			}

			complexRecipe.setIngredient(c, recipe.getIngredientMap().get(c) == null ? null : ComplexMaterial.adapt(recipe.getIngredientMap().get(c).getType()));
		}

		complexRecipe.setResult(recipe.getResult());
		return complexRecipe;
	}
}
