package coffee.khyonieheart.synergy.mechanics.party;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;

import coffee.khyonieheart.hyacinth.Gradient;
import coffee.khyonieheart.hyacinth.Gradient.GradientGroup;
import coffee.khyonieheart.synergy.Synergy;
import coffee.khyonieheart.synergy.profile.PlayerProfile;

public class Party
{
	private Player leader;
	private List<Player> members = new ArrayList<>();
	private boolean allowPVP = false;
	private PartyColor color;

	public Party(
		Player leader
	) {
		this.leader = leader;
		members.add(leader);
		this.color = Synergy.getProfileManager().getProfile(leader).getPartyColor();

		if (Synergy.getPartyManager().isColorTaken(color))
		{
			leader.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Your preferred party color is unavailable, using next available color.", "#AAAAAA", "#FFFFFF")));
			this.color = Synergy.getPartyManager().getFirstInactivePartyColor();
		}
	}

	public void joinParty(
		Player player
	) {
		members.add(player);
		Synergy.getPartyManager().attachToParty(player, this);
		recalculateBonuses();
		Synergy.getTabListManager().update(player);
	}

	public void promote(
		Player player
	) {
		this.leader = player;
		recalculateBonuses();

		leader.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You have been promoted to party leader.", "#55FF55", "#FFFFFF")));
	}

	public boolean allowsPVP()
	{
		return this.allowPVP;
	}

	public void setAllowPVP(
		boolean setting
	) {
		this.allowPVP = setting;
	}

	public void leaveParty(
		Player player
	) {
		members.remove(player);
		Synergy.getPartyManager().deregisterFromParty(player);
		Synergy.getTabListManager().update(player);
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You have left your current party.", "#55FF55", "#FFFFFF")));

		removeAttributes(player);
		recalculateBonuses();

		if (members.size() == 0)
		{
			Synergy.getPartyManager().deleteParty(this);
			this.leader = null;
			return;
		}

		if (leader.equals(player))
		{
			this.leader = members.get(0);
			members.get(0).spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You have been promoted to party leader.", "#55FF55", "#FFFFFF")));
		}

		recalculateBonuses();

		Synergy.getTabListManager().update(player);

		if (members.size() == 1)
		{
			clearParty();
			Synergy.getPartyManager().deleteParty(this);
		}
	}

	public List<Player> getMembers()
	{
		return members;
	}

	public PartyColor getColor()
	{
		return this.color;
	}

	public boolean isLeader(
		Player player
	) {
		return this.leader.equals(player);
	}

	public void clearParty()
	{
		for (Player p : members)
		{
			members.get(0).spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Party has been disbanded.", "#55FF55", "#FFFFFF")));
			removeAttributes(p);
			Synergy.getPartyManager().deregisterFromParty(p);
		}

		List<Player> copy = new ArrayList<>(members);
		members.clear();
		for (Player p : copy)
		{
			Synergy.getTabListManager().update(p);
		}
	}

	public void recalculateBonuses()
	{
		for (Player p : members)
		{
			removeAttributes(p);
		}

		if (members.size() == 1)
		{
			return;
		}

		boolean[] activeBonuses = new boolean[PartyBonus.values().length];
		for (Player p : members)
		{
			PlayerProfile profile = Synergy.getProfileManager().getProfile(p);
			PartyBonus bonus = profile.getPartyBonus();

			if (activeBonuses[bonus.ordinal()])
			{
				continue;
			}

			for (Player target : members) 
			{
				target.getAttribute(bonus.getType()).addModifier(new AttributeModifier("partyBonus" + bonus.name(), leader.equals(p) ? bonus.getLeaderValue() : bonus.getMemberValue(), Operation.MULTIPLY_SCALAR_1));
			}
			activeBonuses[bonus.ordinal()] = true;
		}
	}

	private void removeAttributes(
		Player target
	) {
		for (PartyBonus bonus : PartyBonus.values())
		{
			AttributeInstance instance = target.getAttribute(bonus.getType());
			instance.getModifiers().stream()
				.filter(mod -> mod.getName().startsWith("partyBonus"))
				.toList()
				.forEach(mod -> instance.removeModifier(mod));
		}
	}
}
