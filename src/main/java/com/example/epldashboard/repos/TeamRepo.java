package com.example.epldashboard.repos;

import com.example.epldashboard.models.Team;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamRepo extends CrudRepository<Team, String> {

    @Override
    List<Team> findAll();

}
