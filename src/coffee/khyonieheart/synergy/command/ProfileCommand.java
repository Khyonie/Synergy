package coffee.khyonieheart.synergy.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import coffee.khyonieheart.hyacinth.Gradient;
import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.synergy.Filter;
import coffee.khyonieheart.synergy.Synergy;
import coffee.khyonieheart.synergy.profile.PlayerProfile;
import coffee.khyonieheart.synergy.profile.Pronouns;
import coffee.khyonieheart.synergy.tablist.TabListManager;
import coffee.khyonieheart.tidal.ArgCount;
import coffee.khyonieheart.tidal.TidalCommand;
import coffee.khyonieheart.tidal.structure.Protected;
import coffee.khyonieheart.tidal.structure.Root;
import coffee.khyonieheart.tidal.structure.Static;

public class ProfileCommand extends TidalCommand
{
	public ProfileCommand() 
	{
		super("profile", "Synergy profile command.", "/profile", null);
	}

	private static Matcher specialCharacterMatcher = Pattern.compile("\\W+").matcher(""); 

	@Root("nickname")
	public void nicknameSet(
		Player player,
		@Static String set,
		String name
	) {
		name = name.replace("  ", "");
		if (name.length() > 16 || name.length() < 3)
		{
			Message.send(player, "§cNicknames must be between 3 and 16 characters long.");
			return;
		}

		if (Filter.contains(name))
		{
			Message.send(player, "§cNickname contains profane language. Contact Khyonie if you believe this is a mistake.");
			return;
		}

		specialCharacterMatcher.reset(name);
		if (specialCharacterMatcher.find(0))
		{
			Message.send(player, "§cNickname contains special characters other than underscore.");
			return;
		}

		for (OfflinePlayer p : Bukkit.getOfflinePlayers())
		{
			if (p.getName().equals(name))
			{
				Message.send(player, "§cNickname cannot be the same as another player's name.");
				return;
			}
		}

		Synergy.getProfileManager().getProfile(player).setNickName(name);
		player.spigot().sendMessage(new Gradient("#55FF55", "#FFFFFF").createComponents("Your nickname has been updated!"));
		TabListManager.update(player);
	}

	@Root("nickname")
	public void nicknameClear(
		Player player,
		@Static String clear
	) {
		if (!Synergy.getProfileManager().getProfile(player).hasNickName())
		{
			player.spigot().sendMessage(new Gradient("#AAAAAA", "#FFFFFF").createComponents("You don't have a nickname."));
			return;
		}
		Synergy.getProfileManager().getProfile(player).removeNickname();
		player.spigot().sendMessage(new Gradient("#55FF55", "#FFFFFF").createComponents("Your nickname has been cleared."));
		TabListManager.update(player);
	}

	@Root("nickname")
	public void nicknameClearOther(
		Player player,
		@Static String clear,
		@Protected(permission = "synergy.administrator") @ArgCount(min = 1) OfflinePlayer... targets
	) {
		for (OfflinePlayer target : targets)
		{
			PlayerProfile profile = Synergy.getProfileManager().getProfile(target);
			boolean loaded = false;
			if (profile == null)
			{
				Synergy.getProfileManager().loadProfile(target);
				profile = Synergy.getProfileManager().getProfile(target);

				if (profile == null)
				{
					Message.send(player, "§cNo player by that name has joined.");
					Synergy.getProfileManager().unloadProfile(target);
					continue;
				}

				loaded = true;
			}

			profile.removeNickname();

			if (loaded)
			{
				Synergy.getProfileManager().unloadProfile(target);
				continue;
			}

			TabListManager.update(target.getPlayer());
		}
		Message.send(player, "§aOperation complete.");
	}

	@Root
	public void pronouns(
		Player player,
		Pronouns pronouns
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);

		if (profile.getPronouns() == pronouns)
		{
			player.spigot().sendMessage(new Gradient("#AAAAAA", "#FFFFFF").createComponents("Pronouns already set. No change has been made."));
			return;
		}

		profile.setPronouns(pronouns);
		player.spigot().sendMessage(new Gradient("#55FF55", "#FFFFFF").createComponents("Your pronouns have been updated!"));
		TabListManager.update(player);
	}

	@Override
	public HyacinthModule getModule() 
	{
		return Synergy.getInstance();
	}
}
