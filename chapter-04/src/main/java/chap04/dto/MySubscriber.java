package chap04.dto;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.BaseSubscriber;

@Slf4j
public class MySubscriber<T> extends BaseSubscriber<T> {

 public void hookOnSubscribe(Subscriber subscriber){
     log.info("initail request for 1 element");
     request(1);
 }

 public void hookOnNext(T value){
     log.info("onNext: {}", value);
     log.info("requesting 1 more element");
     request(1);
 }
}
