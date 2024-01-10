package coffee.khyonieheart.synergy.mechanics.augment.testing;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.synergy.mechanics.augment.Augmentation;

public class TestAugment implements Augmentation<BlockBreakEvent>
{
	private int level = 1;
	private String extra = null;

	@Override
	public String getIdentity() 
	{
		return "Petrify";
	}

	@Override
	public String getDescription() 
	{
		return "Turns everything to stone.";
	}

	@Override
	public String getExtra() 
	{
		return this.extra;
	}

	@Override
	public int getLevel() 
	{
		return this.level;
	}

	@Override
	public int setLevel(
		int level
	) {
		return this.level = level;
	}

	@Override
	public void setExtra(
		String extra
	) {
		this.extra = extra;
	}

	@Override
	public Class<BlockBreakEvent> getEventType() 
	{
		return BlockBreakEvent.class;
	}

	@Override
	public void run(
		BlockBreakEvent event
	) {
		event.setDropItems(false);
		event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.STONE));
	}
}
