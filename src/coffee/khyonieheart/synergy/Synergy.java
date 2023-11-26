package coffee.khyonieheart.synergy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.Folders;
import coffee.khyonieheart.hyacinth.util.JsonUtils;

public class Synergy implements HyacinthModule
{
	private static ProfileManager profileManager = new ProfileManager();
	private static List<String> bulitin = new ArrayList<>();
	private static Synergy instance;

	@Override
	public void onEnable()
	{
		instance = this;

		Folders.ensureFolder("SynergyData");
		Folders.ensureFolders("SynergyData", "profiles", "towns");

		File bulitinFile = new File("SynergyData/bulitin.json");	
		if (!bulitinFile.exists())
		{
			try {
				bulitinFile.createNewFile();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		try {
			bulitin = JsonUtils.fromJson("SynergyData/bulitin.json", TypeToken.getParameterized(ArrayList.class, String.class).getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable()
	{
		JsonUtils.toFile("SynergyData/bulitin.json", bulitin);
	}

	public static List<String> getBulitin()
	{
		return bulitin;
	}

	public static ProfileManager getProfileManager()
	{
		return profileManager;
	}

	public static HyacinthModule getInstance()
	{
		return instance;
	}
}
