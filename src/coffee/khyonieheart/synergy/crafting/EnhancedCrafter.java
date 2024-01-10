package coffee.khyonieheart.synergy.crafting;

import java.util.Arrays;

import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.synergy.crafting.recipe.ComplexRecipe;
import coffee.khyonieheart.synergy.crafting.rules.CraftingRule;

public class EnhancedCrafter
{
	public void attemptCrafting(
 		ItemStack[] matrix,
		CraftingInventory inventory
	) {
		if (bitmatrix(matrix) == 0)
		{
			return;
		}

		ItemStack[] matrixCopy = compress(matrix);

		for (CraftingRule rule : RecipeManager.getRules())
		{
			if (rule.match(matrixCopy, inventory))
			{
				return;
			}
		}

		long time = System.currentTimeMillis();
		int checked = 0;
		for (ComplexRecipe recipe : RecipeManager.getRecipes())
		{
			checked++;
			try {
				if (!recipe.matches(matrixCopy))
				{
					continue;
				}

				long delta = Math.max(System.currentTimeMillis() - time, 1);
				Logger.verbose("§aEnhanced crafting match found: " + recipe.getIdentifier() + " ( ~" + (checked / delta) + " recipe(s)/ms )");
				inventory.setResult(recipe.getResult());
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public short bitmatrix(
		ItemStack[] matrix
	) {
		short val = 0;
		for (int i = 0; i < matrix.length; i++)
		{
			if (matrix[i] != null)
			{
				val |= 0x8000 >> i;
			}
		}

		return val;
	}

	private static final short TOP_ROW_MASK = (short) (0b111_000_000 << 7);

	public ItemStack[] compress(
		ItemStack[] matrix
	) {
		ItemStack[] newMatrix = Arrays.copyOf(matrix, 9);

		short bitmatrix = bitmatrix(matrix);
		Logger.debug("Starting bitmatrix: " + Integer.toBinaryString(bitmatrix));
		Logger.debug("Matrix (start): [ " + coffee.khyonieheart.hyacinth.util.Arrays.toString(matrix, ", ", i -> (i == null ? "NULL" : i.getType().name())) + " ]");
		while ((bitmatrix & TOP_ROW_MASK) == 0)
		{
			newMatrix = Arrays.copyOf(Arrays.copyOfRange(newMatrix, 3, 9), 9);
			bitmatrix = bitmatrix(newMatrix);
		}

		Logger.debug("Matrix (vshift): [ " + coffee.khyonieheart.hyacinth.util.Arrays.toString(newMatrix, ", ", i -> (i == null ? "NULL" : i.getType().name())) + " ]");
		if (bitmatrix(newMatrix) == 0)
		{
			Logger.log("§eAfter vshift, crafting matrix became empty!");
			return newMatrix;
		}

		while ((newMatrix[0] == null) && (newMatrix[3] == null) && (newMatrix[6] == null))
		{
			for (int y = 0; y < 3; y++)
			{
				for (int x = 1; x < 3; x++)
				{
					newMatrix[(y * 3) + (x - 1)] = newMatrix[(y * 3) + x];
					newMatrix[(y * 3) + x] = null;
				}
			}
		}
		Logger.debug("Matrix (lshift): [ " + coffee.khyonieheart.hyacinth.util.Arrays.toString(newMatrix, ", ", i -> (i == null ? "NULL" : i.getType().name())) + " ]");
		Logger.debug("Ending bitmatrix: " + Integer.toBinaryString(bitmatrix));

		return newMatrix;
	}

	public void close()
	{

	}
}
