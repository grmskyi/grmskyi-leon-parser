package com.example.leonparser.pojos;

import lombok.Data;

import java.util.List;

@Data
public class Region {
    private String name;
    private List<League> leagues;
}