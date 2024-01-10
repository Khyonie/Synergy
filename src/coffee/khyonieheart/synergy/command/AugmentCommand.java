package coffee.khyonieheart.synergy.command;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.synergy.Synergy;
import coffee.khyonieheart.synergy.items.Wrapping;
import coffee.khyonieheart.synergy.mechanics.augment.testing.TestAugment;
import coffee.khyonieheart.tidal.TidalCommand;
import coffee.khyonieheart.tidal.structure.Root;

public class AugmentCommand extends TidalCommand
{
	private final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
	public AugmentCommand()
	{
		super("augmentation", "Debug augment command", "/augmentation", "synergy.admin", "augment", "aug");
	}

	@Root
	public void apply(
		Player player
	) {
		ItemStack held = player.getInventory().getItemInMainHand();
		if (held == null)
		{
			return;
		}

		new TestAugment().apply(held);
	}

	@Root
	public void chars(
		Player player,
		float length
	) {
		for (String s : Wrapping.wrap(LOREM_IPSUM, length))
		{
			Message.send(player, s);
		}
	}

	@Override
	public HyacinthModule getModule() 
	{
		return Synergy.getInstance();
	}
}
