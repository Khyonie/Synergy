package coffee.khyonieheart.synergy.items;

import java.util.HashMap;
import java.util.Map;

public class UsableItemManager
{
	private static Map<String, UsableItem> registeredItems = new HashMap<>();
	
	public static void register(
		Class<?> type,
		UsableItem item
	) {
		registeredItems.put(type.getName(), item);
	}

	public static UsableItem getItem(
		String typePath
	) {
		return registeredItems.get(typePath);
	}
}
