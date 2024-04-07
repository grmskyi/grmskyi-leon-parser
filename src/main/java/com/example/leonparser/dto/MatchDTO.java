package com.example.leonparser.dto;

import lombok.Data;

import java.util.List;

@Data
public class MatchDTO {
    private Long id;
    private String name;
    private Long kickoff;

    private LeagueDTO leagueDTO;
    private List<MarketsDTO> marketsDTOS;

    public MatchDTO(Long id, String name, Long kickoff, List<MarketsDTO> markets) {
        this.id = id;
        this.name = name;
        this.kickoff = kickoff;
        this.marketsDTOS = markets;
    }
}