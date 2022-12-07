package com.example.epldashboard.configurations;

import com.example.epldashboard.models.Match;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDate;

public class MatchFieldSetMapper implements org.springframework.batch.item.file.mapping.FieldSetMapper<Match> {
    @Override
    public Match mapFieldSet(FieldSet fieldSet) throws BindException {
        Match match = new Match();

        // Map each token to its respective field in the Match object
        match.setDate(getDateFromString(fieldSet.readString(1)));
        match.setTime(fieldSet.readString(2));
        match.setHomeTeam(fieldSet.readString(3));
        match.setAwayTeam(fieldSet.readString(4));
        match.setHomeGoals(fieldSet.readInt(5));
        match.setAwayGoals(fieldSet.readInt(6));
        match.setReferee(fieldSet.readString(11));

        return match;
    }

    private LocalDate getDateFromString(String date) {
        // 12/9/2020
        String[] splitDate = date.split("/");
        int year = Integer.parseInt(splitDate[2]);
        int month = Integer.parseInt(splitDate[1]);
        int day = Integer.parseInt(splitDate[0]);
        return LocalDate.of(year, month, day);
    }

}
