package coffee.khyonieheart.synergy.mechanics.augment.items;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import coffee.khyonieheart.synergy.crafting.material.ComplexMaterial;
import coffee.khyonieheart.synergy.crafting.recipe.ComplexShapedRecipe;
import coffee.khyonieheart.synergy.mechanics.augment.AugmentationManager;

public class EmptyAugmentationRecipe extends ComplexShapedRecipe
{
	private static final ItemStack EMPTY_AUGMENT_ITEM = new ItemStack(Material.CLAY_BALL);
	private static final ComplexMaterial material = new ComplexMaterial(Material.CLAY_BALL, 1).setKey("baseAugment");
	static {
		ComplexMaterial.setMaterialKey(EMPTY_AUGMENT_ITEM, material);
		AugmentationManager.setAsAugmentationCore(EMPTY_AUGMENT_ITEM);

		ItemMeta meta = EMPTY_AUGMENT_ITEM.getItemMeta();
		meta.setDisplayName("§eAugmentation (Empty)");
		meta.setLore(List.of("§7§oAn empty augmentation item,", "§7§oto be filled and attached", "§7§oto an tool or piece of", "§7§oarmor."));

		EMPTY_AUGMENT_ITEM.setItemMeta(meta);
	}

	public EmptyAugmentationRecipe() 
	{
		super("EMPTY_AUGMENTATION", " c ", "cgc", " c ");
		this.setIngredient('c', ComplexMaterial.adapt(Material.COPPER_INGOT))
			.setIngredient('g', ComplexMaterial.adapt(Material.GLASS))
			.setResult(EMPTY_AUGMENT_ITEM);
	}
}
