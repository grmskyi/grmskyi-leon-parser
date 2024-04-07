package com.example.leonparser.service.impl;

import com.example.leonparser.client.HttpClient;
import com.example.leonparser.dto.LeagueDTO;
import com.example.leonparser.dto.MarketsDTO;
import com.example.leonparser.dto.MatchDTO;
import com.example.leonparser.dto.RunnerDTO;
import com.example.leonparser.dto.pojos.*;
import com.example.leonparser.enums.Sports;
import com.example.leonparser.service.PageParseService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageParseServiceImpl implements PageParseService {

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    @Value("${leon.bet.link}")
    private String baseUrl;

    @Value("${leon.bet.link.league.with.id}")
    private String urlWithLeagueId;

    @Value("${leon.bet.link.match.with.id}")
    private String urlWithMatchId;


    /**
     * Fetches and parses the top leagues from a predefined base URL. This method initiates an HTTP request
     * to retrieve league data in JSON format, deserializes the response, and then extracts top leagues
     * into a list of {@link LeagueDTO} objects based on certain criteria (e.g., matching sports of interest).
     *
     * @return A list of {@link LeagueDTO} objects representing the top leagues.
     * @SneakyThrows IOException If there's an issue with network communication or parsing the response.
     */
    @Override
    @SneakyThrows
    public List<LeagueDTO> parseTopLeagueFromPage() {
        String response = httpClient.sendRequest(baseUrl);
        List<SportContainer> sportContainers = objectMapper.readValue(response, new TypeReference<>() {
        });
        return extractTopLeagues(sportContainers);
    }


    /**
     * Extracts top leagues from a list of sport containers. Each sport container is examined to
     * determine if it matches a sport of interest, and then top leagues are extracted and converted
     * into {@link LeagueDTO} objects.
     *
     * @param sportContainers A list of {@link SportContainer} objects to be examined.
     * @return A list of {@link LeagueDTO} objects representing the top leagues within the given sport containers.
     */
    private List<LeagueDTO> extractTopLeagues(List<SportContainer> sportContainers) {
        return sportContainers.stream()
                .filter(this::isSportOfInterest)
                .flatMap(this::toTopLeaguesStream)
                .collect(Collectors.toList());
    }


    /**
     * Determines if a given sport container matches any of the sports of interest based on the sport name.
     *
     * @param container The {@link SportContainer} to check against the sports of interest.
     * @return {@code true} if the sport container's name matches any of the predefined sports of interest,
     * {@code false} otherwise.
     */
    private boolean isSportOfInterest(SportContainer container) {
        return Arrays.stream(Sports.values())
                .anyMatch(sportType -> sportType.getName().equals(container.getName()));
    }


    /**
     * Converts a given sport container into a stream of {@link LeagueDTO} objects representing top leagues.
     * This method examines all regions within the container, filtering for leagues marked as "top", and then
     * maps these to {@link LeagueDTO} objects.
     *
     * @param container The {@link SportContainer} to convert.
     * @return A stream of {@link LeagueDTO} objects representing the top leagues within the container.
     */
    private Stream<LeagueDTO> toTopLeaguesStream(SportContainer container) {
        Optional<Sports> optionalSport = Sports.getByName(container.getName());

        return container.getRegions().stream()
                .flatMap(region -> region.getLeagues().stream()
                        .filter(League::getTop)
                        .map(league -> {
                            Sports sport = optionalSport.orElseThrow(() -> new IllegalArgumentException("Sport not found"));
                            return new LeagueDTO(league.getId(), league.getName(), region.getName(), sport);
                        }));
    }


    /**
     * Parses the first match for each league provided in a list of {@link LeagueDTO} objects. This method
     * utilizes Java Streams to efficiently map each {@link LeagueDTO} to a {@link MatchDTO} by fetching
     * the first match for each league. The operation is performed in parallel to improve performance,
     * especially beneficial when the number of leagues is large or if fetching the match details is
     * time-consuming.
     *
     * @param leagueDTOS A list of {@link LeagueDTO} objects representing the leagues to fetch the first match for.
     * @return A list of {@link MatchDTO} objects, each representing the first match of the corresponding league.
     */
    @Override
    public List<MatchDTO> parseFirstMatchesOfLeaguesFromPage(List<LeagueDTO> leagueDTOS) {
        return leagueDTOS.stream()
                .map(this::parseFirstMatchOfLeagueFromPage)
                .collect(Collectors.toList());
    }


    /**
     * Parses the first match of a given league from a page's response. This method requests match data
     * for a specified league by its ID, deserializes the JSON response to fetch the first event,
     * and constructs a {@link MatchDTO} with detailed match information including the associated league.
     *
     * @param leagueDTO The {@link LeagueDTO} representing the league for which the first match is parsed.
     * @return A {@link MatchDTO} containing detailed information about the first match of the specified league.
     * @throws NoSuchElementException If no events are found in the response.
     * @SneakyThrows IOException If there is an error in fetching or processing the match data.
     */
    @SneakyThrows
    private MatchDTO parseFirstMatchOfLeagueFromPage(LeagueDTO leagueDTO) {
        String response = httpClient.sendRequest(urlWithLeagueId + leagueDTO.getId());
        EventsWrapper eventsWrapper = objectMapper.readValue(response, EventsWrapper.class);
        EventModule firstEventModule = eventsWrapper
                .getEvents()
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No events found"));

        MatchDTO matchDTO = parseMatchByIdFromPage(firstEventModule.getId());
        matchDTO.setLeagueDTO(leagueDTO);
        return matchDTO;
    }


    /**
     * Parses detailed information about a match by its ID, including market data and runners.
     * Utilizes Jackson for JSON deserialization and Java Streams for processing market and runner data.
     *
     * @param id The unique identifier for the match to parse.
     * @return A {@link MatchDTO} populated with match details including name, kickoff time, and markets.
     * @SneakyThrows IOException If there's an issue with network communication or JSON processing.
     */
    @SneakyThrows
    private MatchDTO parseMatchByIdFromPage(Long id) {
        String response = httpClient.sendRequest(urlWithMatchId + id);
        MatchData matchData = objectMapper.readValue(response, MatchData.class);
        List<MarketsDTO> marketList = processMarkets(matchData.getMarkets());
        return new MatchDTO(id, matchData.getName(), matchData.getKickoff(), marketList);
    }


    /**
     * Converts a list of market data into a list of MarketsDTO objects, including processing of runners.
     *
     * @param markets A list of market data extracted from the match data.
     * @return A list of {@link MarketsDTO}, each representing a market and its runners.
     */
    private List<MarketsDTO> processMarkets(List<MarketData> markets) {
        return Optional.ofNullable(markets)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(this::buildMarketDTO)
                .collect(Collectors.toCollection(ArrayList::new));
    }


    /**
     * Builds a MarketsDTO from MarketData, including the aggregation of runner data.
     *
     * @param market The market data to convert into a DTO.
     * @return A {@link MarketsDTO} object representing the market and its runners.
     */
    private MarketsDTO buildMarketDTO(MarketData market) {
        String marketName = market.getName() + (market.getHandicap() != null ? " " + market.getHandicap() : "");

        ArrayList<RunnerDTO> runners = market.getRunners().stream()
                .map(runner -> new RunnerDTO(runner.getId(), runner.getName(), runner.getPrice()))
                .collect(Collectors.toCollection(ArrayList::new));

        return new MarketsDTO(marketName, runners);
    }
}