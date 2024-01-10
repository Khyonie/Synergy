package coffee.khyonieheart.synergy.mechanics.party;

import org.bukkit.attribute.Attribute;

import coffee.khyonieheart.synergy.api.DeriveParser;

@DeriveParser
public enum PartyBonus
{
	ATTACK_DAMAGE(Attribute.GENERIC_ATTACK_DAMAGE, 0.2, 0.3),
	DEFENSE(Attribute.GENERIC_ARMOR, 0.2, 0.3),
	MOVE_SPEED(Attribute.GENERIC_MOVEMENT_SPEED, 0.1, 0.2)
	;

	private Attribute attributeType;
	private double memberValue;
	private double leaderValue;

	private PartyBonus(
		Attribute attributeType,
		double memberValue,
		double leaderValue
	) {
		this.attributeType = attributeType;
		this.memberValue = memberValue;
		this.leaderValue = leaderValue;
	}

	public Attribute getType()
	{
		return this.attributeType;
	}

	public double getMemberValue()
	{
		return this.memberValue;
	}

	public double getLeaderValue()
	{
		return this.leaderValue;
	}
}
