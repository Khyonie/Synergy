package coffee.khyonieheart.synergy.tablist;

import org.bukkit.entity.Player;

import coffee.khyonieheart.synergy.Synergy;
import coffee.khyonieheart.synergy.profile.PlayerProfile;
import coffee.khyonieheart.synergy.profile.Pronouns;

public class TabListManager
{
	public static void update(
		Player player
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);
		Pronouns pronouns = profile.getPronouns();
		player.setPlayerListName((profile.hasNickName() ? profile.getNickName() : player.getDisplayName()) + (pronouns == Pronouns.ASK_ME ? "" : " §7(" + pronouns.getSingular() + "/" + pronouns.getPosessive() + "/" + pronouns.getPluralPossesive() + ")") + "§r");
	}
}
