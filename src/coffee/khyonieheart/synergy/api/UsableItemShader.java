package coffee.khyonieheart.synergy.api;

import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.ClassShader;
import coffee.khyonieheart.hyacinth.util.Reflect;
import coffee.khyonieheart.synergy.items.UsableItem;
import coffee.khyonieheart.synergy.items.UsableItemManager;

public class UsableItemShader implements ClassShader<UsableItem>
{
	@Override
	public Class<UsableItem> getType() 
	{
		return UsableItem.class;
	}

	@Override
	public UsableItem process(
		Class<? extends UsableItem> type,
		UsableItem instance
	) {
		if (instance != null)
		{
			UsableItemManager.register(type, instance);
			return null;
		}

		UsableItem item = Reflect.simpleInstantiate(type);
		UsableItemManager.register(type, item);

		return item;
	}
}
