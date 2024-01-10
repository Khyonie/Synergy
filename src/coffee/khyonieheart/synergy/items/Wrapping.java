package coffee.khyonieheart.synergy.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wrapping
{
	private static final char[] LENGTH_ONE = {
		'!', '\'', ',', '.', ':', ';', '|',
		'i'
	};
	private static final char[] LENGTH_ONE_HALF = {
		'`', 'l'
	};
	private static final char[] LENGTH_TWO = {
		'"', '(', ')', '*', '[', ']', '{', '}', 
		'I',
		't'
	};
	private static final char[] LENGTH_TWO_HALF = {
		'<', '>', 
		'f', 'k'
	};
	private static final char[] LENGTH_THREE = {
		'#', '$', '%', '&', '+', '-', '/', '?', '\\', '^', '_',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'a', 'b', 'c', 'd', 'e', 'g', 'h', 'j', 'm', 'n', 'o', 'p', 'q', 'r', 's', 'u', 'v', 'w', 'x', 'y', 'z'
	};
	private static final char[] LENGTH_THREE_HALF = {
		'@', '~'
	};

	private static Map<Character, Float> lengths = new HashMap<>();
	static {
		for (char c : LENGTH_ONE)
		{
			lengths.put(c, 1.0f);
		}
		for (char c : LENGTH_ONE_HALF)
		{
			lengths.put(c, 1.5f);
		}
		for (char c : LENGTH_TWO)
		{
			lengths.put(c, 2.0f);
		}
		for (char c : LENGTH_TWO_HALF)
		{
			lengths.put(c, 2.5f);
		}
		for (char c : LENGTH_THREE)
		{
			lengths.put(c, 3.0f);
		}
		for (char c : LENGTH_THREE_HALF)
		{
			lengths.put(c, 3.5f);
		}
	}

	public static List<String> wrap(
		String input,
		float wrapLength
	) {
		List<String> wrappedText = new ArrayList<>();
		float current = 0;
		StringBuilder builder = new StringBuilder();
		for (String s : input.split(" "))
		{
			boolean newline = false;
			float delta = 0;
			for (char c : s.toCharArray())
			{
				if (c == '\n')
				{
					newline = true;
					continue;
				}
				if (!lengths.containsKey(c))
				{
					delta += 3.0;
					continue;
				}

				delta += lengths.get(c);
			}
			current += delta; // TODO Clamp when long words are encountered
			
			builder.append(s);
			if (current >= wrapLength || newline)
			{
				newline = false;
				wrappedText.add(builder.toString());
				builder = new StringBuilder();
				current = 0;
				continue;
			}
			builder.append(' ');
			current += 2;
		}

		if (!builder.isEmpty())
		{
			wrappedText.add(builder.toString());
		}

		return wrappedText;
	}
}
