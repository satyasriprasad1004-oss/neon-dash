package com.neondash.game.models;

public class LeaderboardEntry {
    private long id;
    private String playerName;
    private int score;
    private int distance;
    private int coins;
    private String characterName;
    private String gameMode;
    private String dateString;

    public LeaderboardEntry(long id, String playerName, int score, int distance,
                            int coins, String characterName, String gameMode, String dateString) {
        this.id = id;
        this.playerName = playerName;
        this.score = score;
        this.distance = distance;
        this.coins = coins;
        this.characterName = characterName;
        this.gameMode = gameMode;
        this.dateString = dateString;
    }

    public long getId() { return id; }
    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public int getDistance() { return distance; }
    public int getCoins() { return coins; }
    public String getCharacterName() { return characterName; }
    public String getGameMode() { return gameMode; }
    public String getDateString() { return dateString; }
}
