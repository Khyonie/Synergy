package coffee.khyonieheart.synergy.crafting.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.module.marker.PreventAutoLoad;
import coffee.khyonieheart.synergy.crafting.material.ComplexMaterial;

@PreventAutoLoad
public class ComplexShapelessRecipe extends ComplexRecipe
{
	private List<ComplexMaterial> materials;

	public ComplexShapelessRecipe(
		String identifier,
		ComplexMaterial... materials
	) {
		super(identifier);
		this.materials = Arrays.asList(materials);
	}

	@Override
	public boolean matches(
		ItemStack[] matrix
	) {
		List<ItemStack> presentMaterials = new ArrayList<>(
			Arrays.asList(matrix).stream()
				.filter(i -> i != null)
				.toList()
		);

		List<ComplexMaterial> requiredMaterials = new ArrayList<>(materials);

		Iterator<ItemStack> presentIter;
		Iterator<ComplexMaterial> requiredIter = requiredMaterials.iterator();
		mat: while (requiredIter.hasNext())
		{
			ComplexMaterial material = requiredIter.next();
			if (material == null)
			{
				continue;
			}

			presentIter = presentMaterials.iterator();
			while (presentIter.hasNext())
			{
				if (material.matches(presentIter.next()))
				{
					presentIter.remove();
					requiredIter.remove();
					continue mat;
				}
			}
		}

		if (!presentMaterials.isEmpty() || !requiredMaterials.isEmpty())
		{
			return false;
		}

		return true;
	}

	@Override
	public ComplexShapelessRecipe setResult(
		ItemStack item
	) {
		super.setResult(item);
		return this;
	}

	public static ComplexShapelessRecipe adapt(
		ShapelessRecipe recipe
	) {
		//Logger.debug("Adapting shapeless recipe for item " + recipe.getResult().getType().name() + " x" + recipe.getResult().getAmount());
		ComplexShapelessRecipe complexRecipe = new ComplexShapelessRecipe(
			"BUKKIT_SHAPELESS#" + recipe.getResult().getType().name(),
			coffee.khyonieheart.hyacinth.util.Arrays.toArray(
				ComplexMaterial[].class, 
				recipe.getIngredientList().stream()
					.map(i -> ComplexMaterial.adapt(i.getType()))
					.toList()
			)
		)
			.setResult(recipe.getResult());

		return complexRecipe;
	}
}
