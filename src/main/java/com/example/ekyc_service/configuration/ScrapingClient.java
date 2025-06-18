package com.example.ekyc_service.configuration;

import com.example.ekyc_service.exception.ServerException;
import com.example.ekyc_service.model.request.AddUserInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import io.netty.channel.ChannelOption;

import java.time.Duration;


/**
 * ScrapingClient is responsible for communicating with the external scraping microservice
 * using a non-blocking, reactive WebClient.
 * <p>
 * This component handles connection timeouts, exception translation, and maps response statuses
 * from the scraping service into appropriate application-level exceptions.
 * </p>
 *
 * Example usage:
 * <pre>
 *     scrapingClient.fetchScrapedUserData(request).block();
 * </pre>
 *
 * @author Jannath
 */
@Slf4j
@Component
public class ScrapingClient {

    private final WebClient webClient;
    private final Duration timeout;
    @Value("${custom.kyc-url}")
    private String KYC_URL;

    public ScrapingClient(@Value("${scraping.service.timeout:30}") int timeoutSeconds) {
        this.timeout = Duration.ofSeconds(timeoutSeconds);
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configure -> configure.defaultCodecs().maxInMemorySize(1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutSeconds * 1000)
                                .responseTimeout(timeout)
                ))
                .build();
    }


    /**
     * Fetch scraped user data from external scraping service.
     * @param request AddUserInfoRequest payload.
     * @return Mono of response as String.
     */
    public Mono<String> fetchScrapedUserData(AddUserInfoRequest request) {
        return webClient.post()
                .uri(KYC_URL+"/api/v1/scrap/add")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                .bodyToMono(String.class)
                .timeout(timeout)
                .doOnSuccess(result -> log.debug("Successfully scraped user data"))
                .doOnError(error -> log.error("Failed to scrape user data: {}", error.getMessage()))
                .onErrorMap(this::mapException);
    }

    private Mono<Throwable> handleClientError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("Client error")
                .flatMap(body -> Mono.error(new ServerException(
                        "Client error: " + body, mapToHttpStatus(response.statusCode()))));
    }

    private Mono<Throwable> handleServerError(ClientResponse response) {
        return Mono.error(new ServerException(
                "Scraping service error: " + response.statusCode(),
                mapToHttpStatus(response.statusCode())));
    }


    private Throwable mapException(Throwable ex) {
        if (ex instanceof WebClientRequestException) {
            log.error("Connection error to scraping service: {}", ex.getMessage());
            return new ServerException("Unable to connect to scraping service", HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (ex instanceof WebClientResponseException wcex) {
            log.error("HTTP error from scraping service: {}", wcex.getStatusCode());
            return new ServerException("Scraping service error", (HttpStatus) wcex.getStatusCode());
        }
        if (ex instanceof ServerException) {
            return ex;
        }
        log.error("Unexpected error calling scraping service", ex);
        return new ServerException("Failed to contact scraping service", HttpStatus.BAD_GATEWAY);
    }

    /**
     * Safely maps {@link HttpStatusCode} to {@link HttpStatus}.
     * Falls back to {@link HttpStatus#INTERNAL_SERVER_ERROR} if status cannot be resolved.
     *
     * @param statusCode the incoming status code
     * @return resolved HttpStatus
     */
    private HttpStatus mapToHttpStatus(HttpStatusCode statusCode) {
        if (statusCode instanceof HttpStatus) {
            return (HttpStatus) statusCode;
        }
        return HttpStatus.resolve(statusCode.value()) != null ? HttpStatus.resolve(statusCode.value()) : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}


