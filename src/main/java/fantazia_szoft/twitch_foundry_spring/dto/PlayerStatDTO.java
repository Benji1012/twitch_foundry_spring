package fantazia_szoft.twitch_foundry_spring.dto;

import java.util.Map;

import fantazia_szoft.twitch_foundry_spring.model.Player;
import fantazia_szoft.twitch_foundry_spring.model.Player.Skill;

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

    public PlayerStatDTO(Player player) {
        this.name = player.getName();
        this.currentHp = player.getCurrentHp();
        this.maxHp = player.getMaxHp();
        this.ac = player.getAc();
        this.str = player.getStr();
        this.dex = player.getDex();
        this.con = player.getCon();
        this.intel = player.getIntel();
        this.wis = player.getWis();
        this.cha = player.getCha();
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

    // Getters and setters here
}
