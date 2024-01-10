package coffee.khyonieheart.synergy.mechanics.party;

import coffee.khyonieheart.synergy.api.DeriveParser;

@DeriveParser
public enum PartyColor
{
	AQUA('b'),
	TEAL('a'),
	YELLOW('e'),
	RED('c'),
	PINK('d'),
	BLUE('2'),
	GREEN('1'),
	ORANGE('6'),
	CRIMSON('4'),
	PURPLE('5'),
	;

	private final char color;

	private PartyColor(
		char colorCode
	) {
		this.color = colorCode;
	}

	public String getColor()
	{
		return "§" + color;
	}
}
