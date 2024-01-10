package coffee.khyonieheart.synergy.mechanics.party;

import org.bukkit.entity.Player;

public record PartyInvite(
	Player inviter,
	Party party
) { }
