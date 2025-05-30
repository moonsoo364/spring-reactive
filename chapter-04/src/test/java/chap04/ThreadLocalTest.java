package chap04;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
public class ThreadLocalTest {
    @Test(expected = NullPointerException.class)
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
                        Mono.subscriberContext()
                                .doOnNext(context -> {
                                    Map<Object, Object> map = context.get("randoms");
                                    double value = new Random(k).nextGaussian();
                                    map.put(k, value);
                                    System.out.printf("[Step1] k = %d, generated = %.4f%n", k, value);
                                })
                                .thenReturn(k)
                )
                .publishOn(Schedulers.parallel())
                .flatMap(k ->
                        Mono.subscriberContext()
                                .map(context -> {
                                    Map<Object, Object> map = context.get("randoms");
                                    Object value = map.get(k);
                                    System.out.printf("[Step2] k = %d, retrieved = %.4f%n", k, value);
                                    return value;
                                })
                )
                .subscriberContext(context -> context.put("randoms", new HashMap<>()))
                .blockLast();

        Thread.sleep(1000); // 병렬 스케줄러의 작업을 기다리기 위해
    }

    @Test
    public void showcaseContext() {
        printCurrentContext("top")
                .subscriberContext(Context.of("top", "context"))
                .flatMap(__ -> printCurrentContext("middle"))
                .subscriberContext(Context.of("middle", "context"))
                .flatMap(__ -> printCurrentContext("bottom"))
                .subscriberContext(Context.of("bottom", "context"))
                .flatMap(__ -> printCurrentContext("initial"))
                .block();
    }

    void print(String id, Context context) {
        System.out.println(id + " {");
        System.out.print("  ");
        System.out.println(context);
        System.out.println("}");
        System.out.println();
    }

    Mono<Context> printCurrentContext(String id) {
        return Mono
                .subscriberContext()
                .doOnNext(context -> print(id, context));
    }

}
