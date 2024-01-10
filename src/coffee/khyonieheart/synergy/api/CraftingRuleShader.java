package coffee.khyonieheart.synergy.api;

import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.ClassShader;
import coffee.khyonieheart.hyacinth.util.Reflect;
import coffee.khyonieheart.synergy.crafting.RecipeManager;
import coffee.khyonieheart.synergy.crafting.rules.CraftingRule;

public class CraftingRuleShader implements ClassShader<CraftingRule>
{
	@Override
	public Class<CraftingRule> getType() 
	{
		return CraftingRule.class;
	}

	@Override
	public CraftingRule process(
		Class<? extends CraftingRule> type,
		CraftingRule instance
	) {
		if (instance == null)
		{
			CraftingRule rule = Reflect.simpleInstantiate(type);
			RecipeManager.registerRule(rule);
			return rule;
		}

		RecipeManager.registerRule(instance);
		return null;
	}
}
