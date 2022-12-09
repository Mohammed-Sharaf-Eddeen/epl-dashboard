package com.example.epldashboard.models;

import javax.persistence.*;
import java.util.List;

@Entity
public class Team {
    @Id
    private String title;

    private int homeWins;
    private int homeLosses;
    private int homeDraws;

    private int awayWins;
    private int awayLosses;
    private int awayDraws;

    @Transient
    private List<Match> latestMatches;

    public Team() {
    }

    public Team(String title, int homeWins, int homeLosses, int homeDraws, int awayWins, int awayLosses, int awayDraws, List<Match> latestMatches) {
        this.title = title;
        this.homeWins = homeWins;
        this.homeLosses = homeLosses;
        this.homeDraws = homeDraws;
        this.awayWins = awayWins;
        this.awayLosses = awayLosses;
        this.awayDraws = awayDraws;
        this.latestMatches = latestMatches;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHomeWins() {
        return homeWins;
    }

    public void setHomeWins(int homeWins) {
        this.homeWins = homeWins;
    }

    public int getHomeLosses() {
        return homeLosses;
    }

    public void setHomeLosses(int homeLosses) {
        this.homeLosses = homeLosses;
    }

    public int getHomeDraws() {
        return homeDraws;
    }

    public void setHomeDraws(int homeDraws) {
        this.homeDraws = homeDraws;
    }

    public int getAwayWins() {
        return awayWins;
    }

    public void setAwayWins(int awayWins) {
        this.awayWins = awayWins;
    }

    public int getAwayLosses() {
        return awayLosses;
    }

    public void setAwayLosses(int awayLosses) {
        this.awayLosses = awayLosses;
    }

    public int getAwayDraws() {
        return awayDraws;
    }

    public void setAwayDraws(int awayDraws) {
        this.awayDraws = awayDraws;
    }

    @Override
    public String toString() {
        return "Team{" +
                "title='" + title + '\'' +
                ", homeWins=" + homeWins +
                ", homeLosses=" + homeLosses +
                ", homeDraws=" + homeDraws +
                ", awayWins=" + awayWins +
                ", awayLosses=" + awayLosses +
                ", awayDraws=" + awayDraws +
                '}';
    }

    public List<Match> getLatestMatches() {
        return latestMatches;
    }

    public void setLatestMatches(List<Match> latestMatches) {
        this.latestMatches = latestMatches;
    }
}

