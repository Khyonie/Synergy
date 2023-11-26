package coffee.khyonieheart.synergy.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.OfflinePlayer;

import com.google.gson.annotations.Expose;

import coffee.khyonieheart.hyacinth.util.marker.Nullable;
import coffee.khyonieheart.synergy.api.Required;

public class PlayerProfile
{
	@Expose
	private String nickname;
	@Expose
	private String username;
	@Required
	@Expose
	private Pronouns pronouns = Pronouns.ASK_ME;
	@Expose
	private String homeTown;
	@Required
	@Expose
	private int money = 100;
	@Required
	@Expose
	private List<String> mail = new ArrayList<>();
	@Expose
	private int timesJoined = 0;
	@Required
	@Expose
	private boolean mutedServerBulitin = false;
	@Required
	@Expose
	private boolean mutedTownBulitin = false;
	@Required
	@Expose
	private String chatChannel = "global";
	@Required
	@Expose
	private int localChatRadius = 50;

	public PlayerProfile(
		OfflinePlayer player
	) {
		this.username = player.getName();
	}

	public void addMail(
		String sender,
		String content
	) {
		mail.add("§e§nFrom " + sender + "§6: " + content);
	}

	public List<String> getMail()
	{
		return Collections.unmodifiableList(this.mail);
	}

	public boolean hasMail()
	{
		return !this.mail.isEmpty();
	}

	public void clearMail()
	{
		mail.clear();
	}
	
	public int getTimesJoined()
	{
		return timesJoined;
	}

	public void incrementTimesJoined()
	{
		timesJoined++;
	}

	public String getNickName()
	{
		return this.nickname;
	}

	public boolean hasNickName()
	{
		return this.nickname != null;
	}

	public void setNickName(
		String nickname
	) {
		this.nickname = nickname;
	}

	public void removeNickname()
	{
		this.nickname = null;
	}

	public boolean hasMutedServerBulitin()
	{
		return this.mutedServerBulitin;
	}

	public void setMuteServerBulitin(
		boolean state
	) {
		this.mutedServerBulitin = state;
	}

	public String getChatChannel()
	{
		return this.chatChannel;
	}

	public void setChatChannel(
		String channel
	) {
		this.chatChannel = channel;
	}

	public int getChatRadius()
	{
		return this.localChatRadius;
	}

	public void setChatRadius(
		int chatRadius
	) {
		this.localChatRadius = chatRadius;
	}

	public Pronouns getPronouns()
	{
		return this.pronouns;
	}

	public void setPronouns(
		Pronouns pronouns
	) {
		this.pronouns = pronouns;
	}

	@Nullable
	public String getTown()
	{
		return this.homeTown;
	}
}
