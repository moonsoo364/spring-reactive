package client;

import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class WebClientTest {
    /**
     * tmdb의 openapi를  테스트 하기위한 메서드, api_key 발급 필요
     */
    @Test
    public void getMovie(){
        //search/movie : 영화 찾기
        //person/{person_id} : 인물 id로 찾기
        //search/company : 회사 찾기
        //search/person : 인물 request param 으로 찾기
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.themoviedb.org")
                .build();
        Mono<String> movieResponseMono = webClient.get().uri(uriBuilder -> uriBuilder
                .path("/3/person/500")
                .queryParam("query","Paramount")
                .queryParam("language","ko-KR")
                .queryParam("api_key", "")
                .build()
        )
        .retrieve()//응답을 받기 시작
                .bodyToMono(String.class);
        // 실제 테스트에서는 이 Mono를 구독하고 결과를 검증해야 합니다.
        // 예: movieResponseMono.subscribe(response -> System.out.println(response));
        // 또는 StepVerifier를 사용하여 테스트합니다.
//         StepVerifier.create(movieResponseMono)
//         .expectNextCount(1) // 예상되는 응답 수 (예시)
//         .verifyComplete();
        String response = movieResponseMono.block(); // 주의: block()은 테스트나 간단한 경우에만 사용
        System.out.println(response);


    }
}
