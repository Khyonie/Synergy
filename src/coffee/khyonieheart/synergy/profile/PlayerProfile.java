package coffee.khyonieheart.synergy.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.OfflinePlayer;

import com.google.gson.annotations.Expose;

import coffee.khyonieheart.hyacinth.util.marker.Nullable;
import coffee.khyonieheart.synergy.api.Required;
import coffee.khyonieheart.synergy.chat.ChatChannel;
import coffee.khyonieheart.synergy.mechanics.party.PartyBonus;
import coffee.khyonieheart.synergy.mechanics.party.PartyColor;

public class PlayerProfile
{
	@Expose private String nickname;
	@Expose private String username; 
	@Required @Expose private Pronouns pronouns = Pronouns.ASK_ME;
	@Expose private String homeTown;
	@Required @Expose private int money = 100;
	@Required @Expose private List<String> mail = new ArrayList<>();
	@Expose private int timesJoined = 0;
	@Required @Expose private boolean mutedServerBulletin = false;
	@Required @Expose private boolean mutedTownBulletin = false;
	@Required @Expose private ChatChannel chatChannel = ChatChannel.GLOBAL;
	@Required @Expose private int localChatRadius = 50;
	@Required @Expose private PartyBonus partyBonus = PartyBonus.ATTACK_DAMAGE;
	@Required @Expose private boolean enableDoubleJump = true;
	@Required @Expose private PartyColor preferredPartyColor = PartyColor.AQUA;
	@Required @Expose private int pvpRating = 1000;

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

	public boolean hasMutedServerBulletin()
	{
		return this.mutedServerBulletin;
	}

	public void setMuteServerBulletin(
		boolean state
	) {
		this.mutedServerBulletin = state;
	}

	public ChatChannel getChatChannel()
	{
		return this.chatChannel;
	}

	public void setChatChannel(
		ChatChannel channel
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

	public PartyBonus getPartyBonus()
	{
		return this.partyBonus;
	}

	public void setPartyBonus(
		PartyBonus partyBonus
	) {
		this.partyBonus = partyBonus;
	}

	public void setEnableDoubleJump(
		boolean setting
	) {
		this.enableDoubleJump = setting;
	}

	public boolean getEnableDoubleJump()
	{
		return this.enableDoubleJump;
	}

	public PartyColor getPartyColor()
	{
		return this.preferredPartyColor;
	}

	public void setPartyColor(
		PartyColor color
	) {
		this.preferredPartyColor = color;
	}

	public int getDuelRating()
	{
		return this.pvpRating;
	}

	public void incrementDuelRating(
		int delta
	) {
		this.pvpRating += delta;
	}

	public void decrementDuelRating(
		int delta
	) {
		this.pvpRating -= delta;
	}
}
