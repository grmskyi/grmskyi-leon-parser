package com.example.leonparser.parser;

import com.example.leonparser.dto.LeagueDTO;
import com.example.leonparser.dto.MarketsDTO;
import com.example.leonparser.dto.MatchDTO;
import com.example.leonparser.service.PageParseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeonParser {
    private final PageParseService parseService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private String formatMatchToString(MatchDTO match) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'");

        String header = String.format("%s, %s %s\n%s, %s, %d",
                match.getLeagueDTO().getSportType().getName(),
                match.getLeagueDTO().getRegion(),
                match.getLeagueDTO().getName(),
                match.getName(),
                simpleDateFormat.format(new Date(match.getKickoff())),
                match.getId());

        String markets = match.getMarketsDTOS().stream()
                .map(this::formatMarketToString)
                .collect(Collectors.joining("\n"));

        return header + "\n" + markets;
    }

    private String formatMarketToString(MarketsDTO market) {
        String marketHeader = "\t" + market.getName();

        String runners = market.getRunnerDTOArrayList().stream()
                .map(runner -> String.format("\t\t%s, %.2f, %d", runner.getTag(), runner.getPrice(), runner.getId()))
                .collect(Collectors.joining("\n"));

        return marketHeader + "\n" + runners;
    }

    public void getBettingData() {

        List<LeagueDTO> leagueDTOS = parseService.parseTopLeagueFromPage();

        List<Future<List<MatchDTO>>> futures = new ArrayList<>();

        for (LeagueDTO league : leagueDTOS) {
            Future<List<MatchDTO>> future = executorService.submit(() ->
                    parseService.parseFirstMatchesOfLeaguesFromPage(Collections.singletonList(league)));
            futures.add(future);
        }

        List<MatchDTO> matchDTOS = new ArrayList<>();

        for (Future<List<MatchDTO>> future : futures) {
            try {
                matchDTOS.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error retrieving match data", e);
                Thread.currentThread().interrupt();
            }
        }
        matchDTOS.forEach(match -> System.out.println(formatMatchToString(match)));
    }
}