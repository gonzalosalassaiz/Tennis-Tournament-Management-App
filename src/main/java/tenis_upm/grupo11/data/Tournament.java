package tenis_upm.grupo11.data;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public class Tournament {
    private int id;
    private String name;
    private int year;
    private Date deadline;
    private int numberOfPlayers;
    private boolean started;
    private int actualRound;
    private List<Match> listOfMatches;
    private Map<String, User> listOfPlayers;
    private Map<String,User> promotedPlayers;
    private Map<String,User> inscriptions;

    // Constructor
    public Tournament(int id, String name, int year, Date deadline, boolean started, int actualRound) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.deadline = deadline;
        this.started = started;
        this.actualRound = actualRound;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }
    public boolean isActive() {
    	return started;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public int getActualRound() {
        return actualRound;
    }

    public void setActualRound(int actualRound) {
        this.actualRound = actualRound;
    }

    public List<Match> getListOfMatches() {
        return listOfMatches;
    }

    public void setListOfMatches(List<Match> listOfMatches) {
        this.listOfMatches = listOfMatches;
    }

	public Map<String, User> getListOfPlayers() {
		return listOfPlayers;
	}

	public void setListOfPlayers(Map<String, User> listOfPlayers) {
		this.listOfPlayers = listOfPlayers;
	}

	public Map<String, User> getPromotedPlayers() {
		return promotedPlayers;
	}

	public void setPromotedPlayers(Map<String, User> promotedPlayers) {
		this.promotedPlayers = promotedPlayers;
	}

	public Map<String, User> getInscriptions() {
		return inscriptions;
	}

	public void setInscriptions(Map<String, User> inscriptions) {
		this.inscriptions = inscriptions;
	}

    public String getFullName() {
        return name + " " + year;
    }
//UNUSED
    public boolean canAdvanceRound() {
        //TODO: Add final logic, this is temporal
        return listOfMatches != null && !listOfMatches.isEmpty();
    }
}
