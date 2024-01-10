package coffee.khyonieheart.synergy;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.bukkit.Bukkit;

import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.Logger;

public class Updater
{
	private static boolean isShutdownScheduled = false;

	public static void start()
	{
		Hyacinth.getScheduler().runTaskTimer(
			Hyacinth.getInstance(),
			() -> {
				Thread thread = new Thread(() -> {
					UpdateStatus status = update();
					if (status == UpdateStatus.SUCCESS_UPDATE)
					{
						Logger.log("§aAn update has been queued.");
						scheduleShutdown();
					}
					Logger.log("§6Update process ended with status " + status.name());
				});
				thread.start();
			},
			20 * 60 * 60 * 6, // 6 hour intervals
			0L
		);
	}

	public static void scheduleShutdown()
	{
		isShutdownScheduled = true;
		Hyacinth.getScheduler().runTaskTimer(
			Hyacinth.getInstance(), () -> {
				if (Bukkit.getServer().getOnlinePlayers().size() == 0)
				{
					Logger.log("§aPerforming scheduled shutdown...");
					Bukkit.getServer().shutdown();
				}
			}, 
			20 * 60 * 5, // Check on 5 minute intervals
			0L
		);

	}

	public static UpdateStatus update()
	{
		Logger.log("Attempting to update...");
		try {
			Process process = new ProcessBuilder("wisteria", "build", "nightly")
				.directory(new File("/home/khyonie/Development/Working/Synergy/"))
				.start();

			BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = output.readLine()) != null)
			{
				Logger.log(line);
			}

			// process.destroy();
			process.waitFor();
			
			int code = process.exitValue();

			if (code != 0)
			{
				return UpdateStatus.BUILD_FAIL;
			}

			output.close();

			process = new ProcessBuilder("sha256sum", "Synergy.jar")
				.directory(new File("Hyacinth/modules/"))
				.start();

			output = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String hash = "";
			String buffer;
			while ((buffer = output.readLine()) != null)
			{
				hash = buffer;
			}

			hash = hash.split(" ")[0];
			Logger.log("Build hash is " + hash + ", current hash is " + Synergy.getSynergyHash());
			process.destroy();

			if (hash.equals(Synergy.getSynergyHash()))
			{
				return UpdateStatus.SUCCESS_NO_UPDATE;
			}

			return UpdateStatus.SUCCESS_UPDATE;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return UpdateStatus.BUILD_FAIL;
	}

	public static boolean isShutdownScheduled()
	{
		return isShutdownScheduled;
	}

	public static String getSynergyHash()
	{
		try {
			Process process = new ProcessBuilder("sha256sum", "Synergy.jar")
				.directory(new File("Hyacinth/modules/"))
				.start();
			BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String hash = "";
			String buffer;
			while ((buffer = output.readLine()) != null)
			{
				hash = buffer;
			}
			process.destroy();

			hash = hash.split(" ")[0];
			return hash;
		} catch (Exception e) {
			return "";
		}
	}

	public static enum UpdateStatus
	{
		SUCCESS_NO_UPDATE,
		SUCCESS_UPDATE,
		BUILD_FAIL
		;
	}
}
