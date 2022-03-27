package com.planetgallium.kitpvp.util;

public class PlayerData {

    private int kills, deaths, experience, level;

    public PlayerData(int kills, int deaths, int experience, int level) {
        this.kills = kills;
        this.deaths = deaths;
        this.experience = experience;
        this.level = level;
    }

    public int getDataByIdentifier(String identifier) {
        return switch (identifier) {
            case "kills" -> getKills();
            case "deaths" -> getDeaths();
            case "experience" -> getExperience();
            case "level" -> getLevel();
            default -> -1;
        };
    }

    public void setDataByIdentifier(String identifier, int data) {
        switch (identifier) {
            case "kills" -> setKills(data);
            case "deaths" -> setDeaths(data);
            case "experience" -> setExperience(data);
            case "level" -> setLevel(data);
        }
    }

    private void setKills(int amount) { this.kills = amount; }

    private void setDeaths(int amount) { this.deaths = amount; }

    private void setExperience(int amount) { this.experience = amount; }

    private void setLevel(int amount) { this.level = amount; }

    private int getKills() { return kills; }

    private int getDeaths() { return deaths; }

    private int getExperience() { return experience; }

    private int getLevel() { return level; }

}
