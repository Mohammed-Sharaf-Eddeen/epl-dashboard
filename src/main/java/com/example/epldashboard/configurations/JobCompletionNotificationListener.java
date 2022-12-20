package com.example.epldashboard.configurations;

import com.example.epldashboard.models.Match;
import com.example.epldashboard.models.Team;
import com.example.epldashboard.repos.MatchRepo;
import com.example.epldashboard.repos.TeamRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final MatchRepo matchRepo;
    private final TeamRepo teamRepo;

    public JobCompletionNotificationListener(MatchRepo matchRepo, TeamRepo teamRepo) {
        this.matchRepo = matchRepo;
        this.teamRepo = teamRepo;
    }


    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("*** Importing Matches FINISHED ***");

            List<String> teamsTitles = matchRepo.findAllTeamTitles();
            List<String> seasons = matchRepo.findAllSeasonsForAllTeams();
            seasons.forEach(
                    season -> teamsTitles.forEach(
                            teamTitle -> populateTeamsTable(teamTitle, season)
                    )
            );

            log.info("*** Generating Teams Data FINISHED ***");
        }
    }

    private void populateTeamsTable(String teamTitle, String season) {
        Team team = new Team();
        team.setTitle(teamTitle);
        team.setSeason(season);

        // Check to see whether this team's matches exist. If they don't exist, it means the team has been delegated.
        if (matchRepo.findAllByHomeTeamAndSeason(teamTitle, season).isEmpty()) {
            return;
        }

        // Home Matches
        List<Match> homeMatches = matchRepo.findAllByHomeTeamAndSeason(teamTitle, season);
        int homeWins = (int) homeMatches.stream().filter(match -> match.getHomeGoals() > match.getAwayGoals()).count();
        int homeLosses = (int) homeMatches.stream().filter(match -> match.getHomeGoals() < match.getAwayGoals()).count();
        int homeDraws = (int) homeMatches.stream().filter(match -> match.getHomeGoals() == match.getAwayGoals()).count();

        team.setHomeWins(homeWins);
        team.setHomeLosses(homeLosses);
        team.setHomeDraws(homeDraws);

        // Away Matches
        List<Match> awayMatches = matchRepo.findAllByAwayTeamAndSeason(teamTitle, season);
        int awayWins = (int) awayMatches.stream().filter(match -> match.getAwayGoals() > match.getHomeGoals()).count();
        int awayLosses = (int) awayMatches.stream().filter(match -> match.getAwayGoals() < match.getHomeGoals()).count();
        int awayDraws = (int) awayMatches.stream().filter(match -> match.getAwayGoals() == match.getHomeGoals()).count();

        team.setAwayWins(awayWins);
        team.setAwayLosses(awayLosses);
        team.setAwayDraws(awayDraws);

        teamRepo.save(team);
    }
}
