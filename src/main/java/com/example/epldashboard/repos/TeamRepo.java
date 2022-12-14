package com.example.epldashboard.repos;

import com.example.epldashboard.models.Team;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepo extends CrudRepository<Team, String> {

    Optional<Team> findByTitleAndSeason(String title, String season);
}
