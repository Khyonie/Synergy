package coffee.khyonieheart.synergy.parsers;

import org.bukkit.generator.structure.StructureType;

import coffee.khyonieheart.synergy.api.DeriveParser;

@DeriveParser
public enum StructureScanType
{
	ENDCITY(StructureType.END_CITY, "End_City"),
	FORTRESS(StructureType.FORTRESS, "Fortress"),
	MONUMENT(StructureType.OCEAN_MONUMENT, "Ocean_Monument"),
	STRONGHOLD(StructureType.STRONGHOLD, "Stronghold"),
	MANSION(StructureType.WOODLAND_MANSION, "Woodland_Mansion")
	;

	private StructureType wrappedType;
	private String canonicalName;

	private StructureScanType(
		StructureType wrappedType,
		String canonicalName
	) {
		this.wrappedType = wrappedType;
		this.canonicalName = canonicalName;
	}

	public StructureType getType()
	{
		return wrappedType;
	}

	public String getCanonicalName()
	{
		return canonicalName;
	}
}
