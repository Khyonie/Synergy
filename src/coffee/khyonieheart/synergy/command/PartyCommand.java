package coffee.khyonieheart.synergy.command;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import coffee.khyonieheart.hyacinth.Gradient;
import coffee.khyonieheart.hyacinth.Gradient.GradientGroup;
import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.synergy.Synergy;
import coffee.khyonieheart.synergy.api.BooleanSetting;
import coffee.khyonieheart.synergy.mechanics.party.Party;
import coffee.khyonieheart.synergy.mechanics.party.PartyBonus;
import coffee.khyonieheart.synergy.mechanics.party.PartyColor;
import coffee.khyonieheart.synergy.mechanics.party.PartyInvite;
import coffee.khyonieheart.synergy.profile.PlayerProfile;
import coffee.khyonieheart.tidal.TidalCommand;
import coffee.khyonieheart.tidal.structure.Root;

public class PartyCommand extends TidalCommand
{
	private static Map<Player, Map<Player, PartyInvite>> outstandingInvites = new HashMap<>();
	private static Map<Player, Map<Player, BukkitTask>> timeoutTasks = new HashMap<>();
	private static Map<Player, PartyInvite> mostRecentInvites = new HashMap<>();

	public PartyCommand() 
	{
		super("party", "Synergy party command.", "/party", null, "p");
	}

	@Root(isRootExecutor = true)
	public void party(
		Player player
	) {
		if (Synergy.getPartyManager().isInParty(player))
		{
			Message.send(player, "§e-< §6Current Party (ID " + Synergy.getPartyManager().getPartyIndex(player) + ") §e>-");
			Party party = Synergy.getPartyManager().getParty(player);
			boolean[] coveredBonuses = new boolean[PartyBonus.values().length];

			for (Player member : party.getMembers())
			{
				PlayerProfile profile = Synergy.getProfileManager().getProfile(member);
				char covered = coveredBonuses[profile.getPartyBonus().ordinal()] ? '7' : 'a';
				Location location = member.getLocation();
				String worldtype = switch (location.getWorld().getEnvironment())
				{
					case NORMAL -> "the overworld";
					case NETHER -> "the nether";
					case THE_END -> "the end";
					default -> "the unknown";
				};

				Message.send(player, (party.isLeader(member) ? "§e★§b " : "§b") + Synergy.getName(member) + " §f(§" + covered + profile.getPartyBonus().name().toLowerCase().replace('_', ' ') + "§f) §bat (§9" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + " in " + worldtype + "§b)");
				coveredBonuses[profile.getPartyBonus().ordinal()] = true;
			}

			for (Player p : outstandingInvites.keySet())
			{
				for (Player i : outstandingInvites.get(p).keySet())
				{
					if (outstandingInvites.get(p).get(i).party().equals(party))
					{
						Message.send(player, "§7" + Synergy.getName(p) + " (§8Invited§7)");
					}
				}
			}

			return;
		}

		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You are not in a party.", "#AAAAAA", "#FFFFFF")));
	}

	@Root("invite")
	public void partyInvite(
		Player player,
		Player target
	) {
		if (player.equals(target))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You cannot issue an invitation to yourself.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		if (outstandingInvites.containsKey(target))
		{
			if (outstandingInvites.get(target).containsKey(player))
			{
				player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You have already issued an invitation to that player..", "#AAAAAA", "#FFFFFF")));
				return;
			}
		}

		Party party;
		if (Synergy.getPartyManager().isInParty(player))
		{
			party = Synergy.getPartyManager().getParty(player);
		} else {
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Created a new party.", "#AAAAAA", "#FFFFFF")));
			Synergy.getPartyManager().createParty(player);
			party = Synergy.getPartyManager().getParty(player);
		}

		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Invited " + target.getDisplayName() + " to your party!", "#55FF55", "#FFFFFF")));
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);

		PartyInvite invite = new PartyInvite(player, party);

		if (!outstandingInvites.containsKey(target))
		{
			outstandingInvites.put(target, new HashMap<>());
		}

		if (!timeoutTasks.containsKey(target))
		{
			timeoutTasks.put(target, new HashMap<>());
		}

		outstandingInvites.get(target).put(player, invite);
		timeoutTasks.get(target).put(player, Hyacinth.getScheduler().runTaskLater(Hyacinth.getInstance(), () -> { 
			outstandingInvites.get(target).remove(player);
			mostRecentInvites.remove(target);
			timeoutTasks.get(target).remove(player);
		}, 20 * 120));

		mostRecentInvites.put(target, invite);
		
		if (Synergy.getPartyManager().isInParty(target))
		{
			target.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup(Synergy.getName(player) + " has invited you to " + (profile.getPronouns().getPosessive() == null ? "a" : profile.getPronouns().getPosessive().toLowerCase()) + " party. Accept with /party accept " + player.getDisplayName() + " (you will leave your current party), or decline with /party decline " + player.getDisplayName() + ". This invitation will time out in 120 seconds.", "#55FF55", "#FFFFFF")));
			return;
		}
		target.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup(Synergy.getName(player) + " has invited you to " + (profile.getPronouns().getPosessive() == null ? "a" : profile.getPronouns().getPosessive().toLowerCase()) + " party. Accept with /party accept " + player.getDisplayName() + ", or decline with /party decline " + player.getDisplayName() + ". This invitation will time out in 120 seconds.", "#55FF55", "#FFFFFF")));
	}

	@Root
	public void accept(
		Player player
	) {
		if (!mostRecentInvites.containsKey(player))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You do not have any party invitations.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		if (Synergy.getPartyManager().isInParty(player))
		{
			Synergy.getPartyManager().getParty(player).leaveParty(player);
		}

		PartyInvite invite = mostRecentInvites.remove(player);
		timeoutTasks.get(player).remove(invite.inviter()).cancel();
		outstandingInvites.get(player).remove(invite.inviter());

		Party party = invite.party();
		party.joinParty(player);
		for (Player member : party.getMembers())
		{
			member.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup(Synergy.getName(player) + " joined the party!", "#55FF55", "#FFFFFF")));
		}
	}

	@Root
	public void accept(
		Player player,
		Player inviter
	) {
		if (!outstandingInvites.containsKey(player))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You do not have any party invitations.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		if (!outstandingInvites.get(player).containsKey(inviter))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You do not have any invitations from that player.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		if (Synergy.getPartyManager().isInParty(player))
		{
			Synergy.getPartyManager().getParty(player).leaveParty(player);
		}

		timeoutTasks.get(player).remove(inviter).cancel();
		Party party = outstandingInvites.remove(player).get(inviter).party();
		mostRecentInvites.remove(player);

		party.joinParty(player);
		for (Player member : party.getMembers())
		{
			member.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup(Synergy.getName(player) + " joined the party!", "#55FF55", "#FFFFFF")));
		}
	}

	@Root
	public void decline(
		Player player,
		Player inviter
	) {
		if (!outstandingInvites.containsKey(player))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You do not have any party invitations.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		if (!outstandingInvites.get(player).containsKey(inviter))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You do not have any invitations from that player.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		timeoutTasks.get(player).remove(inviter).cancel();
		outstandingInvites.get(player).remove(inviter);
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Invitation declined.", "#55FF55", "#FFFFFF")));
	}

	@Root
	public void decline(
		Player player
	) {
		if (!mostRecentInvites.containsKey(player))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You do not have any party invitations.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		PartyInvite invite = mostRecentInvites.remove(player);
		timeoutTasks.get(player).remove(invite.inviter()).cancel();
		outstandingInvites.get(player).remove(invite.inviter());
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Invitation declined.", "#55FF55", "#FFFFFF")));
	}

	@Root
	public void leave(
		Player player
	) {
		if (!Synergy.getPartyManager().isInParty(player))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You are not in a party.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		Synergy.getPartyManager().getParty(player).leaveParty(player);
		//player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You have left the party.", "#55FF55", "#FFFFFF")));
	}

	@Root
	public void promote(
		Player player,
		Player target
	) {
		if (!Synergy.getPartyManager().isInParty(player))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You are not in a party.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		if (!Synergy.getPartyManager().isInParty(target))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("That player is not in your party.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		Party party = Synergy.getPartyManager().getParty(player);

		if (!Synergy.getPartyManager().getParty(target).equals(party))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("That player is not in your party.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		if (!party.isLeader(player))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You are not the party leader.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		party.promote(target);
		target.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You have been promoted to party leader.", "#55FF55", "#FFFFFF")));
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You have relinquished party leader.", "#55FF55", "#FFFFFF")));
	}

	@Root
	public void color(
		Player player,
		PartyColor color
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);

		if (profile.getPartyColor() == color)
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Nothing changed. Your preferred color is already " + color.name().toLowerCase() + ".", "#AAAAAA", "#FFFFFF")));
			return;
		}

		profile.setPartyColor(color);
		if (Synergy.getPartyManager().isInParty(player))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Updated party color setting. You must disband your current party for this change to take effect.", "#55FF55", "#FFFFFF")));
			return;
		}
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Updated party color setting.", "#55FF55", "#FFFFFF")));
	}

	@Root
	public void pvp(
		Player player,
		BooleanSetting setting
	) {
		if (!Synergy.getPartyManager().isInParty(player))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You are not in a party.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		Party party = Synergy.getPartyManager().getParty(player);

		if (!party.isLeader(player))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You are not the party leader.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		if (setting.getValue() == party.allowsPVP())
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Nothing changed. Party is already set to " + setting.name().toLowerCase() + "inter-party PVP.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		party.setAllowPVP(setting.getValue());
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Updated PVP setting.", "#55FF55", "#FFFFFF")));
	}

	@Root("bonus")
	public void partySetBonus(
		Player player,
		PartyBonus bonusType
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);

		if (profile.getPartyBonus() == bonusType)
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Nothing changed. You already have that bonus.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Updated bonus!", "#55FF55", "#FFFFFF")));
		profile.setPartyBonus(bonusType);

		if (Synergy.getPartyManager().isInParty(player))
		{
			Synergy.getPartyManager().getParty(player).recalculateBonuses();
		}
	}

	@Override
	public HyacinthModule getModule() 
	{
		return Synergy.getInstance();
	}
}
