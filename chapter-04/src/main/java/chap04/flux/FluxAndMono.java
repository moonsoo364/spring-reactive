package chap04.flux;

import chap04.dto.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;

public class FluxAndMono {
    public void p_131(){
        Flux.range(1,5).repeat();
    }
    public void p_132(){
        Flux.range(1, 100)
                .repeat()// 무한 루프
                .collectList()
                .block();
    }
    public void p_134(){
        //Mono.from(Flux.from(mono))
    }
    //Flux, Mono 시퀀스 예제
    public void p_135(){
        //Flux
        Flux<String> stream1 = Flux.just("Hello", "World");
        Flux<Integer> stream2 = Flux.fromArray(new Integer[]{1, 2, 3});
        Flux<Integer> stream3 = Flux.fromIterable(Arrays.asList(9, 8, 7));
        Flux<Integer> stream4 = Flux.range(2010,9);// 2010, 2011 ... 2018

        //Mono
        Mono<String> stream5 = Mono.just("One");
        Mono<String> stream6 = Mono.justOrEmpty(null);
        Mono<String> stream7 = Mono.justOrEmpty(Optional.empty());
    }
    public void p_136_mono(){
        //Mono<String> stream8 = Mono.fromCallable(()-> httpRequest());
        
        //에러와 빈 스트림을 포함하는 스트림
        Flux<String> empty = Flux.empty();
        Flux<String> never = Flux.never();
        Mono<String> error = Mono.error(new RuntimeException("Unknown id"));
    }
    //p137
    // 사용자 요청을 받아서 Mono<User> 반환
    public Mono<User> requestUserData(String sessionId) {
        return Mono.defer(() -> {
            if (isValidSession(sessionId)) {
                return Mono.fromCallable(() -> requestUser(sessionId));
            } else {
                return Mono.error(new RuntimeException("Invalid user session"));
            }
        });
    }

    // 세션 유효성 검사 (null이 아닌지만 체크)
    private boolean isValidSession(String sessionId) {
        return sessionId != null && !sessionId.isEmpty();
    }

    // 실제 User 객체를 가져온다고 가정 (예: DB나 외부 API 호출)
    private User requestUser(String sessionId) {
        // 간단한 목업 데이터
        User user = new User();
        user.setUserId("user123");
        user.setSessionId(sessionId);
        return user;
    }

    public Mono<User> requestUserData2 (String sessionId){
        return isValidSession(sessionId)
                ? Mono.fromCallable(()-> requestUser(sessionId))
                : Mono.error(new RuntimeException("Invalid user session"));
    }

}
