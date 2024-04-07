package com.example.leonparser.service;

import com.example.leonparser.dto.LeagueDTO;
import com.example.leonparser.dto.MatchDTO;

import java.util.List;

public interface PageParseService {
    List<LeagueDTO> parseTopLeagueFromPage();
    List<MatchDTO> parseFirstMatchesOfLeaguesFromPage(List<LeagueDTO> leagueDTOS);
}