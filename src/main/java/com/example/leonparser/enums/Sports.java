package com.example.leonparser.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;


@Getter
public enum Sports {
    FOOTBALL("Футбол"),
    HOCKEY("Хоккей"),
    TENNIS("Теннис"),
    BASKETBALL("Баскетбол");

    private final String sportName;

    Sports(String sportName) {
        this.sportName = sportName;
    }
    public String getName() {
        return sportName;
    }

    public static Optional<Sports> getByName(String name) {
        return Arrays.stream(values())
                .filter(sportType -> sportType.getName().equals(name))
                .findFirst();
    }
}