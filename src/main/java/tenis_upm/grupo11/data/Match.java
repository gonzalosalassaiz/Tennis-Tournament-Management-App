package tenis_upm.grupo11.data;

import java.util.Map;
import javafx.util.Pair;

public class Match {
    private int id;
    private int tournamentId;
    private String firstPlayerUsername;
    private String secondPlayerUsername;
    private int round;
    private Map<Integer, Pair<Integer, Integer>> points;//Numero de Set, Juegos Jugador1, Juegos Jugador2

    // Constructor
    public Match(int id, int tournamentId, String firstPlayerId, String secondPlayerId, int round) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.firstPlayerUsername = firstPlayerId;
        this.secondPlayerUsername = secondPlayerId;
        this.round = round;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getFirstPlayerUsername() {
        return firstPlayerUsername;
    }

    public void setFirstPlayerUsername(String firstPlayerId) {
        this.firstPlayerUsername = firstPlayerId;
    }

    public String getSecondPlayerUsername() {
        return secondPlayerUsername;
    }

    public void setSecondPlayerUsername(String secondPlayerId) {
        this.secondPlayerUsername = secondPlayerId;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Map<Integer, Pair<Integer, Integer>> getPoints() {
        return points;
    }

    public void setPoints(Map<Integer, Pair<Integer, Integer>> points) {
        this.points = points;
    }
}