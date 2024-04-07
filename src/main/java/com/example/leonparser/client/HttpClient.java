package com.example.leonparser.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@AllArgsConstructor
public class HttpClient {

    private final RestTemplate restTemplate;
    private static final HttpEntity<String> HTTP_ENTITY = buildHttpEntity();

    public String sendRequest(String url) {
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.GET, HTTP_ENTITY, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Failed to fetch data from URL: {}", url);
            return null;
        }
        return response.getBody();
    }

    private static HttpEntity<String> buildHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.160 YaBrowser/22.5.4.904 Yowser/2.5 Safari/537.36");
        headers.set("Connection", "keep-alive");
        headers.set("Sec-Fetch-Dest", "document");
        headers.set("Host", "leon.bet");
        headers.set("Cache-Control", "max-age=0");
        headers.set("Accept-Language", "ru,en;q=0.9");
        headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        return new HttpEntity<>(headers);
    }
}