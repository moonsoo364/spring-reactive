package chap04;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Instant;

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
}
