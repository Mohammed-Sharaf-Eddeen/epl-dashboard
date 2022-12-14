package com.example.epldashboard.controllers;

import com.example.epldashboard.models.Match;
import com.example.epldashboard.models.Team;
import com.example.epldashboard.repos.MatchRepo;
import com.example.epldashboard.repos.TeamRepo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class TeamsController {

    private final TeamRepo teamRepo;
    private final MatchRepo matchRepo;

    public TeamsController(TeamRepo teamRepo, MatchRepo matchRepo) {
        this.teamRepo = teamRepo;
        this.matchRepo = matchRepo;
    }

    @GetMapping("/teams/{teamTitle}/{season}")
    public Team getTeamData(@PathVariable String teamTitle, @PathVariable String season) {
        Optional<Team> teamOptional = teamRepo.findByTitleAndSeason(teamTitle, season);
        if (teamOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team Not Found");
        }

        Team team = teamOptional.get();
        team.setLatestMatches(matchRepo.findTeamLatestMatches(teamTitle));

        return team;
    }

    @GetMapping("/matches/{teamTitle}/{season}")
    public List<Match> getAllMatchesForTeam(@PathVariable String teamTitle, @PathVariable String season) {
        return matchRepo.findTeamAllMatchesDuringSeason(teamTitle, season);
    }
}
