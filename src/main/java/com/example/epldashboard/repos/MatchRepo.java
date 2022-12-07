package com.example.epldashboard.repos;

import com.example.epldashboard.models.Match;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MatchRepo extends CrudRepository<Match, Long> {

    @Override
    List<Match> findAll();

    List<Match> findAllByHomeTeam(String teamTitle);

    List<Match> findAllByAwayTeam(String teamTitle);

    @Query("SELECT DISTINCT m.homeTeam FROM Match m")
    List<String> findAllTeamTitles();

    List<Match> findByHomeTeamOrAwayTeamOrderByDateDesc(String homeTeam, String awayTeam, Pageable pageable);

    /*
        Previously, this was used to be done in a Data Access Object (DAO), but now, it is better to be done here
        in the interface after Java allowed default methods in interfaces definitions.
     */
    default List<Match> findTeamLatestMatches(String teamTitle) {
        Pageable pageable = PageRequest.of(0, 4);
        return findByHomeTeamOrAwayTeamOrderByDateDesc(teamTitle, teamTitle, pageable);
    }

    default List<Match> findTeamAllMatches(String teamTitle) {
        // 19 is all matches per season
        Pageable pageable = PageRequest.of(0, 19);
        return findByHomeTeamOrAwayTeamOrderByDateDesc(teamTitle, teamTitle, pageable);
    }
}
