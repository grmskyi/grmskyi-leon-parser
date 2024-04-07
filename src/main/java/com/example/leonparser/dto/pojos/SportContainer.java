package com.example.leonparser.dto.pojos;

import lombok.Data;

import java.util.List;

@Data
public class SportContainer {
    private String name;
    private List<Region> regions;
}