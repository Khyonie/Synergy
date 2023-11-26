package coffee.khyonieheart.synergy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import coffee.khyonieheart.hyacinth.Gradient;
import coffee.khyonieheart.hyacinth.Gradient.GradientGroup;
import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.synergy.profile.PlayerProfile;
import coffee.khyonieheart.synergy.tablist.TabListManager;

public class ProfileListener implements Listener
{
	private static String[] TOWN_TUTORIAL_BOOK = new String[] {
		"""
		-< Welcome! >-

		Glad you could join us, %NAME!
		As always, please direct any issues and feedback towards §9khyonie§r on Discord.
		""",
		"""
		-< This Book >-

		This book serves as an overview of the new and returning features this season. 
		If ever you need to regenerate this book, use §n/synergy guide§r.
		""",
		//
		//
		"""


		§lMechanics
		""",
		"""
		-< Double-Jump >-

		Jumping midair (similar to flying in creative mode) will initiate a second, higher jump. 

		§c⚠ For the purposes of this mechanic, fall damage has been disabled. 
		""",
		"""
		-< Death Chests >-

		On death, a chest will be placed as close as possible with the content of your inventory.  
		A ticket will be placed in your inventory with the coordinates and dimension of this chest.
		""",
		"""
		-< Librarians >- 

		The librarian villager changes from snapshot w23w31a have been removed. The other villager changes have been left as-is.
		""",
		"""
		-< Rail Recipe >-

		Rail recipies have been changed to yield much more than before.
		Rails: x16 -> x64
		Powered Rails: x6 -> x64
		Detector Rails: x6 -> x32
		Activator Rails: x6 -> x32
		""",
		"""
		-< Ice Recipe >-
		
		Ice has been made much easier to obtain. Crafting a water bucket with a snow block will yield 4 ice blocks.
		""",
		"""
		-< Saddle Recipe >-

		Saddles can now be crafted.
		§6▲§r │ §6▲§r │ §6▲§r
		━━╁━━━╁━━
		§6▲§r │ §9●§r │ §6▲§r
		━━╁━━━╁━━
		  │   │
		§6▲ → Leather
		§9● → String
		""",
		//
		//
		"""


		§lTowns and Outposts
		""",
		"""
		-< Town Center 1 >-
		
		To create a town, begin by crafting a Town Center.
		  │ §6▲§r │  
		━━╁━━━╁━━
		§6▲§r │ §9◆§r │ §6▲§r
		━━╁━━━╁━━
		§c●§r │ §c●§r │ §c●§r
		§6▲ → Wool§r
		§9◆ → Dispenser§r
		§c● → Log§r
		""",
		"""
		-< Town Center 2 >-

		Choose a suitable location for your town, and place your Town Center.

		§c⚠ Make sure your town has room to grow! The area protected by your town will expand.
		""",
		"""
		-< Town Center 3 >-
		You will be prompted to choose a §asquare§r or §9cylindrical§r claim area. This cannot be changed later.

		§c⚠ If you currently belong to a town, you must leave it before creating your own. 
		""",
		"""
		-< Expansion >-

		Your town's protected area can be expanded through two means:
		1) Convince players to join your town.
		2) As a community, complete daily and weekly town quests. Check your town quests with §n/synergy town quests§r.
		"""
	};

	@EventHandler
	public void onPlayerJoin(
		PlayerJoinEvent event
	) {
		if (Synergy.getProfileManager().loadProfile(event.getPlayer()))
		{
			Synergy.getProfileManager().getProfile(event.getPlayer()).addMail("Synergy", "Your player data has been updated to the latest version!");
		}

		PlayerProfile profile = Synergy.getProfileManager().getProfile(event.getPlayer());

		if (profile.getTimesJoined() == 0)
		{
			profile.addMail("Synergy", "Cheers friend! Welcome to season IV of the SLLPA Minecraft server. This season's core mechanic begins with §f/synergy§6 (or /syn if you prefer). Thanks for being here, read the /rules if you haven't already, and have fun! -Khyonie");

			Bukkit.getServer().dispatchCommand(event.getPlayer(), "synergy guide");
		}

		Logger.debug("Has muted bulitin? " + profile.hasMutedServerBulitin());
		Logger.debug("Is bulitin empty? " + Synergy.getBulitin().isEmpty());
		if (!profile.hasMutedServerBulitin() && !Synergy.getBulitin().isEmpty())
		{
			event.getPlayer().spigot().sendMessage(Gradient.createComponents(new GradientGroup("-----------------【 ", "#FFFFFF", "#55FF99"), new GradientGroup("Server Bulitin", "#FFAA00", "#FFAA00"), new GradientGroup(" 】-----------------", "#55FF99", "#FFFFFF")));
			Message.send(event.getPlayer(), "§r");
			for (String s : Synergy.getBulitin())
			{
				event.getPlayer().spigot().sendMessage(new Gradient("#55FF55", "#00FFFF").createComponents(s));
			}
			Message.send(event.getPlayer(), "§r");
			event.getPlayer().spigot().sendMessage(Gradient.createComponents(new GradientGroup("-------------------------", "#FFFFFF", "#55FF99"), new GradientGroup("-------------------------", "#55FF99", "#FFFFFF")));
		}

		if (profile.hasMail())
		{
			event.getPlayer().spigot().sendMessage(Gradient.createComponents(new GradientGroup("[Synergy] ", "#005555", "#00FFFF"), new GradientGroup("You have new mail. Use \"/synergy mail read\" to view it.", "#FFAA00", "#FFFFFF")));
		}

		profile.incrementTimesJoined();
		event.getPlayer().setAllowFlight(true);
		TabListManager.update(event.getPlayer());
	}

	@EventHandler
	public void onPlayerLeave(
		PlayerQuitEvent event
	) {
		Synergy.getProfileManager().unloadProfile(event.getPlayer());
	}

	public static ItemStack createSynergyTutorialBook(
		Player player
	) {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

		BookMeta meta = (BookMeta) book.getItemMeta();
		meta.setTitle("§eGuide to Season IV");
		meta.setAuthor("Synergy");

		for (int i = 0; i < TOWN_TUTORIAL_BOOK.length; i++)
		{
			meta.addPage(TOWN_TUTORIAL_BOOK[i].replace("%NAME", player.getDisplayName()));
		}

		book.setItemMeta(meta);

		return book;
	}
}
