package coffee.khyonieheart.synergy.command;

import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.synergy.Synergy;
import coffee.khyonieheart.tidal.TidalCommand;
import coffee.khyonieheart.tidal.structure.Root;

public class AscendCommand extends TidalCommand
{
	public AscendCommand() 
	{
		super("ascend", "Ascend command", "/ascend", null);
	}

	@Root(isRootExecutor = true)
	public void onExecute(
		Player player
	) {
		Location location = player.getLocation();

		if (location.getWorld().getEnvironment() != Environment.NORMAL)
		{
			Message.send(player, "§cYou must be in the overworld underground to ascend.");
			return;
		}

		if (location.getBlockY() > 32)
		{
			Message.send(player, "§cYou must be deep underground to ascend.");
			return;
		}

		location.setY(location.getWorld().getHighestBlockYAt(location));
		while (!location.getBlock().getType().isAir())
		{
			location = location.add(0, 1, 0);
		}
		player.teleport(location);
	}

	@Override
	public HyacinthModule getModule() {
		return Synergy.getInstance();
	}
}
