package coffee.khyonieheart.synergy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.google.gson.reflect.TypeToken;

import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.Folders;
import coffee.khyonieheart.hyacinth.util.JsonUtils;
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
	private static Synergy instance;

	@Override
	public void onEnable()
	{
		instance = this;

		Folders.ensureFolder("SynergyData");
		Folders.ensureFolders("SynergyData", "profiles", "towns");

		// TPS updater
		TpsMonitor.startMonitoring();

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

	public static HyacinthModule getInstance()
	{
		return instance;
	}
}
