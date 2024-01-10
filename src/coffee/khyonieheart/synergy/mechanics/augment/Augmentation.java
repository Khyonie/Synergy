package coffee.khyonieheart.synergy.mechanics.augment;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.module.marker.PreventAutoLoad;
import coffee.khyonieheart.hyacinth.util.Lists;
import coffee.khyonieheart.hyacinth.util.Reflect;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

@PreventAutoLoad
public interface Augmentation<T extends Event>
{
	@NotNull
	public String getIdentity();
	@NotNull 
	public String getDescription();
	@Nullable
	public String getExtra();
	public int getLevel();
	public int setLevel(
		int level
	);
	public void setExtra(
		String extra
	);
	@NotNull
	public Class<T> getEventType();

	public void run(
		@NotNull T event
	);

	public default void apply(
		ItemStack item
	) {
		List<Augmentation<T>> existingAugments = extract(item, this.getEventType());
		for (Augmentation<?> augment : existingAugments)
		{
			if (augment.equalsAugmentation(this))
			{
				return;
			}
		}

		existingAugments.add(this);

		String augmentString = Lists.toString(existingAugments, "\u0000", a -> a.asString());
		Logger.debug("Existing augments: " + augmentString);
		ItemMeta meta = item.getItemMeta();

		meta.getPersistentDataContainer().set(AugmentationManager.getKey(this.getEventType()), PersistentDataType.STRING, augmentString);
		
		// Increment existing augment count
		int count = meta.getPersistentDataContainer().has(AugmentationManager.AUGMENT_COUNT_KEY, PersistentDataType.INTEGER) ? meta.getPersistentDataContainer().get(AugmentationManager.AUGMENT_COUNT_KEY, PersistentDataType.INTEGER) : 0;
		count++;

		meta.getPersistentDataContainer().set(AugmentationManager.AUGMENT_COUNT_KEY, PersistentDataType.INTEGER, count);
		Logger.debug("Augment count: " + count);

		List<Class<? extends Event>> types = AugmentationManager.extractEventTypes(item);
		if (!types.contains(this.getEventType()))
		{
			Logger.debug("Adding non-present event type to item");
			types.add(this.getEventType());
		}
		meta.getPersistentDataContainer().set(AugmentationManager.AUGMENT_TYPE_KEY, PersistentDataType.STRING, Lists.toString(types, "\u0000", t -> t.getSimpleName().toLowerCase()));
		Logger.debug("Present types: " + Lists.toString(types, ", ", t -> t.getSimpleName()));
		
		// Now re-apply lore
		List<String> lore = new ArrayList<>();
		for (Class<? extends Event> type : types)
		{
			Logger.debug("Handling type " + type.getSimpleName());
			for (Augmentation<?> aug : extract(item, type))
			{
				lore.add("§7" + aug.getIdentity() + " " + aug.getLevel());
				lore.add("§8🞄 §o" + aug.getDescription());
			}
		}

		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	public default String asString()
	{
		return this.getIdentity() + ":" + this.getLevel() + ":" + this.getExtra();
	}

	@SuppressWarnings("unchecked")
	public static <T extends Event> List<Augmentation<T>> extract(
		ItemStack item,
		Class<T> type
	) {
		if (item == null)
		{
			return new ArrayList<>();
		}

		NamespacedKey key = AugmentationManager.getKey(type);
		if (key == null)
		{
			return new ArrayList<>();
		}

		PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
		if (!pdc.has(key, PersistentDataType.STRING))
		{
			return new ArrayList<>();
		}

		String[] augmentData = pdc.get(key, PersistentDataType.STRING).split("\u0000");
		List<Augmentation<T>> augments = new ArrayList<>(); 

		for (String augmentString : augmentData)
		{
			String[] data = augmentString.split(":");
			String id = data[0];
			int level = Integer.parseInt(data[1]);
			String extra = data[2].equals("null") ? null : data[2];

			Augmentation<?> aug = Reflect.simpleInstantiate(AugmentationManager.getAugmentation(id, type));
			aug.setLevel(level);
			if (extra != null)
			{
				aug.setExtra(extra);
			}

			augments.add((Augmentation<T>) aug);
		}

		return augments;
	}

	public default boolean equalsAugmentation(
		Object obj
	) {
		if (obj == null)
		{
			return false;
		}

		if (obj instanceof Augmentation<?> augmentation)
		{
			return this.getIdentity().equals(augmentation.getIdentity());
		}

		return false;
	}
}
