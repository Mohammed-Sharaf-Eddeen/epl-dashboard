package com.example.epldashboard.repos;

import com.example.epldashboard.models.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamRepo extends CrudRepository<Team, String> {

    Optional<Team> findByTitleAndSeason(String title, String season);

    @Query("SELECT distinct t.season FROM Team t WHERE t.title = :teamTitle")
    List<String> findAllSeasonsForOneTeam(@Param("teamTitle") String teamTitle);

}
