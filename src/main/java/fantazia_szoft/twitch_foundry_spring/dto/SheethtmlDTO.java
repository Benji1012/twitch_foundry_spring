package fantazia_szoft.twitch_foundry_spring.dto;

public class SheethtmlDTO {
    private String playerName;
    private String sheetHtml;
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public String getSheetHtml() {
		return sheetHtml;
	}
	public void setSheetHtml(String sheetHtml) {
		this.sheetHtml = sheetHtml;
	}
	public SheethtmlDTO(String playerName, String sheetHtml) {
		super();
		this.playerName = playerName;
		this.sheetHtml = sheetHtml;
	}

    // constructor, getters
}