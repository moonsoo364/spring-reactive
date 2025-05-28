package chap04;

import chap04.dto.BookService;
import chap04.dto.Connection;
import chap04.dto.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
public class MakeStreamByCodeTest {
    @Test
    public void p_162_1(){
        Flux.push(emitter-> IntStream.range(2000, 3000)//1
                .forEach(emitter::next))//1.1
                .delayElements(Duration.ofMillis(1))//2
                .subscribe(e -> log.info("onNext: {}",e));//3
    }

    @Test
    public void p_162_2(){
        Flux.create(emitter->{
            emitter.onDispose(() -> log.info("Disposed"));
        }).subscribe(e->log.info("onNext : {}",e));
    }
    @Test
    public void p_163() throws InterruptedException {
        Flux.generate(//1
                () -> Tuples.of(0L,1L),//1.1
                (state, sink) ->{
                    log.info("generated value : {}", state.getT2());
                    sink.next(state.getT2());
                    long newValue = state.getT1() + state.getT2();//1.2
                    return Tuples.of(state.getT2(),newValue);//1.3
                })
                .take(7)//3
                .delayElements(Duration.ofMillis(1))//2
                .subscribe(e -> log.info("onNext: {}", e));//4

        Thread.sleep(100); // 충분한 시간 대기
    }

    @Test
    public void p_163_1() {
        Flux.generate(
                        () -> Tuples.of(0L, 1L),
                        (state, sink) -> {
                            log.info("generated value : {}", state.getT2());
                            sink.next(state.getT2());
                            long newValue = state.getT1() + state.getT2();
                            return Tuples.of(state.getT2(), newValue);
                        }
                )
                .take(7)
                .doOnNext(e -> {
                    log.info("onNext: {}", e);
                })
                .blockLast(); // 동기적으로 끝날 때까지 블로킹
    }

    @Test
    public void p_165_1() {
        try(Connection conn = Connection.newConnection()){// 1
            conn.getData().forEach(// 2
                    data -> log.info("Received data: {}",data)
            );
        }catch (Exception e){// 3
            log.info("error : {}",e.getMessage());
        }
    }
    @Test
    public void p_165_2(){
        Flux<String> ioRequestResults = Flux.using(// 1
                Connection::newConnection,// 1.1
                connection -> Flux.fromIterable(connection.getData()),// 1.2
                Connection::close// 1.3
        );

        ioRequestResults.subscribe(// 2
            data -> log.info("Recieved data: {}", data),
                e -> log.info("Error : {}",e.getMessage()),
                () -> log.info("Stream finished")
        );
    }
    @Test
    public void p_168() throws InterruptedException {
        Flux.usingWhen(
                Transaction.beginTransaction(), // 1
                transaction -> transaction.insertRows(Flux.just("A","B","C")), //2
                Transaction::commit,//3
                Transaction::rollback//4
        ).subscribe(
                d -> log.info("onNext: {}", d),
                e->log.info("orError: {}", e.getMessage()),
                () -> log.info("onComplete")
        );

        Thread.sleep(3000); // 충분한 대기 시간 (딜레이와 트랜잭션 처리 시간 포함)
    }
    @Test
    public void p_170() throws InterruptedException {
        Flux.just("user-1")// 1
                .flatMap(user ->//2
                                BookService.recommendBooks(user)//2.1
                                        .retryBackoff(5, Duration.ofMillis(100))// 2.2
                                        .timeout(Duration.ofSeconds(3))// 2.3
                                        .onErrorResume(e-> Flux.just("The Martian"))// 2.4
                        ).subscribe(// 3
                                b -> log.info("onNext : {}", b),
                                e->log.info("onError : {}", e.getMessage()),
                        () -> log.info("onComplete")
                );

        Thread.sleep(3000);
    }
    @Test
    public void p_173(){
        Flux<String> coldPublisher = Flux.defer(() ->{
            log.info("Generating new items");
            return Flux.just(UUID.randomUUID().toString());
        });

        log.info("No data was generated so far");
        coldPublisher.subscribe(e-> log.info("onNext : {}", e));
        coldPublisher.subscribe(e-> log.info("onNext : {}", e));
        log.info("Data was generated twice for two subscribers");
    }


}
