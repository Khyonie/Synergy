package coffee.khyonieheart.synergy.mechanics.augment.testing;

import org.bukkit.Tag;
import org.bukkit.event.block.BlockBreakEvent;

import coffee.khyonieheart.synergy.deprec.ConnectedBlocks;
import coffee.khyonieheart.synergy.deprec.TreecapitatorListener.ConnectedBlocksResult;
import coffee.khyonieheart.synergy.mechanics.augment.Augmentation;

public class TreecapitatorAugment implements Augmentation<BlockBreakEvent>
{
	private String extra;
	private int level = 1;

	@Override
	public String getIdentity() 
	{
		return "Treecapitator";
	}

	@Override
	public String getDescription() 
	{
		return "Cut down entire trees";
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
		if (!Tag.LOGS.getValues().contains(event.getBlock().getType()))
		{
			return;
		}

		ConnectedBlocksResult result = ConnectedBlocks.getConnectedBlocks(event.getBlock(), null, null);
	}
}
