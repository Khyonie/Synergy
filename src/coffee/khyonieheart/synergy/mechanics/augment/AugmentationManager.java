package coffee.khyonieheart.synergy.mechanics.augment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class AugmentationManager
{
	private static Map<Class<? extends Event>, Map<String, Class<? extends Augmentation<?>>>> registeredAugments = new HashMap<>();
	private static Map<String, Class<? extends Event>> knownEvents = new HashMap<>();
	private static Map<Class<? extends Event>, NamespacedKey> generatedKeys = new HashMap<>();
	public static NamespacedKey AUGMENT_COUNT_KEY = new NamespacedKey("synergy", "augmentcount");
	public static NamespacedKey AUGMENT_TYPE_KEY = new NamespacedKey("synergy", "presenttypes");
	public static NamespacedKey AUGMENT_CRAFTER_ID = new NamespacedKey("synergy", "augmentcraftable");

	@SuppressWarnings("unchecked")
	public static void registerAugmentation(
		Augmentation<?> augment
	) {
		if (!registeredAugments.containsKey(augment.getEventType()))
		{
			registeredAugments.put(augment.getEventType(), new HashMap<>());
			generatedKeys.put(augment.getEventType(), new NamespacedKey("synergy", "aug_" + augment.getEventType().getSimpleName().toLowerCase()));
			knownEvents.put(augment.getEventType().getSimpleName().toLowerCase(), augment.getEventType());
		}

		registeredAugments.get(augment.getEventType()).put(augment.getIdentity(), (Class<? extends Augmentation<?>>) augment.getClass());
	}

	public static Class<? extends Augmentation<?>> getAugmentation(
		String id,
		Class<? extends Event> type
	) {
		return registeredAugments.get(type).get(id);
	}

	public static NamespacedKey getKey(
		Class<? extends Event> type
	) {
		return generatedKeys.get(type);
	}

	public static void setAsAugmentationCore(
		ItemStack item
	) {
		item.getItemMeta().getPersistentDataContainer().set(AUGMENT_CRAFTER_ID, PersistentDataType.STRING, "");
	}

	public static boolean isAugmentCrafterItem(
		ItemStack item
	) {
		if (item == null)
		{
			return false;
		}

		return item.getItemMeta().getPersistentDataContainer().has(AUGMENT_CRAFTER_ID, PersistentDataType.STRING);
	}

	public static List<Class<? extends Event>> extractEventTypes(
		ItemStack item
	) {
		if (!item.getItemMeta().getPersistentDataContainer().has(AUGMENT_TYPE_KEY, PersistentDataType.STRING))
		{
			item.getItemMeta().getPersistentDataContainer().set(AUGMENT_TYPE_KEY, PersistentDataType.STRING, "");
			return new ArrayList<>();
		}

		String eventsRaw = item.getItemMeta().getPersistentDataContainer().get(AUGMENT_TYPE_KEY, PersistentDataType.STRING);
		if (eventsRaw.length() == 0)
		{
			return new ArrayList<>();
		}

		List<Class<? extends Event>> present = new ArrayList<>();
		for (String s : eventsRaw.split("\u0000"))
		{
			present.add(knownEvents.get(s));
		}

		return present;
	}
}
