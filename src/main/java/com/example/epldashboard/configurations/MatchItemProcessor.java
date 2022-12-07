package com.example.epldashboard.configurations;

import com.example.epldashboard.models.Match;
import org.springframework.batch.item.ItemProcessor;


public class MatchItemProcessor implements ItemProcessor<Match, Match> {

    @Override
    public Match process(Match match) throws Exception {
        // For actual business logic processing... However, if all what is required is just mapping and casting from
        // string values to suitable properties, this can be done in the FieldSetMapper

        // Here, no business processing is needed
        return match;
    }

}
