package coffee.khyonieheart.synergy.mechanics.augment;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.hyacinth.killswitch.Feature;
import coffee.khyonieheart.hyacinth.killswitch.FeatureIdentifier;
import coffee.khyonieheart.synergy.api.gatedevents.GatedBranch;
import coffee.khyonieheart.synergy.api.gatedevents.GatedEventHandler;
import coffee.khyonieheart.synergy.api.gatedevents.GatedListener;

@FeatureIdentifier("augmentUse")
public class AugmentationUseListener implements GatedListener, Feature
{
	private static boolean isEnabled = false;

	@SuppressWarnings("unchecked")
	@GatedEventHandler(branch = GatedBranch.NIGHTLY)
	public void onBlockBreak(
		BlockBreakEvent event 
	) {
		ItemStack held = event.getPlayer().getInventory().getItemInMainHand();

		if (held == null)
		{
			return;
		}

		if (held.getType() == Material.AIR)
		{
			return;
		}

		List<?> augments = Augmentation.extract(held, event.getClass());
		augments.forEach(a -> ((Augmentation<BlockBreakEvent>) a).run(event));
	}

	@Override
	public boolean isEnabled(
		String id
	) {
		return switch (id) {
			case "augmentUse" -> isEnabled;
			default -> false;
		};
	}

	@Override
	public boolean kill(
		String id
	) {
		return switch (id) {
			case "augmentUse" -> {
				if (isEnabled)
				{
					yield !(isEnabled = false);
				}

				yield false;
			}
			default -> false;
		};
	}

	@Override
	public boolean reenable(
		String id
	) {
		return switch (id) {
			case "augmentUse" -> {
				if (isEnabled)
				{
					yield isEnabled = true;
				}

				yield false;
			}
			default -> false;
		};
	}
}
