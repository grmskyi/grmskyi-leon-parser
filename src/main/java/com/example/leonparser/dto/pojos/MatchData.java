package com.example.leonparser.dto.pojos;

import lombok.Data;

import java.util.List;

@Data
public class MatchData {
    private String name;
    private Long kickoff;
    private List<MarketData> markets;
}