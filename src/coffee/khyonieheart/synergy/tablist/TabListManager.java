package coffee.khyonieheart.synergy.tablist;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import coffee.khyonieheart.synergy.Synergy;
import coffee.khyonieheart.synergy.profile.PlayerProfile;
import coffee.khyonieheart.synergy.profile.Pronouns;

public class TabListManager
{
	public void update(
		Player player
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);
		Pronouns pronouns = profile.getPronouns();

		String color = "§f";
		if (Synergy.getPartyManager().isInParty(player))
		{
			color = Synergy.getPartyManager().getParty(player).getColor().getColor();
		}

		player.setPlayerListName(color + Synergy.getName(player) + (pronouns == Pronouns.ASK_ME ? "" : " §7(" + pronouns.getSingular() + "/" + pronouns.getPosessive() + "/" + pronouns.getPluralPossesive() + ")") + "§r");
	}

	public void updateTps()
	{
		float current = TpsMonitor.getPreviousSecond();
		float minute = TpsMonitor.getLastMinute();

		char currentColor = 'a';

		if (current < 17)
		{
			currentColor = 'e';
		}

		if (current < 12)
		{
			currentColor = 'c';
		}

		char averageColor = 'a';
	
		if (minute < 17)
		{
			averageColor = 'e';
		}

		if (minute < 15)
		{
			averageColor = 'c';
		}

		for (Player p : Bukkit.getOnlinePlayers())
		{
			p.setPlayerListFooter("§6TPS: §" + currentColor + current + "§6, avg: §" + averageColor + minute);;
		}
	}
}
