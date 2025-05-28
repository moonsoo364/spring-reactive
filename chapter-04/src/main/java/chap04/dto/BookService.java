package chap04.dto;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

@Slf4j
public class BookService {
    static final Random random = new Random();

    public static Flux<String> recommendBooks(String userId){
        return Flux.defer(()->{
            if(random.nextInt(10)<7){// 1
                return Flux.<String>error(new RuntimeException("Error"))// 2
                        .delaySequence(Duration.ofMillis(100));
            }else {
                return Flux.just("Blue Mars", "The Expanse")// 3
                        .delayElements(Duration.ofMillis(50));
            }
        }).doOnSubscribe(s->log.info("Request for {}",userId));// 4
    }
}
