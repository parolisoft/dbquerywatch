package com.parolisoft.dbquerywatch.application;

import com.parolisoft.dbquerywatch.internal.ClassIdRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static com.parolisoft.dbquerywatch.spring.SpringTestHelpers.addTraceHeaders;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Disabled("Expected to fail. Go to junit-platform.properties to re-enable all disabled tests at once.")
public class WebClientIntegrationTests extends BaseIntegrationTests {

    WebTestClient client;

    @AfterAll
    void verifyMetrics() {
        assertTrue(ClassIdRepository.getMdcHits() > 0);
        assertEquals(0, ClassIdRepository.getThreadLocalHits());
    }

    @BeforeEach
    void setupWebClient(@LocalServerPort int serverPort) {
        this.client = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + serverPort)
            .defaultHeaders(headers -> addTraceHeaders(headers, getClass()))
            .build();
    }

    @Test
    void should_find_article_by_author_last_name() {
        client.post()
            .uri("/articles/query")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new JSONObject(Map.of("author_last_name", "Parnas")).toString())
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].author_full_name").isEqualTo("David L. Parnas");
    }

    @Test
    void should_find_article_by_year_range() {
        client.post()
            .uri("/articles/query")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new JSONObject(Map.of("from_year", 1970, "to_year", 1980)).toString())
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody().json("[" +
                "{'author_last_name': 'Parnas'}, " +
                "{'author_last_name': 'Diffie-Hellman'}, " +
                "{'author_last_name': 'Lamport'}" +
                "]");
    }

    @Test
    void should_find_journal_by_publisher() {
        client.get()
            .uri("/journals/{publisher}", "ACM")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].name").isEqualTo("Communications of the ACM");
    }
}
