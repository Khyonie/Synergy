package coffee.khyonieheart.synergy.api;

@DeriveParser
public enum BooleanSetting
{
	ALLOW(true),
	DENY(false)
	;

	private boolean bool;

	private BooleanSetting(
		boolean bool
	) {
		this.bool = bool;
	}

	public boolean getValue()
	{
		return this.bool;
	}
}
