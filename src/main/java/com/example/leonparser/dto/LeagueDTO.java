package com.example.leonparser.dto;

import com.example.leonparser.enums.Sports;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeagueDTO {
    private Long id;
    private String name;
    private String region;

    private Sports sportType;
}