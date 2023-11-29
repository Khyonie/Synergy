package coffee.khyonieheart.synergy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import coffee.khyonieheart.hyacinth.Gradient;
import coffee.khyonieheart.synergy.profile.PlayerProfile;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class ChatListener implements Listener
{
	@EventHandler
	public void onChat(
		AsyncPlayerChatEvent event
	) {
		event.setCancelled(true);

		PlayerProfile profile = Synergy.getProfileManager().getProfile(event.getPlayer());

		switch (profile.getChatChannel())
		{
			case GLOBAL -> {
				ComponentBuilder builder = new ComponentBuilder();

				if (profile.getTown() != null)
				{
					TextComponent component = new TextComponent("(" + profile.getTown() + ") ");
					component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text("- TODO TYPE -"), new Text("§7Residents: TODO"), new Text("§7Power: TODO")));

					builder.append(component);
				}

				TextComponent component = new TextComponent("<" + (profile.hasNickName() ? profile.getNickName() : event.getPlayer().getDisplayName()) + ">");
				switch (profile.getPronouns())
				{
					case ASK_ME -> component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text("§7§oAsk me for my pronouns")));
					default -> component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text("§7" + profile.getPronouns().getSingular() + "/" + profile.getPronouns().getPosessive() + "/" + profile.getPronouns().getPluralPossesive() + "/" + profile.getPronouns().getReflexive())));
				}

				builder.append(component);
				builder.append(new TextComponent(" " + event.getMessage()));
				BaseComponent[] components = builder.create();

				for (Player p : Bukkit.getOnlinePlayers())
				{
					if (Synergy.getProfileManager().getProfile(p).getChatChannel() == ChatChannel.GLOBAL)
					{
						p.spigot().sendMessage(components);
					}
				}
			}
			case LOCAL -> {
				for (Player p : event.getPlayer().getWorld().getPlayers())
				{
					if (p.getLocation().distanceSquared(event.getPlayer().getLocation()) <= (profile.getChatRadius() * profile.getChatRadius()))
					{
						p.spigot().sendMessage(new Gradient("#55FF55", "#FFFFFF").createComponents("(Local) <" + Synergy.getName(event.getPlayer()) + "> " + event.getMessage()));
					}
				}
			}
			case TOWN -> {
				// TODO This
			}
			case PARTY -> {
				for (Player p : Synergy.getPartyManager().getParty(event.getPlayer()).getMembers())
				{
					p.spigot().sendMessage(new Gradient("#55FFFF", "#FFFFFF").createComponents("(Party) <" + Synergy.getName(event.getPlayer()) + "> " + event.getMessage()));
				}
			}
		}
	}
}
