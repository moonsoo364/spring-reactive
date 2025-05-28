package chap04.dto;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

@Slf4j
public class Transaction {
    private static final Random random = new Random();
    private final int id;

    public Transaction(int id) {
        this.id = id;
        log.info("[T: {} created]",id);
    }
    public static Mono<Transaction> beginTransaction(){// 1
        return Mono.defer(()->
            Mono.just(new Transaction(random.nextInt(1000))));
    }
    public Flux<String> insertRows(Publisher<String> rows){// 2
        return Flux.from(rows)
                .delayElements(Duration.ofMillis(100))
                .flatMap(r->{
                    if(random.nextInt(10) <2){
                        return Mono.error(new RuntimeException("Error :"+r));
                    }else{
                        return Mono.just(r);
                    }
                });
    }
    public Mono<Void> commit(){// 3
        return Mono.defer(()->{
            log.info("[T: {}] commit",id);
            if(random.nextBoolean()){
                return Mono.empty();
            }else{
                return Mono.error(new RuntimeException("Conflict"));
            }
        });
    }
    public Mono<Void> rollback(){// 4
        return Mono.defer(()->{
           log.info("[T: {}] rollback", id);
           if(random.nextBoolean()){
               return Mono.empty();
           }else{
               return Mono.error(new RuntimeException("Conn Error"));
           }
        });
    }
}
