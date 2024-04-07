package com.example.leonparser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RunnerDTO {
    private Long id;
    private String tag;
    private Double price;
}