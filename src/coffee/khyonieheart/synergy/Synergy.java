package coffee.khyonieheart.synergy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import com.google.gson.reflect.TypeToken;

import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.Folders;
import coffee.khyonieheart.hyacinth.util.JsonUtils;
import coffee.khyonieheart.synergy.crafting.RecipeManager;
import coffee.khyonieheart.synergy.mechanics.party.PartyManager;
import coffee.khyonieheart.synergy.profile.PlayerProfile;
import coffee.khyonieheart.synergy.profile.ProfileManager;
import coffee.khyonieheart.synergy.tablist.TabListManager;
import coffee.khyonieheart.synergy.tablist.TpsMonitor;

public class Synergy implements HyacinthModule
{
	private static ProfileManager profileManager = new ProfileManager();
	private static TabListManager tablistManager = new TabListManager();
	private static PartyManager partyManager = new PartyManager();
	private static List<String> bulletin = new ArrayList<>();
	private static Set<Player> afkPlayers = new HashSet<>();
	private static Synergy instance;
	private static String hash;

	@Override
	public void onEnable()
	{
		instance = this;

		Folders.ensureFolder("SynergyData");
		Folders.ensureFolders("SynergyData", "profiles", "towns");

		hash = Updater.getSynergyHash();
		RecipeManager.adaptAllRecipes();
		//RecipeManager.clearVanillaRecipes();

		// TPS updater
		TpsMonitor.startMonitoring();

		// Auto updater
		// Updater.start(); 

		File bulletinFile = new File("SynergyData/bulletin.json");	
		if (!bulletinFile.exists())
		{
			try {
				bulletinFile.createNewFile();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		try {
			bulletin = JsonUtils.fromJson("SynergyData/bulletin.json", TypeToken.getParameterized(ArrayList.class, String.class).getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable()
	{
		JsonUtils.toFile("SynergyData/bulletin.json", bulletin);
	}

	public static String getName(
		Player player
	) {
		PlayerProfile profile = profileManager.getProfile(player);
		return profile.hasNickName() ? profile.getNickName() : player.getDisplayName();
	}

	public static List<String> getBulletin()
	{
		return bulletin;
	}

	public static ProfileManager getProfileManager()
	{
		return profileManager;
	}

	public static TabListManager getTabListManager()
	{
		return tablistManager;
	}

	public static PartyManager getPartyManager()
	{
		return partyManager;
	}

	public static String getSynergyHash()
	{
		return hash;
	}

	public static Set<Player> getAfkPlayers()
	{
		return afkPlayers;
	}

	public static HyacinthModule getInstance()
	{
		return instance;
	}
}
