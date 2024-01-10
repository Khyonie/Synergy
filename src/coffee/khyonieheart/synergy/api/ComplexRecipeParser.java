package coffee.khyonieheart.synergy.api;

import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.ClassShader;
import coffee.khyonieheart.hyacinth.util.Reflect;
import coffee.khyonieheart.synergy.crafting.RecipeManager;
import coffee.khyonieheart.synergy.crafting.recipe.ComplexRecipe;

public class ComplexRecipeParser implements ClassShader<ComplexRecipe>
{
	@Override
	public Class<ComplexRecipe> getType() 
	{
		return ComplexRecipe.class;
	}

	@Override
	public ComplexRecipe process(
		Class<? extends ComplexRecipe> type,
		ComplexRecipe instance
	) {
		if (instance != null)
		{
			RecipeManager.registerRecipe(instance);
			return null;
		}

		ComplexRecipe recipe = Reflect.simpleInstantiate(type);
		RecipeManager.registerRecipe(recipe);
		return recipe;
	}
}
