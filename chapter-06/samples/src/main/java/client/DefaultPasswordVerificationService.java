package client;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.EXPECTATION_FAILED;

public class DefaultPasswordVerificationService
        implements PasswordVerificationService {

    final WebClient webClient;

    public DefaultPasswordVerificationService(WebClient.Builder webClientBuilder) {
        webClient = webClientBuilder
            .baseUrl("http://localhost:8080")// 2.1
            .build();
    }

    @Override
    public Mono<Void> check(String raw, String encoded) {// 3
        //ClientHttpConnector
        return webClient
            .post()// 1
            .uri("/password")// 3.1
            .body(BodyInserters.fromPublisher(// 3.2
                Mono.just(new PasswordDTO(raw, encoded)),
                PasswordDTO.class
            ))
            .exchange()// 3.3
            .flatMap(response -> {// 3.4
                if (response.statusCode().is2xxSuccessful()) {// 3.5
                    return Mono.empty();
                }
                else if (response.statusCode() == EXPECTATION_FAILED) {
                    return Mono.error(new BadCredentialsException("Invalid credentials"));// 3.6
                }
                return Mono.error(new IllegalStateException());
            });
    }
}
