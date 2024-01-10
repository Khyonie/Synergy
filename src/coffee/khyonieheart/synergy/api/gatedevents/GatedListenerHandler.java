package coffee.khyonieheart.synergy.api.gatedevents;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.hyacinth.killswitch.Feature;
import coffee.khyonieheart.hyacinth.killswitch.FeatureIdentifier;
import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.ClassShader;
import coffee.khyonieheart.hyacinth.util.Reflect;
import coffee.khyonieheart.synergy.Synergy;

@FeatureIdentifier("gatedEvents")
public class GatedListenerHandler implements Listener, ClassShader<GatedListener>, Feature
{
	public static Map<Class<? extends Event>, Map<EventPriority, RegisteredGatedEvent>> handlers = new HashMap<>();
	private static boolean isEnabled = true;

	@Override
	public Class<GatedListener> getType() 
	{
		return GatedListener.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GatedListener process(
		Class<? extends GatedListener> type,
		GatedListener instance
	) {
		Logger.verbose("Shading gated listener class " + type.getName());
		Set<Class<?>> coveredEvents = new HashSet<>();
		for (Method m : this.getClass().getMethods())
		{
			if (m.getParameterCount() != 1)
			{
				continue;
			}

			if (!m.isAnnotationPresent(EventHandler.class))
			{
				continue;
			}

			if (!Event.class.isAssignableFrom(m.getParameterTypes()[0]))
			{
				continue;
			}

			coveredEvents.add(m.getParameterTypes()[0]);
		}

		if (instance != null)
		{
			for (Method m : type.getDeclaredMethods())
			{
				if (!m.isAnnotationPresent(GatedEventHandler.class))
				{
					continue;
				}

				m.setAccessible(true);
				if (m.getParameterCount() == 0)
				{
					Logger.log("§cEvent handlers must possess an event type as their first and only parameter in " + type.getName());
					continue;
				}
				
				if (!Event.class.isAssignableFrom(m.getParameterTypes()[0]))
				{
					Logger.log("§cIllegal event type \"" + m.getParameterTypes()[0].getName() + "\" in listener method " + m.getName() + " in " + type.getName());
					continue;
				}
				
				if (!coveredEvents.contains(m.getParameterTypes()[0]))
				{
					Logger.log("§dUnregistered event type \"" + m.getParameterTypes()[0].getName() + "\". Register an event delegator in GatedListenerHandler to support this type of event");
					continue;
				}

				if (!handlers.containsKey(m.getParameterTypes()[0]))
				{
					handlers.put((Class<? extends Event>) m.getParameterTypes()[0], new HashMap<>());
					for (EventPriority priority : EventPriority.values())
					{
						handlers.get(m.getParameterTypes()[0]).put(priority, new RegisteredGatedEvent(instance, new ArrayList<>()));
					}
					Logger.debug("Registering event handler method " + m.getParameterTypes()[0].getName());
				}

				EventPriority priority = m.getAnnotation(GatedEventHandler.class).priority();
				handlers.get(m.getParameterTypes()[0]).get(priority).methods().add(m);
			}

			return null;
		}

		GatedListener listener = Reflect.simpleInstantiate(type);
		process(type, listener);
		return listener;
	}

	// --------------------------------------------------------------------------------
	// Delegator methods 
	// Add an event handler that passes off events to delegate() to delegate the event
	// -------------------------------------------------------------------------------- 

	@EventHandler
	public void onBlockBreak(
		BlockBreakEvent event
	) {
		delegate(event.getPlayer(), event, handlers.get(event.getClass()));
	}

	@EventHandler
	public void onInventoryClick(
		InventoryClickEvent event
	) {
		delegate((Player) event.getWhoClicked(), event, handlers.get(event.getClass()));
	}

	@EventHandler
	public void onInventoryOpen(
		InventoryOpenEvent event
	) {
		delegate((Player) event.getPlayer(), event, handlers.get(event.getClass()));
	}

	@EventHandler
	public void onInventoryClose(
		InventoryCloseEvent event
	) {
		
		delegate((Player) event.getPlayer(), event, handlers.get(event.getClass()));
	}

	@EventHandler
	public void onPrepareCraft(
		PrepareItemCraftEvent event
	) {
		delegate((Player) event.getView().getPlayer(), event, handlers.get(event.getClass()));
	}

	@EventHandler
	public void onMove(
		PlayerMoveEvent event
	) {
		delegate((Player) event.getPlayer(), event, handlers.get(event.getClass()));
	}

	@EventHandler
	public void onEntityDamage(
		EntityDamageEvent event
	) {
		if (event.getEntity() instanceof Player player)
		{
			delegate(player, event, handlers.get(event.getClass()));
		}
	}

	@EventHandler
	public void onEntityTargetPlayer(
		EntityTargetLivingEntityEvent event
	) {
		if (event.getTarget() instanceof Player player)
		{
			delegate(player, event, handlers.get(event.getClass()));
		}
	}

	//
	// Delegation target
	//
	public static void delegate(
		Player player,
		Event event,
		Map<EventPriority, RegisteredGatedEvent> handlers
	) {
		if (!isEnabled)
		{
			return;
		}

		if (handlers == null)
		{
			Logger.log("§eAttempted to handle unregistered event type " + event.getClass().getName() + " as delegation target. Likely cause is a valid delegator passing a subclass as a delegation target, which is unsupported.");
			return;
		}

		if (Synergy.getProfileManager().getProfile(player) == null)
		{
			return;
		}

		GatedBranch branch = Synergy.getProfileManager().getProfile(player).getBranch();
		for (EventPriority priority : EventPriority.values())
		{
			GatedListener instance = handlers.get(priority).instance();
			for (Method m : handlers.get(priority).methods())
			{
				// Logger.debug("Branch gate: " + m.getAnnotation(GatedEventHandler.class).branch().name() + " | player branch: " + branch.name());
				if (branch.ordinal() > m.getAnnotation(GatedEventHandler.class).branch().ordinal())
				{
					continue;
				}

				try {
					m.invoke(instance, event);
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Error e) {
					Logger.log("§cAn error was caught when attempting to invoke method " + m.getName() + " in " + m.getDeclaringClass().getName());
					Message.send(player, "§cAn error was caught when attempting to invoke method " + m.getName() + " in " + m.getDeclaringClass().getName());
					e.printStackTrace();
				}
			}
		}
	}

	private static record RegisteredGatedEvent(
		GatedListener instance,
		List<Method> methods
	) {}

	@Override
	public boolean isEnabled(String target) 
	{
		return isEnabled;
	}

	@Override
	public boolean kill(String target) 
	{
		if (!isEnabled)
		{
			return false;
		}

		isEnabled = false;

		return true;
	}

	@Override
	public boolean reenable(String target) 
	{
		if (isEnabled)
		{
			return false;
		}

		isEnabled = true;	

		return true;
	}
}
