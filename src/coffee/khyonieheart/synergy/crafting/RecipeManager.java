package coffee.khyonieheart.synergy.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.synergy.crafting.recipe.ComplexRecipe;
import coffee.khyonieheart.synergy.crafting.recipe.ComplexShapedRecipe;
import coffee.khyonieheart.synergy.crafting.recipe.ComplexShapelessRecipe;
import coffee.khyonieheart.synergy.crafting.rules.CraftingRule;

public class RecipeManager
{
	private static List<ComplexRecipe> registeredRecipes = new ArrayList<>();
	private static List<CraftingRule> registeredRules = new ArrayList<>();

	public static void registerRecipe(
		ComplexRecipe recipe
	) {
		registeredRecipes.add(recipe);
	}

	public static void registerRule(
		CraftingRule rule
	) {
		registeredRules.add(rule);
	}

	public static List<ComplexRecipe> getRecipes()
	{
		return registeredRecipes;
	}

	public static List<CraftingRule> getRules()
	{
		return registeredRules;
	}

	public static void adaptAllRecipes()
	{
		Logger.log("Adapting Bukkit recipes to Synergy recipes...");
		Iterator<Recipe> iter = Bukkit.getServer().recipeIterator();
		Recipe recipe;
		while (iter.hasNext())
		{
			recipe = iter.next();

			if (!(recipe instanceof CraftingRecipe))
			{
				continue;
			}

			if (recipe instanceof ShapedRecipe shaped)
			{
				registeredRecipes.add((ComplexRecipe) ComplexShapedRecipe.adapt(shaped));
				continue;
			}

			registeredRecipes.add((ComplexRecipe) ComplexShapelessRecipe.adapt((ShapelessRecipe) recipe));
		}

		Logger.log("Registered " + registeredRecipes.size() + " recipe(s)");
	}

	public static ComplexShapedRecipe newShapedRecipe()
	{
		return new ComplexShapedRecipe("   ", "   ", "   ");
	}

	public static void clearVanillaRecipes()
	{
		Bukkit.getServer().clearRecipes();
	}
}
