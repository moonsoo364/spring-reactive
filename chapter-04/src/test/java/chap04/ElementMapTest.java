package chap04;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ElementMapTest {

    @Test
    public void p_145(){
        Flux
            .range(2018,5) //1
                .timestamp()//2
            .index() //3
            .subscribe(e -> log.info("index : {}, ts: {}, value: {}",//4
                    e.getT1(),//4.1
                    Instant.ofEpochMilli(e.getT2().getT1()),//4.2
                    e.getT2().getT2()//4.3
            ));
    }
    @Test
    public void p_147(){
        Flux.just(1,6,2,8,3,1,5,1)
                .collectSortedList(Comparator.reverseOrder())
                .subscribe(System.out::println);
    }

    @Test
    public void p_149(){
        Flux.just(3,5,7,9,11,15,16,17)
                .any(e->e%2==0)
                .subscribe(hasEvens -> log.info("Has evens: {}", hasEvens));
    }

    @Test
    public void p_150_1(){
        Flux.range(1, 5)
                .reduce(0,(acc,elem)-> acc+elem)
                .subscribe(result-> log.info("Result: {}",result));
    }
    @Test
    public void p_150_2(){
        Flux.range(1,5)
                .scan(0,(acc,elem)->acc+elem)
                .subscribe(result->log.info("Result : {}",result));
    }
    @Test
    public void p_151(){
        Flux.just(1, 2, 3)
                .thenMany(Flux.just(4,5))
                .subscribe(e -> log.info("onNext {}", e));
    }

    @Test
    public void p_152(){
        Flux.concat(
                Flux.range(1, 3),
                Flux.range(4, 2),
                Flux.range(6, 5)
                ).subscribe(e->log.info("onNext: {}",e))
                ;
    }
    @Test
    public void p_153_1(){
        Flux.range(1, 13)
                .buffer(4)
                .subscribe(e -> log.info("onNext: {}", e));
    }
    @Test
    public void p_153_2(){
        Flux<Flux<Integer>> windowedFlux = Flux.range(101,20)//1
                .windowUntil(ElementMapTest::isPrime, true);//2
        windowedFlux.subscribe(window -> //3
                window.collectList()//4
                .subscribe(e -> log.info("window: {}",e))//5
        );
    }

    private static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
    @Test
    public void p_154(){
        Flux.range(1,7)
                .groupBy(e -> e % 2 == 0 ? "Even" : "Odd")//1
                .subscribe(groupFlux -> groupFlux //2
                        .scan(//3
                                new LinkedList<>(),//4
                                (list, elem) -> {//4.1
                                    list.add(elem);//4.2
                                    if(list.size() > 2){
                                        list.remove(0);//4.3
                                    }
                                    return list;
                                })
                                .filter(arr -> !arr.isEmpty())//5
                                .subscribe(data -> {//6
                                    log.info("{}: {}",groupFlux.key(), data);
                                })
                        );
    }
    @Test
    public void p_157() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Flux.just("user-1", "user-2", "user-3")
                .flatMap(u -> requestBooks(u)
                        .map(b->u+"/"+b)
                ).subscribe(r->log.info("onNext: {}",r));
        latch.await(1, TimeUnit.SECONDS); // 최대 1초까지 기다림
    }
   static public Flux<String> requestBooks(String user){
       Random random = new Random();
       return Flux.range(1, random.nextInt(3) + 1)//1
               .map(i -> "book-" + i)//2
               .delayElements(Duration.ofMillis(3));//3
   }

    @Test
    public void p_158() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Flux.range(1, 100)
                .delayElements(Duration.ofMillis(1))
                .sample(Duration.ofMillis(20))
                .doOnNext(e -> log.info("onNext : {}", e))
                .doOnComplete(latch::countDown)
                .subscribe();

        // 2초 정도 대기 (데이터 양과 delay에 따라 조절)
        latch.await(1, TimeUnit.SECONDS);
    }
    @Test
    public void p_160_1(){
        Flux.just(1, 2, 3)
                .concatWith(Flux.error(new RuntimeException("conn error")))
                .doOnEach(s -> log.info("signal: {}", s))
                .subscribe();
    }
    @Test
    public void p_160_2(){
        Flux.range(1, 3)
                .doOnNext(e -> log.info("data : {}",e))
                .materialize()
                .doOnNext(e -> log.info("signal: {}", e))
                .dematerialize()
                .collectList()
                .subscribe(r->log.info("result: {}",r));
    }

}
