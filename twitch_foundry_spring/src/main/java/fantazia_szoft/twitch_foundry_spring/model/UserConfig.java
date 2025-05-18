package fantazia_szoft.twitch_foundry_spring.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
public class UserConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String twitchChannelId;
    private String twitchuserId;
    private String twitchToken;
    private String foundryApiKey;

    private String player1Name;
    private String player2Name;
    private String player3Name;
    private String player4Name;
    private String player5Name;
    private String player6Name;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTwitchuserId() {
		return twitchuserId;
	}
	public void setTwitchuserId(String twitchuserId) {
		this.twitchuserId = twitchuserId;
	}
	public String getTwitchToken() {
		return twitchToken;
	}
	public void setTwitchToken(String twitchToken) {
		this.twitchToken = twitchToken;
	}
	public String getFoundryApiKey() {
		return foundryApiKey;
	}
	public void setFoundryApiKey(String foundryApiKey) {
		this.foundryApiKey = foundryApiKey;
	}
	public String getPlayer1Name() {
		return player1Name;
	}
	public void setPlayer1Name(String player1Name) {
		this.player1Name = player1Name;
	}
	public String getPlayer2Name() {
		return player2Name;
	}
	public void setPlayer2Name(String player2Name) {
		this.player2Name = player2Name;
	}
	public String getPlayer3Name() {
		return player3Name;
	}
	public void setPlayer3Name(String player3Name) {
		this.player3Name = player3Name;
	}
	public String getPlayer4Name() {
		return player4Name;
	}
	public void setPlayer4Name(String player4Name) {
		this.player4Name = player4Name;
	}
	public String getPlayer5Name() {
		return player5Name;
	}
	public void setPlayer5Name(String player5Name) {
		this.player5Name = player5Name;
	}
	public String getPlayer6Name() {
		return player6Name;
	}
	public void setPlayer6Name(String player6Name) {
		this.player6Name = player6Name;
	}
	public UserConfig() {
		super();
	}
	public String gettwitchChannelId() {
		return twitchChannelId;
	}
	public void settwitchChannelId(String twitchChannelId) {
		this.twitchChannelId = twitchChannelId;
	}
	public UserConfig(Long id, String twitchChannelId, String twitchuserId, String twitchToken, String foundryApiKey,
			String player1Name, String player2Name, String player3Name, String player4Name, String player5Name,
			String player6Name) {
		super();
		this.id = id;
		this.twitchChannelId = twitchChannelId;
		this.twitchuserId = twitchuserId;
		this.twitchToken = twitchToken;
		this.foundryApiKey = foundryApiKey;
		this.player1Name = player1Name;
		this.player2Name = player2Name;
		this.player3Name = player3Name;
		this.player4Name = player4Name;
		this.player5Name = player5Name;
		this.player6Name = player6Name;
	}
	@Override
	public String toString() {
		return "UserConfig [id=" + id + ", twitchChannelId=" + twitchChannelId + ", twitchuserId=" + twitchuserId
				+ ", twitchToken=" + twitchToken + ", foundryApiKey=" + foundryApiKey + ", player1Name=" + player1Name
				+ ", player2Name=" + player2Name + ", player3Name=" + player3Name + ", player4Name=" + player4Name
				+ ", player5Name=" + player5Name + ", player6Name=" + player6Name + "]";
	}
}

