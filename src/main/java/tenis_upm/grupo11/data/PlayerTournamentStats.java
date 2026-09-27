package tenis_upm.grupo11.data;

public class PlayerTournamentStats {
    private final String username;
    private final String tournamentFullName;
    private final int setWins;
    private final int gameWins;
    private final int gameLosses;
    private final int position;

    public PlayerTournamentStats(String username, String tournamentFullName, int setWins, int gameWins,
                                 int gameLosses, int position) {
        this.username = username;
        this.tournamentFullName = tournamentFullName;
        this.setWins = setWins;
        this.gameWins = gameWins;
        this.gameLosses = gameLosses;
        this.position = position;
    }

    public String getUsername() {
        return username;
    }

    public String getTournamentFullName() {
        return tournamentFullName;
    }

    public int getSetWins() {
        return setWins;
    }

    public int getGameWins() {
        return gameWins;
    }

    public int getGameLosses() {
        return gameLosses;
    }

    public int getPosition() {
        return position;
    }
}
