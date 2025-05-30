package chap04;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ThreadLocalTest {
    @Test
    public void p_197(){
        ThreadLocal<Map<Object,Object>> threadLocal = new java.lang.ThreadLocal<>();//1
        threadLocal.set(new HashMap<>());// 1.1

        Flux//2
        .range(0, 10)// 2
        .doOnNext(k ->// 2.1
                        threadLocal
                                .get()
                                .put(k, new Random(k).nextGaussian()))// 2.2
                .publishOn(Schedulers.parallel())// 2.3
                .map(k -> threadLocal.get().get(k))// 2.4
                .blockLast();
    }
    @Test
    public void p_197_2() throws InterruptedException {
        Flux.range(0, 10)
                .flatMap(k ->
                        Mono.subscriberContext()// 1
                                .doOnNext(context -> {// 1.1
                                    Map<Object, Object> map = context.get("randoms");// 1.2
                                    map.put(k, new Random(k).nextGaussian());
                        })
                        .thenReturn(k)// 1.3
                )
                .publishOn(Schedulers.parallel())
                .flatMap(k ->
                        Mono.subscriberContext()
                                .map(context -> {// 2
                                    Map<Object, Object> map = context.get("randoms");// 2.1
                                    return map.get(k);// 2.2
                                })
                ).subscriberContext(context ->
                    context.put("randoms", new HashMap())
                )
                .blockLast();
        Thread.sleep(1000);
    }

}
