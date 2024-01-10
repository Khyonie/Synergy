package coffee.khyonieheart.synergy.command;

import org.bukkit.entity.Player;

import coffee.khyonieheart.hyacinth.Gradient;
import coffee.khyonieheart.hyacinth.Gradient.GradientGroup;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.synergy.Synergy;
import coffee.khyonieheart.synergy.api.gatedevents.GatedBranch;
import coffee.khyonieheart.tidal.TidalCommand;
import coffee.khyonieheart.tidal.structure.Root;

public class AfkCommand extends TidalCommand
{
	public AfkCommand()
	{
		super("afk", "Toggles your AFK status.", "/afk", null);
	}

	@Root(isRootExecutor = true)
	public void toggleAfk(
		Player player
	) {
		if (Synergy.getProfileManager().getProfile(player).getBranch().ordinal() > GatedBranch.BETA.ordinal())
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("AFK toggle is a beta feature. Switch branches to the beta or nightly branch to use AFK toggles.", "#FFAA00", "#FFFFFF")));
			return;
		}

		if (Synergy.getAfkPlayers().contains(player))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You are no longer AFK.", "#FFAA00", "#FFFFFF")));
			Synergy.getAfkPlayers().remove(player);
			Synergy.getTabListManager().update(player);
			return;
		}

		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You are now AFK.", "#FFAA00", "#FFFFFF")));
		Synergy.getAfkPlayers().add(player);
		Synergy.getTabListManager().update(player);
	}

	@Override
	public HyacinthModule getModule() 
	{
		return Synergy.getInstance();
	}
}
