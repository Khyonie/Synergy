package coffee.khyonieheart.synergy.tablist;

import org.bukkit.entity.Player;

import coffee.khyonieheart.synergy.Synergy;
import coffee.khyonieheart.synergy.profile.PlayerProfile;
import coffee.khyonieheart.synergy.profile.Pronouns;

public class TabListManager
{
	private static final char[] partyColors = new char[] {
		'b', 'a', 'e', 'c', 'd', '2', '1', '6', '4', '5'
	};

	public void update(
		Player player
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);
		Pronouns pronouns = profile.getPronouns();

		char color = 'f';
		if (Synergy.getPartyManager().isInParty(player))
		{
			color = partyColors[Synergy.getPartyManager().getPartyIndex(player) % partyColors.length];
		}

		player.setPlayerListName("§" + color + Synergy.getName(player) + (pronouns == Pronouns.ASK_ME ? "" : " §7(" + pronouns.getSingular() + "/" + pronouns.getPosessive() + "/" + pronouns.getPluralPossesive() + ")") + "§r");
	}
}
