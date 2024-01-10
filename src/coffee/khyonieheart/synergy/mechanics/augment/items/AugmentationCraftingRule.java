package coffee.khyonieheart.synergy.mechanics.augment.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.Event;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import coffee.khyonieheart.synergy.crafting.rules.CraftingRule;
import coffee.khyonieheart.synergy.mechanics.augment.Augmentation;
import coffee.khyonieheart.synergy.mechanics.augment.AugmentationManager;

public class AugmentationCraftingRule implements CraftingRule
{
	private static Set<Material> AUGMENTABLE_MATERIALS = new HashSet<>();
	static {
		AUGMENTABLE_MATERIALS.addAll(Tag.ITEMS_TOOLS.getValues());
		AUGMENTABLE_MATERIALS.addAll(Tag.ITEMS_SWORDS.getValues());
		AUGMENTABLE_MATERIALS.addAll(Set.of(
			Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
			Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
			Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
			Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS,
			Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS
		));
	}

	@Override
	public boolean match(
		ItemStack[] matrix,
		CraftingInventory inventory
	) {
		List<ItemStack> items = Arrays.asList(matrix).stream()
			.filter(i -> i != null)
			.toList();

		if (items.size() != 2)
		{
			return false; 
		}

		boolean augmentPresent = false;
		boolean augmentablePresent = false;
		for (ItemStack i : items)
		{
			if (AugmentationManager.isAugmentCrafterItem(i))
			{
				augmentPresent = true;
			}

			if (AUGMENTABLE_MATERIALS.contains(i.getType()))
			{
				augmentablePresent = true;
			}
		}

		if (!augmentablePresent || !augmentPresent)
		{
			return false;
		}

		ItemStack augmentItem = items.get(0);
		ItemStack target = cloneItem(items.get(1));
		if (!AugmentationManager.isAugmentCrafterItem(augmentItem))
		{
			augmentItem = items.get(1);
			target = cloneItem(items.get(0));
		}

		for (Class<? extends Event> event : AugmentationManager.extractEventTypes(augmentItem))
		{
			for (Augmentation<?> augment : Augmentation.extract(augmentItem, event))
			{
				augment.apply(target);
			}
		}

		inventory.setResult(target);

		return true;
	}

	private static ItemStack cloneItem(
		ItemStack item
	) {
		ItemStack clone = new ItemStack(item.getType());
		// Enchantments
		clone.addUnsafeEnchantments(item.getEnchantments());
		
		// Meta
		ItemMeta cloneMeta = clone.getItemMeta(); 	
		ItemMeta itemMeta = item.getItemMeta();

		((Damageable) cloneMeta).setDamage(((Damageable) itemMeta).getDamage());
		cloneMeta.setLore(new ArrayList<>(itemMeta.getLore()));
		cloneMeta.setDisplayName(itemMeta.getDisplayName());
		itemMeta.getPersistentDataContainer().copyTo(cloneMeta.getPersistentDataContainer(), true);

		clone.setItemMeta(cloneMeta);

		return clone;
	}
}
