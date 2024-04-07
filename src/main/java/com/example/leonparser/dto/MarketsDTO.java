package com.example.leonparser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MarketsDTO {
    private String name;
    private List<RunnerDTO> runnerDTOArrayList;
}