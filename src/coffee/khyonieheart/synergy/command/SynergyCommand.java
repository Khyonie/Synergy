package coffee.khyonieheart.synergy.command;

import java.io.File;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import coffee.khyonieheart.hyacinth.Gradient;
import coffee.khyonieheart.hyacinth.Gradient.GradientGroup;
import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.marker.Range;
import coffee.khyonieheart.synergy.ChatChannel;
import coffee.khyonieheart.synergy.ProfileListener;
import coffee.khyonieheart.synergy.Synergy;
import coffee.khyonieheart.synergy.api.BooleanSetting;
import coffee.khyonieheart.synergy.profile.PlayerProfile;
import coffee.khyonieheart.tidal.ArgCount;
import coffee.khyonieheart.tidal.TidalCommand;
import coffee.khyonieheart.tidal.structure.Root;
import coffee.khyonieheart.tidal.structure.Static;

public class SynergyCommand extends TidalCommand
{
	public SynergyCommand() 
	{
		super("synergy", "Synergy for season VII.", "synergy", null, "syn", "s");
	}

	@Root(isRootExecutor = true)
	public void synergy(
		CommandSender sender
	) {
		Message.send(sender, "Welcome to Synergy, SLLPA's season VII custom module. As always, any bugs can be directed to khyonie on Discord.");
	}

	@Root
	public void guide(
		Player player
	) {
		player.getInventory().addItem(ProfileListener.createSynergyTutorialBook(player));
	}

	@Root
	public void djump(
		Player player,
		BooleanSetting setting
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);

		if (profile.getEnableDoubleJump() == setting.getValue())
		{
			player.spigot().sendMessage(new Gradient("#AAAAAA", "#FFFFFF").createComponents("No change has been made."));
			return;
		}

		profile.setEnableDoubleJump(setting.getValue());
		player.spigot().sendMessage(new Gradient("#55FF55", "#FFFFFF").createComponents("Double jump setting updated."));

		player.setAllowFlight(false);
		player.setFlying(false);
	}

	// Chat channel commands
	//--------------------------------------------------------------------------------
	
	@Root
	public void channel(
		Player player
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);

		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Current channel: " + profile.getChatChannel(), "#FFAA00", "#FFFFFF")));
	}

	@Root("channel")
	public void channelSwitch(
		Player player,
		ChatChannel channel
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);

		if (profile.getChatChannel() == channel)
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Current channel is already " + channel.name().toLowerCase() + ". No change has been made.", "#FFAA00", "#FFFFFF")));
			return;
		}

		if (channel == ChatChannel.PARTY)
		{
			if (!Synergy.getPartyManager().isInParty(player))
			{
				player.spigot().sendMessage(new Gradient("#AAAAAA", "#FFFFFF").createComponents("You must be in a party to use the party channel."));
				return;
			}
		}

		if (channel == ChatChannel.TOWN)
		{
			if (Synergy.getProfileManager().getProfile(player).getTown() == null)
			{
				player.spigot().sendMessage(new Gradient("#AAAAAA", "#FFFFFF").createComponents("You must belong to a town to use the town channel."));
			}
		}

		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Moved to " + channel.name().toLowerCase() + " channel.", "#FFAA00", "#FFFFFF")));
		profile.setChatChannel(channel);
	}

	// Mail commands
	//--------------------------------------------------------------------------------

	@Root("mail")
	public void mailBase(
		Player player
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);
		if (profile.hasMail())
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You have new mail. Use \"/synergy mail read\" to view it.", "#FFAA00", "#FFFFFF")));
			return;
		}
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You do not have any mail. To send mail, use \"/synergy mail send <recipient> '<message>'\".", "#FFAA00", "#FFFFFF")));
	}

	@Root("mail")
	public void mailRead(
		Player player,
		@Static String read
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);
		
		if (!profile.hasMail())
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You do not have any mail. To send mail, use \"/synergy mail send <recipient> '<message>'\".", "#FFAA00", "#FFFFFF")));
			return;
		}

		for (int i = 0; i < profile.getMail().size(); i++)
		{
			Message.send(player, "§e[" + (i + 1) + "/" + profile.getMail().size() + "]");
			Message.send(player, profile.getMail().get(i));
		}
		player.spigot().sendMessage(new Gradient("#AAAAAA", "#FFFFFF").createComponents("All done? Clear mail with /synergy mail clear.", 'o'));
	}

	@Root("mail")
	public void mailClear(
		Player player,
		@Static String clear
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);
		
		if (!profile.hasMail())
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You do not have any mail. To send mail, use \"/synergy mail send <recipient> '<message>'\".", "#FFAA00", "#FFFFFF")));
			return;
		}
		
		profile.clearMail();
		Message.send(player, "§6Mail cleared.");
	}

	@Root("mail")
	public void mailSend(
		Player player,
		@Static String send,
		@ArgCount(min = 1) OfflinePlayer[] recipients,
		String message
	) {
		message = message.replace('&', '§');

		for (OfflinePlayer recipient : recipients)
		{
			PlayerProfile profile = Synergy.getProfileManager().getProfile(recipient);
			boolean loaded = false;
			if (profile == null)
			{
				Synergy.getProfileManager().loadProfile(recipient);
				profile = Synergy.getProfileManager().getProfile(recipient);

				if (profile == null)
				{
					player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("No player by that name could be found. Check the spelling or try again later.", "#FFAA00", "#FFFFFF")));
					Synergy.getProfileManager().unloadProfile(recipient);
					continue;
				}

				loaded = true;
			}

			PlayerProfile sender = Synergy.getProfileManager().getProfile(player);
			profile.addMail(player.getDisplayName() + (sender.hasNickName() ? " (" + sender.getNickName() + ")" : ""), message);

			if (loaded)
			{
				player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Mail sent! " + recipient.getName() + " will see it the next time they log in.", "#FFAA00", "#FFFFFF")));
				Synergy.getProfileManager().unloadProfile(recipient);
				continue;
			}

			recipient.getPlayer().spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("New mail from " + player.getDisplayName() + (sender.hasNickName() ? " (" + sender.getNickName() + ")" : "") + ".", "#FFAA00", "#FFFFFF")));
		}
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Mail sent!", "#FFAA00", "#FFFFFF")));
	}

	// Admin commands
	//-------------------------------------------------------------------------------- 
	
	@Root(permission = "synergy.administrator")
	public void reset(
		CommandSender sender,
		OfflinePlayer target
	) {
		Synergy.getProfileManager().resetProfile(target);
		Message.send(sender, "§aOperation complete.");
	}

	@Root(permission = "synergy.administrator")
	public void delete(
		CommandSender sender,
		OfflinePlayer target
	) {
		if (target.isOnline())
		{
			Message.send(sender, "§bKicking online target, then deleting their profile...");
			target.getPlayer().kickPlayer("Player data is being reset.");
		}

		Synergy.getProfileManager().unloadProfile(target);

		File file = new File("SynergyData/profiles/" + target.getUniqueId().toString());

		if (!file.delete())
		{
			Message.send(sender, "§cOperation failure. The file could not be deleted.");
			return;
		}

		Message.send(sender, "§aOperation complete.");
	}

	// Bulletin commands
	//-------------------------------------------------------------------------------- 
	
	@Root
	public void bulletin(
		Player player
	) {
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("----------【 ", "#FFFFFF", "#55FF99"), new GradientGroup("Server Bulletin", "#D2AFFF", "#D2AFFF"), new GradientGroup(" 】----------", "#55FF99", "#FFFFFF")));
		for (String s : Synergy.getBulletin())
		{
			player.spigot().sendMessage(new Gradient("#005555", "#00FFFF").createComponents(s));
		}
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("--------------------", "#FFFFFF", "#55FF99"), new GradientGroup("--------------------", "#55FF99", "#FFFFFF")));
		
		// TODO Town bulletin
	}

	@Root("bulletin")
	public void bulletin(
		Player player,
		@Static String server,
		@Static String mute
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);

		if (profile.hasMutedServerBulletin())
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You have unmuted the server bulletin.", "#FFAA00", "#FFFFFF")));
			profile.setMuteServerBulletin(false);
			return;
		}

		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You have muted the server bulletin.", "#FFAA00", "#FFFFFF")));
		profile.setMuteServerBulletin(true);
	}

	@Root("bulletin")
	public void bulletin(
		Player sender,
		@Static(permission = "synergy.modifybulletin") String server,
		@Static String add,
		String message
	) {
		Synergy.getBulletin().add(message);
		Message.send(sender, "§aAdded new bulletin message at index " + (Synergy.getBulletin().size() - 1) + ".");
	}

	@Root("bulletin")
	public void bulletinClear(
		Player sender,
		@Static(permission = "synergy.modifybulletin") String server,
		@Static String clear
	) {
		Synergy.getBulletin().clear();
		Message.send(sender, "§aCleared server bulletin.");
	}

	@Root("bulletin")
	public void bulletinRemove(
		Player sender,
		@Static(permission = "synergy.modifybulletin") String server,
		@Static String remove,
		@Range(minimum = 0, maximum = Integer.MAX_VALUE) int index
	) {
		if (index > (Synergy.getBulletin().size() - 1))
		{
			Message.send(sender, "§cBulletin index out of bounds.");
			return;
		}

		Synergy.getBulletin().remove(index);
		Message.send(sender, "§aRemoved index " + index + " from server bulletin.");
	}

	@Root("bulletin")
	public void bulletinTownMute(
		Player player,
		@Static String town,
		@Static String mute
	) {
	}

	@Root("bulletin")
	public void bulletinTownAdd(
		Player sender,
		@Static String town,
		@Static String add,
		String message
	) {
		// TODO
	}

	@Root("bulletin")
	public void bulletinTownClear(
		Player sender,
		@Static String town,
		@Static String clear
	) {
		// TODO
	}

	@Root("bulletin")
	public void bulletinTownRemove(
		Player sender,
		@Static String town,
		@Static String remove,
		@Range(minimum = 0, maximum = Integer.MAX_VALUE) int index
	) {
		// TODO
	}

	@Override
	public HyacinthModule getModule() 
	{
		return Synergy.getInstance();
	}
}
