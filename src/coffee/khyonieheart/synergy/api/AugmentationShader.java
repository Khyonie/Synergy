package coffee.khyonieheart.synergy.api;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.ClassShader;
import coffee.khyonieheart.hyacinth.util.Reflect;
import coffee.khyonieheart.synergy.mechanics.augment.Augmentation;
import coffee.khyonieheart.synergy.mechanics.augment.AugmentationManager;

@SuppressWarnings("rawtypes")
public class AugmentationShader implements ClassShader<Augmentation>
{
	@Override
	public Class<Augmentation> getType() 
	{
		return Augmentation.class;
	}

	@Override
	public Augmentation process(
		Class<? extends Augmentation> type,
		Augmentation instance
	) {
		Logger.verbose("Shading augmentation class " + type.getName());
		if (instance != null)
		{
			AugmentationManager.registerAugmentation(instance);
			return null;
		}

		Augmentation<?> augment = Reflect.simpleInstantiate(type);
		AugmentationManager.registerAugmentation(augment);
		return augment;
	}
}
