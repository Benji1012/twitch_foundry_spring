package fantazia_szoft.twitch_foundry_spring.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import fantazia_szoft.twitch_foundry_spring.controller.FoundryApiClientController;


public class Player {

	private String client_id = "";
	private String uuid = "";
	private String name = "";
	private String race = "";
	private String class_ = "";
	private Integer maxHp = 0;
	private Integer currentHp = 0;
	private Integer ac = 0;
	private Integer str = 0;
	private Integer dex = 0;
	private Integer con = 0;
	private Integer intel = 0;
	private Integer wis = 0;
	private Integer cha = 0;
	private Integer level_ = 0;
	private Integer acrobatics = 0;
	private Integer animalHandling = 0;
	private Integer arcana = 0;
	private Integer athletics = 0;
	private Integer deception = 0;
	private Integer history = 0;
	private Integer insight = 0;
	private Integer intimidation = 0;
	private Integer investigation = 0;
	private Integer medicine = 0;
	private Integer nature = 0;
	private Integer perception = 0;
	private Integer performance = 0;
	private Integer persuasion = 0;
	private Integer religion = 0;
	private Integer sleightOfHand = 0;
	private Integer stealth = 0;
	private Integer survival = 0;
	List<String> equippedItems = new ArrayList<>();

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
				//System.out.println(system.toString(2));
				if (system.has("skills") && !system.isNull("skills") && system.has("details") && !system.isNull("details")) {
				    JSONObject skillsJson = system.getJSONObject("skills");
				    int xp = 0;
				    if(system.getJSONObject("details").has("xp")) {
				    	 xp = system.getJSONObject("details").getJSONObject("xp").getInt("value");
				    }
					level_ = calculateLevel(xp);
					int proficiency = 2 + (level_ - 1) / 4;
					Map<String, Integer> abilityMap = Map.of(
						    "str", this.str,
						    "dex", this.dex,
						    "con", this.con,
						    "int", this.intel,
						    "wis", this.wis,
						    "cha", this.cha
						);

						for (String skillName : skillsJson.keySet()) {
						    JSONObject skillObj = skillsJson.getJSONObject(skillName);
						    String ability = skillObj.getString("ability");
						    double profLevel = skillObj.getDouble("value");  // 0, 0.5, 1.0, or 2.0
						    int baseMod = (int) Math.floor((abilityMap.getOrDefault(ability, 10) - 10) / 2.0);
						    int skillValue = (int) Math.floor(baseMod + (profLevel * proficiency));
						    if(skillName.equals("acr")){
						    	System.out.println("profLevel: "+profLevel);
						    	System.out.println("abilityMap: "+abilityMap.getOrDefault(ability, 10));
						    	System.out.println("baseMod: "+baseMod);
						    	System.out.println("proficiency: "+proficiency);
						    	System.out.println("skillValue: "+skillValue);
						    	System.out.println("profLevel: "+profLevel);
						    }

						    switch (skillName) {
						        case "acr": acrobatics = skillValue; break;
						        case "ani": animalHandling = skillValue; break;
						        case "arc": arcana = skillValue; break;
						        case "ath": athletics = skillValue; break;
						        case "dec": deception = skillValue; break;
						        case "his": history = skillValue; break;
						        case "ins": insight = skillValue; break;
						        case "itm": intimidation = skillValue; break;
						        case "inv": investigation = skillValue; break;
						        case "med": medicine = skillValue; break;
						        case "nat": nature = skillValue; break;
						        case "prc": perception = skillValue; break;
						        case "prf": performance = skillValue; break;
						        case "per": persuasion = skillValue; break;
						        case "rel": religion = skillValue; break;
						        case "slt": sleightOfHand = skillValue; break;
						        case "ste": stealth = skillValue; break;
						        case "sur": survival = skillValue; break;
						    }
						}
						String race_id = "";
						String class_id = "";
						if(system.getJSONObject("details").has("race") && !system.getJSONObject("details").isNull("race")) {
							 race_id = system.getJSONObject("details").getString("race");
						}
						if(system.getJSONObject("details").has("originalClass") && !system.getJSONObject("details").isNull("originalClass")) {
							 class_id = system.getJSONObject("details").getString("originalClass");
						}
						
						
						if(data.has("items") &&  !data.isNull("items")) {
							JSONArray items = data.getJSONArray("items");

							
							JSONObject raceItem = null;
							JSONObject classItem = null;

							for (int i = 0; i < items.length(); i++) {
							    JSONObject item = items.getJSONObject(i);
							    String itemId = item.getString("_id");

							    // Equipped items
							    if (item.has("system") && item.getJSONObject("system").optBoolean("equipped", false)) {
							        String itemName = item.optString("name", "Unnamed Item");
							        equippedItems.add(itemName);
							    }

							    // Match race or class
							    if (!race_id.equals("") && itemId.equals(race_id)) {
							        raceItem = item;
							    } else if (!class_id.equals("") && itemId.equals(class_id)) {
							        classItem = item;
							    }
							}

							// Output equipped items
//							System.out.println("Equipped Items:");
//							for (String item : equippedItems) {
//							    System.out.println("- " + item);
//							}

							// Optional: Show matched race/class item names
							if (raceItem != null) {
							    race = raceItem.optString("name");
							}
							if (classItem != null) {
							    class_ = classItem.optString("name");
							}
						}

				} else {
				    System.out.println("No 'skills' field found in system JSON.");
				}
				
				
				
				
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
	

	public FoundryApiClientController getFoundryClient() {
		return foundryClient;
	}
	

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	
	public static int calculateLevel(int xp) {
	    int[] xpThresholds = {
	        0, 300, 900, 2700, 6500, 14000, 23000, 34000, 48000,
	        64000, 85000, 100000, 120000, 140000, 165000, 195000,
	        225000, 265000, 305000, 355000
	    };
	    for (int i = xpThresholds.length - 1; i >= 0; i--) {
	        if (xp >= xpThresholds[i]) return i + 1;
	    }
	    return 1;
	}

	public Integer getLevel_() {
		return level_;
	}

	public void setLevel_(Integer level_) {
		this.level_ = level_;
	}

	public Integer getAcrobatics() {
		return acrobatics;
	}

	public void setAcrobatics(Integer acrobatics) {
		this.acrobatics = acrobatics;
	}

	public Integer getAnimalHandling() {
		return animalHandling;
	}

	public void setAnimalHandling(Integer animalHandling) {
		this.animalHandling = animalHandling;
	}

	public Integer getArcana() {
		return arcana;
	}

	public void setArcana(Integer arcana) {
		this.arcana = arcana;
	}

	public Integer getAthletics() {
		return athletics;
	}

	public void setAthletics(Integer athletics) {
		this.athletics = athletics;
	}

	public Integer getDeception() {
		return deception;
	}

	public void setDeception(Integer deception) {
		this.deception = deception;
	}

	public Integer getHistory() {
		return history;
	}

	public void setHistory(Integer history) {
		this.history = history;
	}

	public Integer getInsight() {
		return insight;
	}

	public void setInsight(Integer insight) {
		this.insight = insight;
	}

	public Integer getIntimidation() {
		return intimidation;
	}

	public void setIntimidation(Integer intimidation) {
		this.intimidation = intimidation;
	}

	public Integer getInvestigation() {
		return investigation;
	}

	public void setInvestigation(Integer investigation) {
		this.investigation = investigation;
	}

	public Integer getMedicine() {
		return medicine;
	}

	public void setMedicine(Integer medicine) {
		this.medicine = medicine;
	}

	public Integer getNature() {
		return nature;
	}

	public void setNature(Integer nature) {
		this.nature = nature;
	}

	public Integer getPerception() {
		return perception;
	}

	public void setPerception(Integer perception) {
		this.perception = perception;
	}

	public Integer getPerformance() {
		return performance;
	}

	public void setPerformance(Integer performance) {
		this.performance = performance;
	}

	public Integer getPersuasion() {
		return persuasion;
	}

	public void setPersuasion(Integer persuasion) {
		this.persuasion = persuasion;
	}

	public Integer getReligion() {
		return religion;
	}

	public void setReligion(Integer religion) {
		this.religion = religion;
	}

	public Integer getSleightOfHand() {
		return sleightOfHand;
	}

	public void setSleightOfHand(Integer sleightOfHand) {
		this.sleightOfHand = sleightOfHand;
	}

	public Integer getStealth() {
		return stealth;
	}

	public void setStealth(Integer stealth) {
		this.stealth = stealth;
	}

	public Integer getSurvival() {
		return survival;
	}

	public void setSurvival(Integer survival) {
		this.survival = survival;
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public String getClass_() {
		return class_;
	}

	public void setClass_(String class_) {
		this.class_ = class_;
	}

	public List<String> getEquippedItems() {
		return equippedItems;
	}

	public void setEquippedItems(List<String> equippedItems) {
		this.equippedItems = equippedItems;
	}

	@Override
	public String toString() {
		return "Player [client_id=" + client_id + ", uuid=" + uuid + ", name=" + name + ", race=" + race + ", class_="
				+ class_ + ", maxHp=" + maxHp + ", currentHp=" + currentHp + ", ac=" + ac + ", str=" + str + ", dex="
				+ dex + ", con=" + con + ", intel=" + intel + ", wis=" + wis + ", cha=" + cha + ", level_=" + level_
				+ ", acrobatics=" + acrobatics + ", animalHandling=" + animalHandling + ", arcana=" + arcana
				+ ", athletics=" + athletics + ", deception=" + deception + ", history=" + history + ", insight="
				+ insight + ", intimidation=" + intimidation + ", investigation=" + investigation + ", medicine="
				+ medicine + ", nature=" + nature + ", perception=" + perception + ", performance=" + performance
				+ ", persuasion=" + persuasion + ", religion=" + religion + ", sleightOfHand=" + sleightOfHand
				+ ", stealth=" + stealth + ", survival=" + survival + ", equippedItems=" + equippedItems
				+ ", foundryClient=" + foundryClient + ", getUuid()=" + getUuid() + ", getName()=" + getName()
				+ ", getMaxHp()=" + getMaxHp() + ", getCurrentHp()=" + getCurrentHp() + ", getAc()=" + getAc()
				+ ", getStr()=" + getStr() + ", getDex()=" + getDex() + ", getCon()=" + getCon() + ", getIntel()="
				+ getIntel() + ", getWis()=" + getWis() + ", getCha()=" + getCha() + ", getFoundryClient()="
				+ getFoundryClient() + ", getClient_id()=" + getClient_id() + ", getLevel_()=" + getLevel_()
				+ ", getAcrobatics()=" + getAcrobatics() + ", getAnimalHandling()=" + getAnimalHandling()
				+ ", getArcana()=" + getArcana() + ", getAthletics()=" + getAthletics() + ", getDeception()="
				+ getDeception() + ", getHistory()=" + getHistory() + ", getInsight()=" + getInsight()
				+ ", getIntimidation()=" + getIntimidation() + ", getInvestigation()=" + getInvestigation()
				+ ", getMedicine()=" + getMedicine() + ", getNature()=" + getNature() + ", getPerception()="
				+ getPerception() + ", getPerformance()=" + getPerformance() + ", getPersuasion()=" + getPersuasion()
				+ ", getReligion()=" + getReligion() + ", getSleightOfHand()=" + getSleightOfHand() + ", getStealth()="
				+ getStealth() + ", getSurvival()=" + getSurvival() + ", getRace()=" + getRace() + ", getClass_()="
				+ getClass_() + ", getEquippedItems()=" + getEquippedItems() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}
