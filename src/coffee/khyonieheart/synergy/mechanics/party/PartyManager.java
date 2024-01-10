package coffee.khyonieheart.synergy.mechanics.party;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;
import coffee.khyonieheart.synergy.Synergy;

public class PartyManager
{
	private List<Party> activeParties = new ArrayList<>();
	private Map<Player, Party> partyLookups = new HashMap<>();
	private boolean[] takenColors = new boolean[PartyColor.values().length];

	public void createParty(
		Player leader
	) {
		Logger.debug("Created new party with " + leader.getName() + " as leader");
		Party party = new Party(leader);

		activeParties.add(party);
		partyLookups.put(leader, party);
		Synergy.getTabListManager().update(leader);
	}

	public void deleteParty(
		Party party
	) {
		Logger.debug("Deleting party " + activeParties.indexOf(party));
		for (Player member : party.getMembers())
		{
			partyLookups.remove(member);
		}
		party.clearParty();
		activeParties.remove(party);
	}

	void attachToParty(
		Player player, 
		Party party
	) {
		Logger.debug("Attached " + player.getDisplayName() + " party " + activeParties.indexOf(party));
		partyLookups.put(player, party);
	}

	void deregisterFromParty(
		Player player
	) {
		Logger.debug("Deregistered " + player.getDisplayName() + " from party " + activeParties.indexOf(partyLookups.get(player)));
		partyLookups.remove(player);
	}

	@Nullable
	public Party getParty(
		Player player
	) {
		return partyLookups.get(player);
	}

	public boolean isInParty(
		Player player
	) {
		return partyLookups.containsKey(player);
	}

	public int getPartyIndex(
		Player player
	) {
		if (!partyLookups.containsKey(player))
		{
			return Integer.MIN_VALUE;
		}

		Logger.debug("Party index for player " + player.getDisplayName() + ": " + activeParties.indexOf(partyLookups.get(player)));
		return activeParties.indexOf(partyLookups.get(player));
	}

	public boolean isColorTaken(
		PartyColor color
	) {
		return takenColors[color.ordinal()];
	}

	public PartyColor getFirstInactivePartyColor()
	{
		for (PartyColor color : PartyColor.values())
		{
			if (!isColorTaken(color))
			{
				return color;
			}
		}

		return PartyColor.values()[this.activeParties.size() % PartyColor.values().length];
	}
}
