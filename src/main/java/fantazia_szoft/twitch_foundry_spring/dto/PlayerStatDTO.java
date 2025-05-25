package fantazia_szoft.twitch_foundry_spring.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fantazia_szoft.twitch_foundry_spring.model.Player;

public class PlayerStatDTO {
    private String name;
    private int currentHp;
    private int maxHp;
    private int ac;
    private int str;
    private int dex;
    private int con;
    private int intel;
    private int wis;
    private int cha;
    private int acrobatics;
	private int animalHandling;
	private int arcana;
	private int athletics;
	private int deception;
	private int history;
	private int insight;
	private int intimidation;
	private int investigation;
	private int medicine;
	private int nature;
	private int perception;
	private int performance;
	private int persuasion;
	private int religion;
	private int sleightOfHand;
	private int stealth;
	private int survival;
	private Integer level_ ;
	List<String> equippedItems = new ArrayList<>();
	private String race = "";
	private String class_ = "";

    public PlayerStatDTO(Player player) {
        this.name = player.getName();
       this.level_ = player.getLevel_();
        this.currentHp = player.getCurrentHp();
        this.maxHp = player.getMaxHp();
        this.ac = player.getAc();
        this.str = player.getStr();
        this.dex = player.getDex();
        this.con = player.getCon();
        this.intel = player.getIntel();
        this.wis = player.getWis();
        this.cha = player.getCha();
        this.acrobatics = player.getAcrobatics();
        this.animalHandling = player.getAnimalHandling();
        this.arcana = player.getArcana();
        this.athletics = player.getAthletics();
        this.deception = player.getDeception();
        this.history = player.getHistory();
        this.insight = player.getInsight();
        this.intimidation = player.getIntimidation();
        this.investigation = player.getInvestigation();
        this.medicine = player.getMedicine();
        this.nature = player.getNature();
        this.perception = player.getPerception();
        this.performance = player.getPerformance();
        this.persuasion = player.getPersuasion();
        this.religion = player.getReligion();
        this.sleightOfHand = player.getSleightOfHand();
        this.stealth = player.getStealth();
        this.survival = player.getSurvival();
        this.race = player.getRace();
        this.class_ = player.getClass_();
        this.equippedItems = player.getEquippedItems();
      
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCurrentHp() {
		return currentHp;
	}

	public void setCurrentHp(int currentHp) {
		this.currentHp = currentHp;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public int getAc() {
		return ac;
	}

	public void setAc(int ac) {
		this.ac = ac;
	}

	public int getStr() {
		return str;
	}

	public void setStr(int str) {
		this.str = str;
	}

	public int getDex() {
		return dex;
	}

	public void setDex(int dex) {
		this.dex = dex;
	}

	public int getCon() {
		return con;
	}

	public void setCon(int con) {
		this.con = con;
	}

	public int getIntel() {
		return intel;
	}

	public void setIntel(int intel) {
		this.intel = intel;
	}

	public int getWis() {
		return wis;
	}

	public void setWis(int wis) {
		this.wis = wis;
	}

	public int getCha() {
		return cha;
	}

	public void setCha(int cha) {
		this.cha = cha;
	}

	public int getAcrobatics() {
		return acrobatics;
	}

	public void setAcrobatics(int acrobatics) {
		this.acrobatics = acrobatics;
	}

	public int getAnimalHandling() {
		return animalHandling;
	}

	public void setAnimalHandling(int animalHandling) {
		this.animalHandling = animalHandling;
	}

	public int getArcana() {
		return arcana;
	}

	public void setArcana(int arcana) {
		this.arcana = arcana;
	}

	public int getAthletics() {
		return athletics;
	}

	public void setAthletics(int athletics) {
		this.athletics = athletics;
	}

	public int getDeception() {
		return deception;
	}

	public void setDeception(int deception) {
		this.deception = deception;
	}

	public int getHistory() {
		return history;
	}

	public void setHistory(int history) {
		this.history = history;
	}

	public int getInsight() {
		return insight;
	}

	public void setInsight(int insight) {
		this.insight = insight;
	}

	public int getIntimidation() {
		return intimidation;
	}

	public void setIntimidation(int intimidation) {
		this.intimidation = intimidation;
	}

	public int getInvestigation() {
		return investigation;
	}

	public void setInvestigation(int investigation) {
		this.investigation = investigation;
	}

	public int getMedicine() {
		return medicine;
	}

	public void setMedicine(int medicine) {
		this.medicine = medicine;
	}

	public int getNature() {
		return nature;
	}

	public void setNature(int nature) {
		this.nature = nature;
	}

	public int getPerception() {
		return perception;
	}

	public void setPerception(int perception) {
		this.perception = perception;
	}

	public int getPerformance() {
		return performance;
	}

	public void setPerformance(int performance) {
		this.performance = performance;
	}

	public int getPersuasion() {
		return persuasion;
	}

	public void setPersuasion(int persuasion) {
		this.persuasion = persuasion;
	}

	public int getReligion() {
		return religion;
	}

	public void setReligion(int religion) {
		this.religion = religion;
	}

	public int getSleightOfHand() {
		return sleightOfHand;
	}

	public void setSleightOfHand(int sleightOfHand) {
		this.sleightOfHand = sleightOfHand;
	}

	public int getStealth() {
		return stealth;
	}

	public void setStealth(int stealth) {
		this.stealth = stealth;
	}

	public int getSurvival() {
		return survival;
	}

	public void setSurvival(int survival) {
		this.survival = survival;
	}

	public Integer getLevel_() {
		return level_;
	}

	public void setLevel_(Integer level_) {
		this.level_ = level_;
	}

	public List<String> getEquippedItems() {
		return equippedItems;
	}

	public void setEquippedItems(List<String> equippedItems) {
		this.equippedItems = equippedItems;
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

    // Getters and setters here
}
