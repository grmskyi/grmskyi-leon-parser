# Sports Match Data Parser

This project provides a set of tools to fetch, parse, and format sports match data from specified endpoints. It leverages Spring Boot for dependency injection and asynchronous processing, Jackson for JSON parsing, and custom logic to format the data for console output.

## Features

- Fetch sports match data asynchronously using Spring's `RestTemplate`.
- Parse JSON responses into Java objects with Jackson.
- Format and print match data, including markets and odds, to the console.

## Setup and Configuration

### Prerequisites

- JDK 21 or later
- Maven 3.6 or later (for building the project)

### Building the Project

1. Clone the repository:
   ```sh
   git clone https://github.com/grmskyi/grmskyi-leon-parser
2. Build the project with Maven:
   ```sh
   mvn clean install

### Configuration

The project requires configuration of endpoint URLs in application.properties:
* Application properties:
   ```sh
   leon.bet.link=<base-url>
   leon.bet.link.league.with.id=<url-to-fetch-league-data>
   leon.bet.link.match.with.id=<url-to-fetch-match-data>

### Usage
The project's main functionality revolves around fetching and parsing sports match data:

* Fetching Data: Data is fetched asynchronously from the configured endpoints.
* Parsing Data: Fetched data is parsed into structured objects (LeagueDTO, MatchDTO, etc.).
* Formatting and Output: Parsed data is formatted into a readable format and printed to the console.

To initiate the data fetching and parsing process, run the LeonParser.getBettingData() method, which orchestrates the fetching, parsing, and formatting of the data.

### Result

Below is the result of the image from the console with the data in the correct form:
![Screenshot from the console](https://github.com/grmskyi/grmskyi-leon-parser/blob/main/scr1.png)

I also attach a .txt file with all the data after validation:
![TXT file with data](https://github.com/grmskyi/grmskyi-leon-parser/blob/main/result.txt)
