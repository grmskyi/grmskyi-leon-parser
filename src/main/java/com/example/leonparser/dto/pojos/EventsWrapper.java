package com.example.leonparser.dto.pojos;

import lombok.Data;

import java.util.List;

@Data
public class EventsWrapper {
    private List<EventModule> events;
}