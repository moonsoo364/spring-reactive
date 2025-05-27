package chap04;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.Comparator;

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
}
