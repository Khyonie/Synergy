package coffee.khyonieheart.synergy.profile;

public enum Pronouns
{
	MASCULINE("Him", "His", "His", "Himself"),
	FEMININE("She", "Her", "Hers", "Herself"),
	NEUTRAL_A("They", "Them", "Theirs", "Themself"),
	NEUTRAL_B("Ze", "Zir", "Zirs", "Zirself"),
	NEUTRAL_C("Sie", "Hir", "Hirs", "Hirself"),
	ASK_ME(null, null, null, null)
	;

	private final String singular;
	private final String posessive;
	private final String pluralPossesive;
	private final String reflexive;

	private Pronouns(
		String singular,
		String posessive,
		String pluralPossesive,
		String reflexive
	) {
		this.singular = singular;
		this.posessive = posessive;
		this.pluralPossesive = pluralPossesive;
		this.reflexive = reflexive;
	}

	public String getSingular() 
	{
		return singular;
	}

	public String getPosessive() 
	{
		return posessive;
	}

	public String getPluralPossesive() 
	{
		return pluralPossesive;
	}

	public String getReflexive() 
	{
		return reflexive;
	}
}
