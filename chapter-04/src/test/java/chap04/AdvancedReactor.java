package chap04;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class AdvancedReactor {

    public void p_185_1(){
//        Flux<Integer> sourceFlux = new FluxArray(1, 20 ,30,4000);
//        Flux<String> mapFlux = new FluxMap(sourceFlux, String::valueOf);
//        Flux<String> filterFlux = new FluxFilter(mapFlux, s->length() > 1);
    }
    public void p_185_2(){
//        FluxFilter(
//                FluxMap(
//                        FluxArray(1, 2, 3, 40, 500, 6000)
//                )
//        )
    }
    public void p_190_1(){
//        Scheduler scheduler = new Scheduler() {// 1
//            @Override
//            public Disposable schedule(Runnable runnable) {
//                return null;
//            }
//
//            @Override
//            public Worker createWorker() {
//                return null;
//            }
//        };
//        Flux.range(0, 100)// 2
//                .map(String::valueOf)// 3
//                .filter(s -> s.length() > 1)// 4
//                .publishOn(scheduler)// 5
//                .map(this::calculateHash)// 6
//                .map(this::doBusinessLogic)// 7
//                .subscribe();// 8
    }
    public void p_195_1(){
//        Flux.range(0,10000)
//                .parallel()
//                .runOn(Schedulers.parallel())
//                .map()
//                .filter()
//                .subscribe();
    }

}
