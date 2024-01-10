package coffee.khyonieheart.synergy.profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.util.JsonUtils;
import coffee.khyonieheart.synergy.api.Required;

public class ProfileManager
{
	private Map<OfflinePlayer, PlayerProfile> loadedProfiles = new HashMap<>();

	public PlayerProfile getProfile(
		OfflinePlayer player
	) {
		return loadedProfiles.get(player);
	}

	public boolean loadProfile(
		OfflinePlayer player
	) {
		File target = new File("SynergyData/profiles/" + player.getUniqueId().toString());
		if (!target.exists())
		{
			PlayerProfile profile = new PlayerProfile(player);
			loadedProfiles.put(player, profile);
			
			return false;
		}

		try {
			PlayerProfile profile = JsonUtils.fromJson(target.getAbsolutePath(), PlayerProfile.class);
			loadedProfiles.put(player, profile);

			// Validate
			boolean updated = false;
			for (Field f : PlayerProfile.class.getDeclaredFields())
			{
				f.setAccessible(true);
				if (f.isAnnotationPresent(Required.class))
				{
					if (f.get(profile) == null)
					{
						f.set(profile, f.get(new PlayerProfile(player)));
						updated = true;
					}
				}
			}

			return updated;
		} catch (FileNotFoundException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void unloadProfile(
		OfflinePlayer player
	) {
		if (!loadedProfiles.containsKey(player))
		{
			return;
		}

		if (loadedProfiles.get(player) == null)
		{
			return;
		}

		JsonUtils.toFile("SynergyData/profiles/" + player.getUniqueId().toString(), loadedProfiles.get(player));
		loadedProfiles.remove(player);
		Logger.debug("Removal complete.");
	}

	public void resetProfile(
		OfflinePlayer player
	) {
		boolean loaded = false;
		if (!loadedProfiles.containsKey(player))
		{
			loadProfile(player);
			loaded = true;
		}

		this.loadedProfiles.put(player, new PlayerProfile(player));

		if (loaded)
		{
			unloadProfile(player);
		}
	}

	public void unloadAll()
	{
		List<OfflinePlayer> targets = new ArrayList<>(loadedProfiles.keySet());

		for (OfflinePlayer p : targets)
		{
			unloadProfile(p);;
		}
	}
}
