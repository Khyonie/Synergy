package coffee.khyonieheart.synergy.api;

import java.util.HashSet;
import java.util.Set;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.ClassShader;
import coffee.khyonieheart.synergy.parsers.EnumParser;
import coffee.khyonieheart.tidal.TypeManager;

// FIXME Integrate this into tidal instead
@SuppressWarnings({ "rawtypes" })
public class EnumClassShader implements ClassShader<Enum>
{
	private static Set<Class<? extends Enum>> processedEnums = new HashSet<>();

	@Override
	public Class<Enum> getType() 
	{
		return Enum.class;
	}

	@Override
	public Enum process(
		Class<? extends Enum> type,
		Enum enumInstance
	) {
		if (processedEnums.contains(type))
		{
			return null;
		}

		if (!type.isAnnotationPresent(DeriveParser.class))
		{
			return null;
		}

		Logger.verbose("Registering enum tidal type parser for " + type.getName());
		TypeManager.register(type, new EnumParser<>(type));
		processedEnums.add(type);

		return null;
	}
}
