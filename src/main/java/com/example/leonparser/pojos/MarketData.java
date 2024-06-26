package com.example.leonparser.pojos;

import lombok.Data;

import java.util.List;

@Data
public class MarketData {
    private String name;
    private String handicap;
    private List<RunnerData> runners;
}