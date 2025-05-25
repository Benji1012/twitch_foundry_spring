package fantazia_szoft.twitch_foundry_spring.model;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import fantazia_szoft.twitch_foundry_spring.controller.FoundryApiClientController;


public class Player {

	private String client_id = "";
	private String uuid = "";
	private String name = "";
	private Integer maxHp = 0;
	private Integer currentHp = 0;
	private Integer ac = 0;
	private Integer str = 0;
	private Integer dex = 0;
	private Integer con = 0;
	private Integer intel = 0;
	private Integer wis = 0;
	private Integer cha = 0;
	private final FoundryApiClientController foundryClient;

	
	public Player( String name,  FoundryApiClientController foundryClient) {
		super();
		this.name = name;
		this.foundryClient = foundryClient;
		try {
			downloadId();
			if (this.uuid == null || this.uuid.isEmpty()) {
			   this.uuid = "0";
			   this.currentHp = 0;
		        this.maxHp = 0;
		        this.str = 0;
		        this.dex = 0;
		        this.con = 0;
		        this.intel = 0;
		        this.wis = 0;
		        this.cha = 0;
		        this.ac = 0;
			}else {
				JSONObject json = foundryClient.getPlayerStastByUUid(uuid);
				JSONObject data = json.getJSONObject("data");
				JSONObject system = data.getJSONObject("system");
				setClient_id(foundryClient.getClienId());
				JSONObject attributes = system.getJSONObject("attributes");
				JSONObject abilities = system.getJSONObject("abilities");
		
				// Now you can extract values
				Object tempValue = attributes.getJSONObject("hp").get("temp");
				int temp = (tempValue instanceof Number) ? ((Number) tempValue).intValue() : 0;
				this.currentHp = attributes.getJSONObject("hp").getInt("value") + temp;
				Object tempMaxValue = attributes.getJSONObject("hp").get("tempmax");
				int tempmax = (tempMaxValue instanceof Number) ? ((Number) tempMaxValue).intValue() : 0;
				this.maxHp = attributes.getJSONObject("hp").getInt("max") + tempmax;
		//		this.ac = attributes.getJSONObject("ac").optInt("flat", 0);  // fallback if null
		
				this.str = abilities.getJSONObject("str").getInt("value");
				this.dex = abilities.getJSONObject("dex").getInt("value");
				this.con = abilities.getJSONObject("con").getInt("value");
				this.intel = abilities.getJSONObject("int").getInt("value");
				this.wis = abilities.getJSONObject("wis").getInt("value");
				this.cha = abilities.getJSONObject("cha").getInt("value");
				this.ac = foundryClient.getAc(this.uuid);
				
			}
		} catch (Exception e) {
		    System.err.println("Failed to load player stats: " + e.getMessage());
		    e.printStackTrace();
		    this.currentHp = 0;
	        this.maxHp = 0;
	        this.str = 0;
	        this.dex = 0;
	        this.con = 0;
	        this.intel = 0;
	        this.wis = 0;
	        this.cha = 0;
	        this.ac = 0;
		}
	}
	
	 private void downloadId() {
	    	if(!name.equals("")) {
	    		uuid = foundryClient.getPlayerIdByName(name);
	    	}
	    }
	 
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getMaxHp() {
		return maxHp;
	}
	public void setMaxHp(Integer maxHp) {
		this.maxHp = maxHp;
	}
	public Integer getCurrentHp() {
		return currentHp;
	}
	public void setCurrentHp(Integer currentHp) {
		this.currentHp = currentHp;
	}
	public Integer getAc() {
		return ac;
	}
	public void setAc(Integer ac) {
		this.ac = ac;
	}
	public Integer getStr() {
		return str;
	}
	public void setStr(Integer str) {
		this.str = str;
	}
	public Integer getDex() {
		return dex;
	}
	public void setDex(Integer dex) {
		this.dex = dex;
	}
	public Integer getCon() {
		return con;
	}
	public void setCon(Integer con) {
		this.con = con;
	}
	public Integer getIntel() {
		return intel;
	}
	public void setIntel(Integer intel) {
		this.intel = intel;
	}
	public Integer getWis() {
		return wis;
	}
	public void setWis(Integer wis) {
		this.wis = wis;
	}
	public Integer getCha() {
		return cha;
	}
	public void setCha(Integer cha) {
		this.cha = cha;
	}
	@Override
	public String toString() {
	    return name + " AC: " + ac + " HP: " + currentHp + " / " + maxHp
	        + " STR: " + str + " DEX: " + dex + " CON: " + con
	        + " INT: " + intel + " WIS: " + wis + " CHA: " + cha
	       ;
	}

	public FoundryApiClientController getFoundryClient() {
		return foundryClient;
	}
	
	public class Skill {
	    private String ability;
	    private int value;

	    public Skill(String ability, int value) {
	        this.ability = ability;
	        this.value = value;
	    }

	    public String getAbility() {
	        return ability;
	    }

	    public int getValue() {
	        return value;
	    }

	    @Override
	    public String toString() {
	        return ability.toUpperCase() + ": " + value;
	    }
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	
}
