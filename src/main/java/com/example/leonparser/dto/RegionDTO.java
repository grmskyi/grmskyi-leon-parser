package com.example.leonparser.dto;

import lombok.Data;

import java.util.List;

@Data
public class RegionDTO {
    private Long id;
    private String name;

    private List<LeagueDTO> leagueDTOS;
}