package coffee.khyonieheart.synergy.parsers;

import java.util.List;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.hyacinth.util.Arrays;
import coffee.khyonieheart.synergy.profile.Pronouns;
import coffee.khyonieheart.tidal.TypeParser;
import coffee.khyonieheart.tidal.error.CommandError;
import coffee.khyonieheart.tidal.structure.branch.Branch;

public class PronounParser extends TypeParser<Pronouns>
{
	private static List<String> options;
	public PronounParser()
	{
		super(Pronouns.class);
		options = Arrays.toArrayList(Pronouns.values())
			.stream()
			.map(p -> p.name())
			.toList();
	}

	@Override
	public List<String> generateCompletions() 
	{
		return options;
	}

	@Override
	public Pronouns parseType(
		String input
	) {
		return Pronouns.valueOf(input.toUpperCase());
	}

	@Override
	public CommandError validateExecution(
		CommandSender sender,
		String label,
		int index,
		Branch branch,
		String argument,
		String[] args
	) {
		try {
			Pronouns.valueOf(argument.toUpperCase());

			return null;
		} catch (IllegalArgumentException e) {
			return new CommandError("Unknown pronoun set, if your preferred pronouns aren't represented, please contact Khyonie", argument, index);
		}
	}

	@Override
	public CommandError validateTabComplete(
		CommandSender sender,
		String label,
		int index,
		Branch branch,
		String argument,
		String[] args
	) {
		try {
			Pronouns.valueOf(argument.toUpperCase());

			return null;
		} catch (IllegalArgumentException e) {
			return new CommandError("Unknown pronoun set", argument, index);
		}
	}
}
