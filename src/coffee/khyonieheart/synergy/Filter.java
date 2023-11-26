package coffee.khyonieheart.synergy;

import java.util.Set;

public class Filter
{
	// May some higher power forgive me for this
	private static final Set<String> filterWords = Set.of(
		"fag",
		"nigger",
		"tranny",
		"whore"
	);

	public static boolean contains(
		String input
	) {
		String normalized = normalize(input);
		for (String s : filterWords)
		{
			if (normalized.contains(s))
			{
				return true;
			}
		}

		return false;
	}

	public static String normalize(
		String input
	) {
		return input.replace('1', 'L')
			.replace('2', 'Z')
			.replace('3', 'E')
			.replace('4', 'A')
			.replace('5', 'S')
			.replace('6', 'G')
			.replace('7', 'T')
			.replace('8', 'B')
			.replace("ph", "F")
			.replace('0', 'O')
			.replace('@', 'a')
			.replace('!', 'I')
			.replace('_', ' ')
			.replace('-', ' ')
			.replace(" ", "")
			.toLowerCase();
	}
}
