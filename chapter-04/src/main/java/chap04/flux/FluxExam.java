package chap04.flux;

import java.util.Arrays;

public class FluxExam {
    public void p_131(){
        reactor.core.publisher.Flux.range(1,5).repeat();
    }
    public void p_132(){
        reactor.core.publisher.Flux.range(1, 100)
                .repeat()// 무한 루프
                .collectList()
                .block();
    }
    public void p_134(){
        //Mono.from(Flux.from(mono))
    }
    public void p_135(){
        reactor.core.publisher.Flux<String> stream1 = reactor.core.publisher.Flux.just("Hello", "World");
        reactor.core.publisher.Flux<Integer> stream2 = reactor.core.publisher.Flux.fromArray(new Integer[]{1, 2, 3});
        reactor.core.publisher.Flux<Integer> stream3 = reactor.core.publisher.Flux.fromIterable(Arrays.asList(9, 8, 7));
        reactor.core.publisher.Flux<Integer> stream4 = reactor.core.publisher.Flux.range(2010,9);// 2010, 2011 ... 2018
    }
}
