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
import coffee.khyonieheart.synergy.ProfileListener;
import coffee.khyonieheart.synergy.Synergy;
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
	public void channelLocal(
		Player player,
		@Static String local
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);

		if (profile.getChatChannel().equals("local"))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Current channel is already local. No change has been made.", "#FFAA00", "#FFFFFF")));
			return;
		}

		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Moved to local channel.", "#FFAA00", "#FFFFFF")));
	}

	@Root("channel")
	public void channelGlobal(
		Player player,
		@Static String global
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);

		if (profile.getChatChannel().equals("local"))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Current channel is already global. No change has been made.", "#FFAA00", "#FFFFFF")));
			return;
		}

		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Moved to global channel.", "#FFAA00", "#FFFFFF")));
	}

	@Root("channel")
	public void channelTown(
		Player player,
		@Static String town
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);

		if (profile.getChatChannel().equals("town"))
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Current channel is already town. No change has been made.", "#FFAA00", "#FFFFFF")));
			return;
		}

		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Moved to town channel.", "#FFAA00", "#FFFFFF")));
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

	// Bulitin commands
	//-------------------------------------------------------------------------------- 
	
	@Root
	public void bulitin(
		Player player
	) {
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("----------【 ", "#FFFFFF", "#55FF99"), new GradientGroup("Server Bulitin", "#D2AFFF", "#D2AFFF"), new GradientGroup(" 】----------", "#55FF99", "#FFFFFF")));
		for (String s : Synergy.getBulitin())
		{
			player.spigot().sendMessage(new Gradient("#005555", "#00FFFF").createComponents(s));
		}
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("--------------------", "#FFFFFF", "#55FF99"), new GradientGroup("--------------------", "#55FF99", "#FFFFFF")));
		
		// TODO Town bulitin
	}

	@Root("bulitin")
	public void bulitinMute(
		Player player,
		@Static String server,
		@Static String mute
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);

		if (profile.hasMutedServerBulitin())
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You have unmuted the server bulitin.", "#FFAA00", "#FFFFFF")));
			profile.setMuteServerBulitin(false);
			return;
		}

		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You have muted the server bulitin.", "#FFAA00", "#FFFFFF")));
		profile.setMuteServerBulitin(true);
	}

	@Root("bulitin")
	public void bulitinAdd(
		Player sender,
		@Static(permission = "synergy.modifybulitin") String server,
		@Static String add,
		String message
	) {
		Synergy.getBulitin().add(message);
		Message.send(sender, "§aAdded new bulitin message at index " + (Synergy.getBulitin().size() - 1) + ".");
	}

	@Root("bulitin")
	public void bulitinClear(
		Player sender,
		@Static(permission = "synergy.modifybulitin") String server,
		@Static String clear
	) {
		Synergy.getBulitin().clear();
		Message.send(sender, "§aCleared server bulitin.");
	}

	@Root("bulitin")
	public void bulitinRemove(
		Player sender,
		@Static(permission = "synergy.modifybulitin") String server,
		@Static String remove,
		@Range(minimum = 0, maximum = Integer.MAX_VALUE) int index
	) {
		if (index > (Synergy.getBulitin().size() - 1))
		{
			Message.send(sender, "§cBulitin index out of bounds.");
			return;
		}

		Synergy.getBulitin().remove(index);
		Message.send(sender, "§aRemoved index " + index + " from server bulitin.");
	}

	@Root("bulitin")
	public void bulitinTownMute(
		Player player,
		@Static String town,
		@Static String mute
	) {
	}

	@Root("bulitin")
	public void bulitinTownAdd(
		Player sender,
		@Static String town,
		@Static String add,
		String message
	) {
		// TODO
	}

	@Root("bulitin")
	public void bulitinTownClear(
		Player sender,
		@Static String town,
		@Static String clear
	) {
		// TODO
	}

	@Root("bulitin")
	public void bulitinTownRemove(
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
