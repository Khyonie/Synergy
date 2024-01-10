package coffee.khyonieheart.synergy.tablist;

import java.util.ArrayDeque;
import java.util.Deque;

import org.bukkit.Bukkit;

import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.synergy.Synergy;

public class TpsMonitor
{
	private static float minuteAverage = 0f;
	private static float fiveMinuteAverage = 0f;
	private static float hourAverage = 0f;
	private static byte ticks = 0;
	private static byte previousTicks = 0;

	public static void startMonitoring()
	{
		Deque<Byte> hourValues = new ArrayDeque<>();
		Deque<Byte> minuteValues = new ArrayDeque<>();
		Deque<Byte> fiveMinuteValues = new ArrayDeque<>();

		// Ticker
		Bukkit.getScheduler().runTaskTimer(Hyacinth.getInstance(), () -> ticks++, 0l, 1l);

		Bukkit.getScheduler().runTaskTimerAsynchronously(Hyacinth.getInstance(), () -> {
			synchronized (hourValues) 
			{
				hourValues.push(ticks);
			}
			synchronized (minuteValues)
			{
				minuteValues.push(ticks);
			}
			synchronized (fiveMinuteValues) 
			{
				fiveMinuteValues.push(ticks);
			}
			previousTicks = ticks;
			ticks = 0;
		}, 0l, 20l);

		Bukkit.getScheduler().runTaskTimer(Hyacinth.getInstance(), () -> Synergy.getTabListManager().updateTps(), 0l, 20l);

		Bukkit.getScheduler().runTaskTimerAsynchronously(Hyacinth.getInstance(), () -> {
			float total = 0;
			synchronized (minuteValues)
			{
				for (Byte b : minuteValues)
				{
					total += b;
				}

				minuteAverage = total / minuteValues.size();
				minuteValues.clear();
			}
		}, 0l, 20 * 60);

		Bukkit.getScheduler().runTaskTimerAsynchronously(Hyacinth.getInstance(), () -> {
			float total = 0;
			synchronized (fiveMinuteValues)
			{
				for (Byte b : fiveMinuteValues)
				{
					total += b;
				}

				fiveMinuteAverage = total / fiveMinuteValues.size();
				fiveMinuteValues.clear();
			}
		}, 0l, 20 * 60 * 5);

		Bukkit.getScheduler().runTaskTimerAsynchronously(Hyacinth.getInstance(), () -> {
			float total = 0;
			for (Byte b : hourValues)
			{
				total += b;
			}

			hourAverage = total / hourValues.size();
			hourValues.clear();
		}, 0l, 20 * 60 * 60);
	}

	public static byte getPreviousSecond()
	{
		return previousTicks;
	}
	
	public static float getLastMinute()
	{
		return minuteAverage;
	}

	public static float getLastFiveMinutes()
	{
		return fiveMinuteAverage;
	}

	public static float getLastHour()
	{
		return hourAverage;
	}
}
