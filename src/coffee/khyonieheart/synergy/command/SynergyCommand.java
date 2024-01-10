package coffee.khyonieheart.synergy.command;

import java.io.File;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R2.CraftChunk;
import org.bukkit.entity.Player;
import org.bukkit.util.StructureSearchResult;

import coffee.khyonieheart.hyacinth.Gradient;
import coffee.khyonieheart.hyacinth.Gradient.GradientGroup;
import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.marker.Range;
import coffee.khyonieheart.synergy.Synergy;
import coffee.khyonieheart.synergy.Updater;
import coffee.khyonieheart.synergy.Updater.UpdateStatus;
import coffee.khyonieheart.synergy.api.BooleanSetting;
import coffee.khyonieheart.synergy.api.gatedevents.GatedBranch;
import coffee.khyonieheart.synergy.chat.ChatChannel;
import coffee.khyonieheart.synergy.parsers.StructureScanType;
import coffee.khyonieheart.synergy.profile.PlayerProfile;
import coffee.khyonieheart.synergy.profile.ProfileListener;
import coffee.khyonieheart.tidal.ArgCount;
import coffee.khyonieheart.tidal.TidalCommand;
import coffee.khyonieheart.tidal.structure.Root;
import coffee.khyonieheart.tidal.structure.Static;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class SynergyCommand extends TidalCommand
{
	public SynergyCommand() 
	{
		super("synergy", "Synergy for season IV.", "synergy", null, "syn", "s");
	}

	@Root(isRootExecutor = true)
	public void synergy(
		CommandSender sender
	) {
		new Thread(() -> {
			sender.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Synergy version: #" + Updater.getSynergyHash().substring(0, 8), "#FFAA00", "#FFFFFF")));
		}).start();
	}

	@Root(permission = "synergy.admin")
	public void update(
		CommandSender sender
	) {
		sender.spigot().sendMessage(new Gradient("#00FFFF", "#FFFFFF").createComponents("Checking for update..."));
		Runnable updateThread = () -> {
			UpdateStatus status = Updater.update();
			switch (status)
			{
				case SUCCESS_UPDATE -> sender.spigot().sendMessage(new Gradient("#55FF55", "#FFFFFF").createComponents("An update is available!"));
				case SUCCESS_NO_UPDATE -> sender.spigot().sendMessage(new Gradient("#AAAAAA", "#FFFFFF").createComponents("No update is available."));
				case BUILD_FAIL -> sender.spigot().sendMessage(new Gradient("#FF5555", "#FFFFFF").createComponents("Build failed."));
			}
		};

		new Thread(updateThread).start();
	}

	@Root(permission = "synergy.admin")
	public void schedulerestart(
		CommandSender sender
	) {
		Updater.scheduleShutdown();
		sender.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Shutdown has been scheduled.", "#FFAA00", "#FFFFFF")));
	}

	@Root
	public void guide(
		Player player
	) {
		player.getInventory().addItem(ProfileListener.createSynergyTutorialBook(player));
	}

	@Root
	public void scan(
		Player player,
		StructureScanType structure
	) {
		if (Synergy.getProfileManager().getProfile(player).getBranch() != GatedBranch.NIGHTLY)
		{
			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Structure bounds scanning is a nightly feature. Switch branches to the nightly branch to use structure bounds scanning.", "#5555FF", "#FF5555")));
			return;
		}
		
		StructureSearchResult result = player.getWorld().locateNearestStructure(player.getLocation(), structure.getType(), 1, false);

		if (result == null)
		{
			player.spigot().sendMessage(new Gradient("#AAAAAA", "#FFFFFF").createComponents("You are not standing in a(n) " + structure.name().toLowerCase() + "."));
			return;
		}

		// Oh boy. Here we go.
		CraftChunk objCraftChunk = (CraftChunk) player.getLocation().getWorld().getChunkAt(result.getLocation());
		objCraftChunk.getHandle(ChunkStatus.e).g().forEach((type, start) -> {
			StructureBoundingBox box = start.a();

			int minX = box.g();
			int maxX = box.j();
			int minY = box.h();
			int maxY = box.k();
			int minZ = box.i();
			int maxZ = box.l();

			player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Scanned structure:", "#55FF55", "#FFFFFF")));
			player.spigot().sendMessage(new Gradient("#AAAAAA", "#FFFFFF").createComponents("X Bounds: min " + minX + " | max " + maxX));
			player.spigot().sendMessage(new Gradient("#AAAAAA", "#FFFFFF").createComponents("Y Bounds: min " + minY + " | max " + maxY));
			player.spigot().sendMessage(new Gradient("#AAAAAA", "#FFFFFF").createComponents("Z Bounds: min " + minZ + " | max " + maxZ));
		});
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

	@Root
	public void branch(
		Player player
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(player);
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You are currently on the " + profile.getBranch().name().toLowerCase() + " branch.", "#FFAA00", "#FFFFFF")));
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("- Stable", "#55FF55", "#00AA00"), new GradientGroup(": Only complete features. The most stable experience.", "#AAAAAA", "#FFFFFF")));
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("- Beta", "#FFFF55", "#FFAA00"), new GradientGroup(": New features and mechanics. Some things may not work as intended.", "#AAAAAA", "#FFFFFF")));
		player.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), 
			new GradientGroup("- Nightly", "#FF5555", "#AA0000"), 
			new GradientGroup(": The bleeding edge, updated daily. Changes will be debuted here with little testing. ", "#AAAAAA", "#FFFFFF"), 
			new GradientGroup("Here be dragons!", "#5555FF", "#FF5555"))
		);
	}

	@Root
	public void branch(
		Player sender,
		GatedBranch branch
	) {
		PlayerProfile profile = Synergy.getProfileManager().getProfile(sender);

		if (profile.getBranch() == branch)
		{
			sender.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Nothing changed. You are already part of the " + branch.name().toLowerCase() + " branch.", "#AAAAAA", "#FFFFFF")));
			return;
		}

		profile.setBranch(branch);
		sender.spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("Updated feature branch setting.", "#55FF55", "#FFFFFF")));
	}

	@Override
	public HyacinthModule getModule() 
	{
		return Synergy.getInstance();
	}
}
