package chap04;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class MicroFusion {
    @Test
    public void p_203(){
        Flux.just(1, 2, 3)
                .publishOn(Schedulers.parallel())// 1
                .concatMap(i -> Flux.range(0,1)
                        .publishOn(Schedulers.parallel())).subscribe();// 2
    }
}
