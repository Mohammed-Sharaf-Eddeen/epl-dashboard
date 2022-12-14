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

    List<Match> findAllByHomeTeamAndSeason(String teamTitle, String season);

    List<Match> findAllByAwayTeamAndSeason(String teamTitle, String season);

    List<Match> findByHomeTeamOrAwayTeamOrderByDateDesc(String homeTeam, String awayTeam, Pageable pageable);

    List<Match> findByHomeTeamAndSeasonOrAwayTeamAndSeasonOrderByDateDesc(
            String homeTeam, String season1, String awayTeam, String season2, Pageable pageable);




    @Query("SELECT DISTINCT m.homeTeam FROM Match m")
    List<String> findAllTeamTitles();

    @Query("SELECT DISTINCT m.season FROM Match m")
    List<String> findAllSeasons();

    /*
        Previously, this was used to be done in a Data Access Object (DAO), but now, it is better to be done here
        in the interface after Java allowed default methods in interfaces definitions.
     */
    default List<Match> findTeamLatestMatches(String teamTitle) {
        Pageable pageable = PageRequest.of(0, 4);
        return findByHomeTeamOrAwayTeamOrderByDateDesc(teamTitle, teamTitle, pageable);
    }

    default List<Match> findTeamAllMatchesDuringSeason(String teamTitle, String season) {
        return findByHomeTeamAndSeasonOrAwayTeamAndSeasonOrderByDateDesc(teamTitle, season, teamTitle, season, Pageable.unpaged());
    }
}
