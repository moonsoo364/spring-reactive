# 스프링 리액티브 프로그래밍 
실전! 스프링 5를 활용한 리액티브 프로그래밍 책 실습 및 코드를 참조하여 작성되었습니다.
원본 코드 링크 : https://github.com/wikibook/spring5-reactive

## 스프링 리액티브 프로그래밍 이란?
스프링 리액티브 프로그래밍은 비동기(Asynchronous), 논 블로킹(Non-blocking) 방식으로 데이터를 처리하여 
적은 리소스로 높은 처리량과 응답성을 얻기 위한 프로그래밍 모델이다.

## 리액티브 스트림
리액티브 스트림은 비동기 데이터 스트림 처리를 위한 표준 사양이다.
자바 9 버전 이후의 `java.util.concurrent.Flow` 패키지에 포함되었고 대표 구현체로는 Rxjava, Project Reactor 등이 있다. 

### 스트림(stream)
스트림이란 시간의 흐름에 따라 비동기적으로 전달되는 데이터의 흐름입니다. 
스트림은 데이터가 한 번에 모두 있는 전달되는 것이 아니라 조금씩(push 방식으로) 흘러들어오는 데이터 시퀀스입니다.

### 리액티브 스트림의 목적
- 비동기 처리
- 논블로킹 백프래셔 지원
- 데이터 생산자와 소비자 간 안정적인 데이터 흐름 관리

# 리액터 프로젝트 - 리액티브 앱의 기초

## 리액터 프로젝트 버전 1.x
리액터 버전 1.x는 리액터 패턴, 함수형 프로그래밍 및 리액티브 프로그래밍과 같은 메시지 처리에 대한 모범 사례를 통합한 것입니다.

### 리액터 패턴 
비동기 이벤트 처리 및 동기처리에 도움이 되는 행위 패턴, 모든 이벤트가 큐에 추가되고 이벤트는 나중에 별도의 큐에 추가되고 이벤트는 나중에 별도의 스레드에 의해 처리됩니다.

## 리액티브 타입 Flux, Mono

리액티브 프로젝트의 구현체로 `Flux<T>`, `Mono<T>` 두 가지가 있습니다.

### Flux

Flux는 0, 1 또는 여러 요소를 생성할 수 있는 일반적인 리액티브 스트림을 정의합니다. 무한한 양의 요소를 만들 수도 있습니다. 다음과 같은 표현식으로 나타냅니다.

```java
onNext x 0..N [onError | onComplete]
```

스트림에 의해 만들어진 모든 요소를 수집하려는 시도는 OutOfMemoryError를 유발할 수 있습니다. 아래 예제를 통해 설명하겠습니다.

```java
    public void p_132(){
        Flux.range(1, 100)// 1
                .repeat()//2
                .collectList()//3
                .block();//4
    }
```

1. range 연산자는 1부터 100까지 정수 시퀀스를 만듭니다.
2. repeat 연산자는 소스 스트림이 끝난 후 소스 스트림을 다시 구독합니다. 따라서 request 연산자는 스트림 연산자 1 ~ 100 결과를 구독하고 onComplete 신호를 수신한 다음 다시 1 ~ 100을 수신하는 동작을 반복합니다.
3. collectList 연산자를 사용해생성된 모든 요소를 단일 리스트로 만듭니다. 반복 연산자가 끝없는 스트림을 생성하기 때문에 오소가 도착하고 목록의 크기가 늘어나면 OutouMemory에러를 발생 시킬 것입니다.
4. block은 실제 구독을 기동하고 최종 결과가 도착할 때 까지 실행 중인 스레드를 차단합니다.

### Mono

Mono는 최대 하나의 요소를 생성할 수 있는 스트림을 정의하며 다음 표현식으로 나타낼 수 있습니다.

```java
onNext x 0..1 [onError | onComplete]
```

Flux와 Mono의 차이는 메서드 시그니처에만 있는 것이 아닙니다. 버퍼 중복과 값비싼 동기화 작업을 생략하기 땜누에 Mono를 보다 효율적으로 사용할 수 있게 해줍니다.

`Mono<T>`는 응용 프로그램 API가 최대 하나의 원소를 반환하는 경우 유용합니다. `CompletableFuture<T>` 와 비슷한 용도로 사용하 수 있습니다. `CompletableFuture<T>` 는 Mono와 달리 반환값을 반드시 반환해야 합니다. 또한 `CompletableFuture<T>` 은 즉시 처리를 시작하고 `Mono`는 구독자가 나타날 때까지 아무 작업도 수행하지 않습니다. Mono의 장점은 더 많은 리액티브 연산자를 제공하고 더 큰 규모의 리액티브 워크플로와 완벽하게 통합할 수 있다는 점입니다.

- `CompletableFuture<T>`는 **비동기 작업의 결과(T)를 나중에 사용할 수 있도록** 해주는 Java 클래스입니다.

`Mono`는 클라이언트에게 작업이 완료됐음을 알리는 데 사용할 수 있습니다. 이 경우 `Mono<void>` 유형을 반환하고 처리가 완료되면 `onComplete()` 신호를 보내거나 실패한 경우 `onError()` 신호를 보냅니다. 이런 시나리오에서 데이터를 반환하지는 않지만 이후 연산을 위한 알림을 보내는 용도로 사용할 수 있습니다.
또한 Mono와 Flux는 서로 쉽게 변환할 수 있습니다. 이 코드를 실행하면 개념적으로 연산자 없는 반환이므로 원본 Mono 인스턴스를 반환합니다.

```java
Mono.from(Flux.from(mono))
```
### Flux와 Mono 시퀀스 만들기

Flux 및 Mono는 데이터를 기반으로 리액티브 스트림을 생성하는 많은 팩토리 메서드를 제공합니다. 예를 들어 객체에 대한 참조나 컬렉션에서 Flux를 만들거나 직접 숫자를 정해서 만들 수도 있습니다.

```java
//Flux, Mono 시퀀스 예제
    public void p_135(){
        //Flux
        Flux<String> stream1 = Flux.just("Hello", "World");
        Flux<Integer> stream2 = Flux.fromArray(new Integer[]{1, 2, 3});
        Flux<Integer> stream3 = Flux.fromIterable(Arrays.asList(9, 8, 7));
        Flux<Integer> stream4 = Flux.range(2010,9);// 2010, 2011 ... 2018
        
        //Mono
        Mono<String> stream5 = Mono.just("One");
        Mono<String> stream6 = Mono.justOrEmpty(null);
        Mono<String> stream7 = Mono.justOrEmpty(Optional.empty());
    }
```

`Mono`는 주로 하나의 요소를 대상으로 합니다. `nullable` 및 `optional` 타입과 함께 자주 사용됩니다. `Mono`는 Http요청이나 DB 쿼리와 같은 비동기 작업을 래핑하는 데 매우 유용합니다. `Mono` 는 HTTP 요청이나 DB 쿼리와 같은 비동기 작업을 래핑하는 데 유용합니다. 이를 위해 Mono는 `fromCallable`, `fromRunnable` 등의 메서드를 제공합니다.

```java
Mono<String> stream8 = Mono.fromCallable(()-> httpRequest()); 
Mono<String> stream9 = Mono.fromCallable(this::httpRequest);
```

이 코드는 HTTP 요청을 비동기적으로 만들 뿐만 아니라 onError로 전파되는 오류도 함께 처리합니다.

Flux와 Mono는 fromPublisher 팩토리 메서드를 사용해 다른 Publisher 인스턴스를 변환할 수 있습니다. 두 가지 타입 모두 빈 스트림과 오류만 포함하는 스트림을 만드는 메서드가 있습니다.

```java
Flux<String> empty = Flux.empty();
Flux<String> never = Flux.never();
Mono<String> error = Mono.error(new RuntimeException("Unknown id"));
```

Mono와 Flux는 empty()라는 메서드가 있습니다. 이 메서드는 Flux 또는 Mono의 빈 인스턴스를 각각 생성합니다.

defer는 구독하는 순간에 행동을 결정하는 시퀀스를 생성하는 메서드로 결과적으로 서로 다른 구독자에 대해 다른 데이터를 생성할 수 있습니다.

```java
// 사용자 요청을 받아서 Mono<User> 반환
    public Mono<User> requestUserData(String sessionId) {
        return Mono.defer(() -> {
            if (isValidSession(sessionId)) {
                return Mono.fromCallable(() -> requestUser(sessionId));
            } else {
                return Mono.error(new RuntimeException("Invalid user session"));
            }
        });
    }
```

이 코드는 실제 구독이 발생할 때까지 sessionId 유효성 검사를 연기합니다. 반대로 다음 코드는 requestUserData() 메서드가 호출될 때 유효성 검사를 수행합니다. 이 메서든느 실제 구독 이전에도 호출할 수 있습니다.

```java
    public Mono<User> requestUserData2 (String sessionId){
        return isValidSession(sessionId)
                ? Mono.fromCallable(()-> requestUser(sessionId))
                : Mono.error(new RuntimeException("Invalid user session"));
    }
```

첫 번째 예제는 `Mono<User>` 를 구독할 때 마다 유효성을 검사합니다. 두 번째 예제는  `requestUserData` 메서드가 호출될 때만 유효성 검사를 수행합니다. 그러나 구독 시 유효성 검사가 수행되지 않습니다.

### 리액티브 스트림 구독하기
Flux와 Mono는 구독 루틴을 훨씬 단순화하는 subscribe() 메서드를 람다 기반으로 정의합니다.

구독자를 만드는 데 필요한 옵션을 살펴보겠습니다. 우선 오버로딩한 subscribe의 모든 메서드는 Disposable 인터페이스의 인스턴스를 반환합니다. 이는 기본 Subscribion을 취소하는데 사용할 수 있습니다. 1 ~ 4의 경우 구독은 무제한으로(`Long.MAX_VALUE`) 요청합니다.

1. 스트림을 구독하는 가장 간단한 방법, 이 메서드는 모든 신호를 무시합니다. 일반적으로 다른 메서드를 사용하지만, 때로는 부작용이 있는 스트림 처리를 기동하는 것이 필요할 때도 있습니다.
2. dataConsumer는 값(`onNext()`)마다 호출됩니다. `onError` 및 `onComplete` 는 처리하지 않습니다.
3. (2)와 동일하지만 onError를 처리할 수 있습니다.
4. (3)과 동일하지만 onComplete를 처리할 수 있습니다.
5. 오류 처리 및 완료를 포함해 리액티브 스트림의 모든 요소를 처리합니다. 이 오버라이드는 적절한 양의 데이터를 요구함으로써 구독을 제어할 수 있게 하지만, 여전히 `Long.Max_VALUE` 형태로 무한 스트림을 요청할 수 있습니다.
6. 시퀀스를 구독하는 가장 일반적인 방법입니다. 구독자 구현에 원하는 동작을 추가할 수 있습니다.

```java
    public void p_138() {
        // 기본 구독 (onNext만 처리)
        Flux<Integer> flux = Flux.range(1, 5);  // 1~5까지의 숫자 emit
        flux.subscribe(); // 아무 처리도 안 함
        // 구독하면서 데이터 처리
        flux.subscribe();// 1
        flux.subscribe(Consumer<T> dataConsumer);// 2
        flux.subscribe(Consumer<T> dataConsumer, Consumer<Throwable> errorConsumer);// 3
        flux.subscribe(Consumer<T> dataConsumer, Consumer<Throwable> errorConsumer, Runnable completeConsumer);// 4
        flux.subscribe(Consumer<T> dataConsumer, Consumer<Throwable> errorConsumer, Runnable completeConsumer, Consumer<Subscription> subscriptionConsumer);// 5
        flux.subscribe(Subscriber<T> subscriber);//5
    }
```

아래는 간단한 리액티브 스트림을 만들고 실행한 결과 입니다.

```java
@Slf4j
public class FluxTest {

    @Test
    public void p_139(){
        Flux.just("A","B","C").subscribe(
                data -> log.info("onNext : {}", data),
                err -> {},
                () -> log.info("onComplete")
        );
    }
}
```

```java
12:17:03.354 [main] DEBUG reactor.util.Loggers$LoggerFactory - Using Slf4j logging framework
12:17:03.360 [main] INFO chap04.FluxTest - onNext : A
12:17:03.361 [main] INFO chap04.FluxTest - onNext : B
12:17:03.361 [main] INFO chap04.FluxTest - onNext : C
12:17:03.361 [main] INFO chap04.FluxTest - onComplete
```

제한 없는 수요 요청을 통한 간단한 구독을 통해 프로듀서가 수요를 충족시키기 위해 상당한 양의 작업을 하도록 할 수 있습니다. 프로듀서가 제한된 수요를 처리하는 데 더 적합하다면 구독 객체로 수요를 제어하거나 요청 제한 연산자를 적용하는 것이 좋습니다.

다음은 코드에 대한 설명 입니다.

1. range 연산자로 100개의 값을 생성합니다.
2. 이전 예제와 같은 방식으로 스트림을 구독합니다.
3. 구독을 제어합니다. 처음 4개를 요청한다음 즉시 구독을 취소하므로 다른 요소가 전혀 생성되지 않아야 합니다.

```java
@Test
    public void p_139_2(){
        Flux.range(1, 100)//1
                .subscribe(//2
                        data -> log.info("onNext : {}",data),
                        err -> {},
                        () -> log.info("onComplete"),
                        subscription -> {
                            subscription.request(4);
                            subscription.cancel();
                        }
                );
    }
```

위 코드를 실행한 결과는 아래와 같습니다.

```java
12:26:41.693 [main] INFO chap04.FluxTest - onNext : 1
12:26:42.814 [main] INFO chap04.FluxTest - onNext : 2
12:26:43.400 [main] INFO chap04.FluxTest - onNext : 3
12:26:43.977 [main] INFO chap04.FluxTest - onNext : 4
```

구독자가 스트림이 끝나기 전에 구독을 취소했으므로 onComplete 신호를 수신하지 않습니다. 리액티브 스트림은 프로듀서가 종료하거나 Subscription 인스턴스를 통해 구독자가 취소할 수 있음을 기억하는 것도 중요합니다.

Disposable 인스턴스는 취소 목적으로 사용할 수도 있습니다. 보통은 구독자에 사용하는 것보다는 상위 레벨의 추상화 코드에서 사용합니다.

Disposable을 호출해 스트림 처리를 취소하는 예제를 확인해 봅시다.
```java
    @Test
    public void p_140() throws InterruptedException { // 1
        Disposable disposable = Flux.interval(Duration.ofMillis(50))
                .subscribe(// 2
                data -> log.info("onNext: {}", data)
        );
        Thread.sleep(200);// 3
        disposable.dispose();// 4
    }
```

1. interval 팩토리 메서드는 주기적으로(50밀리초)으로 이벤트를 생성할 수 있습니다. 스트림은 무한히 생성됩니다.
2. onNext 시그널에 대한 핸들러만 제공해 구독합니다.
3. 이벤트를 받을 때까지 잠시 대기합니다.
4. 내부적으로 구독을 취소하는 dispose 메서드를 호출합니다.

### 사용자 정의 subscriber 구현하기

기본 subscribe 메서드만으로 요구사항을 만족하지 못한다면 Subscriber 인터페이스를 직접 구현하고 스트림을 구독할 수 있습니다.

```java
@Test
    public void p_141(){
        Subscriber<String> subscriber = new Subscriber<String>() {
            volatile Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                log.info("requesting 1 more element");
                subscription.request(1);

            }

            @Override
            public void onNext(String s) {
                log.info("onNext : {}", s);
                log.info("requesting 1 more element");
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                log.warn("onError : {}",t.getMessage());
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };
        Flux<String> stream = Flux.just("Hello","World","!");
        stream.subscribe(subscriber);
    }
```

다음은 아래 코드 에 대한 설명입니다.

1. 커스텀 구독자는 Publisher와 Subscriber를 바인딩하는 Subscription에 대한 참조를 가져야 합니다. 구독 및 데이터 처리가 다른 스레드에서 발생할 수 있으므로 모든 스레드가 Subscription 인스턴스에 대한 올바른 참조를 가질 수 있도록 volatile 키워드를 사용했습니다.
2. 구독이 도착하면 Subscriber에 onSubscribe 콜백이 전달됩니다. 2.1에서 구독을 저장하고 초기 수요를 요청합니다.
3. onNext 콜백에서 수신된 데이터를 기록하고 다음 원소를 요청합니다. 이 경우 배압 관리를 위해 간단한 pull 모델을 사용합니다.

```java
@Test
    public void p_141(){
        Subscriber<String> subscriber = new Subscriber<String>() {
            volatile Subscription subscription;//1

            @Override
            public void onSubscribe(Subscription s) {//2
                subscription = s;
                log.info("requesting 1 more element");
                subscription.request(1);

            }

            @Override
            public void onNext(String s) {//3
                log.info("onNext : {}", s);
                log.info("requesting 1 more element");
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                log.warn("onError : {}",t.getMessage());
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };
        Flux<String> stream = Flux.just("Hello","World","!");//4
        stream.subscribe(subscriber);//5
    }
```

위 코드를 실행하면 콘솔에 다음 내용이 출력됩니다.

```java
16:56:24.916 [main] INFO chap04.FluxTest - requesting 1 more element
16:56:24.916 [main] INFO chap04.FluxTest - onNext : Hello
16:56:24.917 [main] INFO chap04.FluxTest - requesting 1 more element
16:56:24.917 [main] INFO chap04.FluxTest - onNext : World
16:56:24.917 [main] INFO chap04.FluxTest - requesting 1 more element
16:56:24.917 [main] INFO chap04.FluxTest - onNext : !
16:56:24.917 [main] INFO chap04.FluxTest - requesting 1 more element
16:56:24.919 [main] INFO chap04.FluxTest - onComplete
```

그러나 이 예제에서 구독을 정의하는 접근 방식은 올바르지 않습니다. 1차원적 코드 흐름이 깨지며 오류가 발생하기 쉽습니다. 가장 어려운 부분은 스스로 배압을 관리하고 가입자에 대한 모든 TCK (Technology Company Kit)요구사항을 위반했습니다.

대신 리액터 프로젝트에서 제공하는 BaseSubscriber 클래스를 상속하는 것이 훨씬 더 좋은 방법입니다.

```java
@Test
    public void p_143(){
        Subscriber<String> subscriber = new MySubscriber<String>();
        Flux<String> stream = Flux.just("Hello","World","!");
        stream.subscribe(subscriber);
    }
```

hookOnSubscribe() 및 hookOnNext(T) 메서드와 함께 hookOnError(Throwable), hookOnCancel(), hookOnComplete() 및 기타 소수의 메서드를 재정의 할 수 있습니다. 이런 접근은 세심한 라이프 사이클 관리가 필요한 리소스를 포함하는 경우 바람직할 수 있습니다. 예를 들면 외부 서비스에 연결하는 파일 핸들러 또는 웹소켓 연결을 가진 구독자의 경우입니다.

```java
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
```

리액티브 시퀀스를 이용해 작업할 때는 스트림을 생성하고 소비하는 것 외에도 스트림을 완벽하게 변환하고 조작할 수 있어야 합니다. 리액터 프로젝트는 거의 모든 리액티브 타입 변환에 필요한 도구를 제공합니다.

이런 도구는 다음과 같이 분류할 수 있습니다.

- 기존 시퀀스 변환
- 시퀀스 처리 과정을 살펴보는 메서드
- Flux 시퀀스를 분할 또는 결합
- 시간을다루는 작업
- 데이터를 동기적으로 작업

### 리액티브 시퀀스의 원소 매핑하기

시퀀스를 변환하는 가장 자연스러운 방법은 모든 원소를 새 값으로 매핑하는 것 입니다. Flux 및 Mono는 자바 스트림 API의 map 연산자와 비슷한 동작을 하는 map연산자를 제공합니다. map 시그니쳐가 있는 함수는 원소를 하나씩 처리합니다. 물론 원소의 유형을 T에서 R로 변경하면 전체 시퀀스의 유형이 변경되므로 map 연산자 Flux<T>가 Flux<R>이 된 후에Mono<T>는 Mono<R>이 됩니다.

```java
   public final <E> Flux<E> cast(Class<E> clazz) {
        Objects.requireNonNull(clazz, "clazz");
        clazz.getClass();
        return this.<E>map(clazz::cast);
    }
```

index 연산자를 이용하면 시퀀스의 원소를 열거할 수 있습니다. 이제 Tuple2 클래스를 이용해야 합니다. 이는 표준 라이브러리에 없는 Tuple 타입 입니다. 연산자에서 자주 사용하는 Tuple2 -Tuple8 클래스 라이브러리와 함께 제공됩니다. `timestamp()` 연산자는 현재 타임스템프를 추가합니다. 따라서 다음 코드는 원소를 열거하고 시퀀스의 모든 원소에 타임스탬프를 첨부합니다.

```java
 @Test
    public void p_145(){
        Flux
            .range(2018,5) //1
                .timestamp()//2
            .index() //3
            .subscribe(e -> log.info("index : {}, ts: {}, value: {}",//4
                    e.getT1(),//4.1
                    Instant.ofEpochMilli(e.getT2().getT1()),//4.2
                    e.getT2().getT2()//4.3s
            ));
    }
```

1. range 연산자로 데이터(2018 ~ 2022)를 생성합니다. 이 연산자는 Flux<Integer> 타입의 시퀀스를 반환합니다.
2. timestamp 연산자를 사용해 현재 타임스탬프를 첨부합니다. 이제 시퀀스에는 `Flux<Tuple2<Long, Integer>>` 타입이 들어 있습니다.
3. index연산자를 열거형으로 전환합니다. 이제 시퀀스에는 `Flux<Tuple2<Long, Tuple2<Long, Integer>>>` 타입이 들어가 있습니다.
4. 시퀀스를 구독하고 각 원소에 대한 로그 메시지를 남깁니다
    1. `e.getT1()` 호출은 인덱스를 반환합니다.
    2. `e.getT2().getT1()` 호출은 타임스탬프를 반환하고 Instant 클래스의 매개변수로 사용해 사람이 읽을 수 있는 시간을 출력합니다.
    3. `e.getT2().getT2()` 호출은 실제 값을 반환합니다.

```java
17:22:56.898 [main] INFO chap04.ElementMapTest - index : 0, ts: 1970-01-01T00:00:02.018Z, value: 2018
17:22:56.909 [main] INFO chap04.ElementMapTest - index : 1, ts: 1970-01-01T00:00:02.019Z, value: 2019
17:22:56.909 [main] INFO chap04.ElementMapTest - index : 2, ts: 1970-01-01T00:00:02.020Z, value: 2020
17:22:56.909 [main] INFO chap04.ElementMapTest - index : 3, ts: 1970-01-01T00:00:02.021Z, value: 2021
17:22:56.909 [main] INFO chap04.ElementMapTest - index : 4, ts: 1970-01-01T00:00:02.022Z, value: 2022
```

### 리액티브 시퀀스 필터링하기

리액터 프로젝트에는 다음과 같이 필터링을 위한 모든 종류의 연산자가 포함돼 있습니다.

- filter 연산자는 조건을 만족하는 요소만 통과시킵니다.
- ignoreElement 연산자는 Mono<T>를 반환하고 어떤 원소도 통과시키지 않습니다. 결과 시퀀스는 원본 시퀀스가 종료된 후에 종료됩니다.
- 라이브러리는 첫 번째 n을 제외한 모든 원소를 무시하는 `take(n)` 연산자로 유입되는 원소의 개수를 제한할 수 있습니다.
- takeLast() 는 스트림의 마지막 원소만 반환합니다.
- takeUntil(Predicate)는 조건이 만족할 때 까지 원소를 전달합니다.
- elementAt(n)은 시퀀스의 n번째 원소만 가져옵니다.
- single 연산자는 소스에서 단일 항목을 내보냅니다. 빈 소스에 대해 NoSuchElementException 오류를 발생시키고, 복수의 요소를 가지는 소스의 경우는 `IndexOutofBoundException`을 발생시킵니다.
- `skip(Duration)` 또는 `take(Duration)` 연산자를 사용해 양뿐만 아니라 특정 시간까지 원소를 가져오거나 건너뛸 수 있습니다.
- `takeUnitOther(Publisher)` 또는 `skipUtilOther(Publisher)` 를 이용해 특정 스트림에서 메시지가 도착할 때까지 원소를 건너뛰거나 가져올 수 있습니다.

### 리액티브 시퀀스 수집하기

리스트의 모든 원소를 수집하고 결과를 `Flux.collectList()` 및 `Flux.collectSortedList()` 를 사용해 Mono타입 스트림으로 처리할 수 있습니다. 마지막 원소에 이르면 해당 원소를 수지할 뿐만 아니라 정렬 작업도 수행합니다.

```java
@Test
    public void p_147(){
        Flux.just(1,6,2,8,3,1,5,1)
                .collectSortedList(Comparator.reverseOrder())
                .subscribe(System.out::println);
    }
```
```java
[8, 6, 5, 3, 2, 1, 1, 1]
```

컬렉션에서 시퀀스 원소를 수집하는 것은 자원을 부족하게 할 수 있으며, 특히 시퀀스에 많은 원소가 있는 경우 주의해야 합니다. 또한 스트림이 무한할 경우에는 메모리가 고갈될 수 있습니다.

리액터 프로젝트는 Flux 원소를 List외에 다음 형태로도 변환할 수 있습니다.

- collectMap 연산자로 Map<K, T>로 변환
- collectMultimap 연산자로 Map<K, Collection<T>>로 변환
- `Flux.collect(Collector)` 연산자를 이용해 `java.util.stream.Collector`를 상속한 모든 형태의 데이터 구조로 변환

Flux와 Mono에는 입력 시퀀스의 루핑을 허용하는 `repeat()`  및 `repeat(times)` 메서드가 있습니다. 이전 절에는 이 메서드를 이미 사용했습니다.

또한 스트림이 비어 있을 경우에는 기본값을 반환하는 defaultEmpty(T) 라는 메서드도 있습니다.

`Flux.distinct()` 는 입력 시퀀스의 중복을 제외하고 전달합니다. 그러나 이 메서드는 모든 원소를 추적하므로 신중하게 사용해야 합니다.(특히 스트림 개수가 많은 경우) distinct 메서드의 중복 체크를 위한 사용자 지정 알고리즘을 지정할 수 있습니다.따라서 distinct 연산자의 자원 사용을 수동으로 최적화하는 것도 가능합니다.

### 스트림의 원소 줄이기

리액터 프로젝트를 사용하면 스트림의 원소 수를 카운트하거나 `Flux.all(predicate)` 로 모든 원소가 필요한 속성을 갖고 있는 지 확인할 수 있습니다. `Flux.any(Predicate)` 연산자를 이용해 하나 이상의 원소에 원하는 속성이 있는 지 확인할 수 있습니다.

스트림에 특정 원소가 하나라도 있는 지 hasElements 연산자로 확인할 수 있으며, 스트림에 특정 원소가 존재하는 지 확인하기 위해 `hasElement` 연산자를 사용할 수 있습니다. 후자는 찾는 원소가 발견되는 즉시 true를 반환하고 종료합니다. 또한 any 연산자는 Predicate 인스턴스를 직접 정의할 수 있어서 원소의 일치 여부뿐만 아니라 다른 특성도 검사할 수 있습니다. 아래는 시퀀스에 짝수가 있는 지 확인하는 예제입니다.

```jsx
    @Test
    public void p_149(){
        Flux.just(3,5,7,9,11,15,16,17)
                .any(e->e%2==0)
                .subscribe(hasEvens -> log.info("Has evens: {}", hasEvens));
    }
```

sort 연산자는 백그라운드에서 원소를 정렬한 다음, 원래 시퀀스가 완료되면 정렬한 시퀀스를 출력으로 내보냅니다.

Flux 클래스를 사용하면 사용자가 로직을 직접 정의해 시퀀스를 줄일 수 있습니다.(폴딩 이라고 합니다.) reduce 연산자는 일반적으로 초깃값을 첫 번째 매개 변수로 받고 이전 단계의 결과를 현재 단계의 원소와 결합하는 함수를 두 번째 매개 변수로 받습니다. 1에서 5사이의 정수를 더해봅시다.

```jsx
    @Test
    public void p_150_1(){
        Flux.range(1, 5)
                .reduce(0,(acc,elem)-> acc+elem)
                .subscribe(result-> log.info("Result: {}",result));
    }
```

reduce 연산자는 최종 결과 하나만 출력으로 내보냅니다. 그러나 집계를 수행할 때 중간 결과를 다운스트림으로 내보내는 것이 필요할 때가 있습니다. `Flux.scan()` 연산자가 이를 수행합니다. scan 연산자를 통해 1~5 사이의 정수를 더해봅시다.

```jsx
    @Test
    public void p_150_2(){
        Flux.range(1,5)
                .scan(0,(acc,elem)->acc+elem)
                .subscribe(result->log.info("Result : {}",result));
    }
```

최종 결과는 이전과 동일하게 15입니다. 하지만 이번에는 중간과정도 모두 출력됐습니다. 즉, 진행 중인 이벤트에 대한 정보가 필요한 애플리케이션에서 scan 연산자를 유용하게 쓸 수 있습니다. 예를 들어 다음과 같이 스트림의 이동 평균을 계산할 수 있습니다.
이 코드에 대한 설명은 다음과 같습니다.

1. 이동 평균 범위를 정의합니다.(최근 다섯 가지 이벤트에 관심이 있다고 가정해 봅시다.)
2. range 연산자로 데이터를 생성합니다.
3. index 연산자를 사용해 각 원소에 인덱스를 부여할 수 있었습니다.
4. scan 연산자를 사용해 최근 5개의 원소를 컨테이너로 수집합니다. 여기서 인덱스는 컨테이너의 위치를 계산하는 데 사용됩니다. 단계마다 업데이트된 컨테이너를 반환합니다.
5. 스트림 시작 부분의 일부 원소를 건너뛰고 이동 평균에 대한 데이터를 수집합니다.
6. 이동 평균의 값을 계산하기 위해서 컨테이너 내부 원소의 합을 크기로 나눕니다.
7. 물론 값을 받기 위해 데이터를 구독합니다.

```java
@Test
    public void p_150_3(){
        int bucketSize = 5;// 1
        Flux.range(1, 500)//2
                .index()//3
                .scan(//4
                        new int[bucketSize],//4.1
                        (acc,elem) ->{
                            acc[(int)(elem.getT1() % bucketSize)] = elem.getT2();//4.2
                            return acc;//4.3
                        })
                .skip(bucketSize)//5
                .map(array-> Arrays.stream(array).sum()*1.0/bucketSize)//6
                .subscribe(av->log.info("Running average: {}",av));//7
    }
```

```java
08:51:43.253 [main] INFO chap04.ElementMapTest - Running average: 3.0
08:51:43.255 [main] INFO chap04.ElementMapTest - Running average: 4.0
08:51:43.255 [main] INFO chap04.ElementMapTest - Running average: 5.0
```

Mono 및 Flux에는 then, thenMany, thenEmpty 연산자가 있으며, 이들 연산자는 상위 스트림이 완료될 때 동시에 완료됩니다. 이들 연산자는 들어오는 원소를 무시하고 완료 또는 오류 신호만 내보냅니다. 이러한 연산자는 상위 스트림 처리가 완료되는 즉시 새 스트림을 기동하는 데 유용하게 사용할 수 있습니다.

```java
@Test
    public void p_151(){
        Flux.just(1, 2, 3)
                .thenMany(Flux.just(4,5))
                .subscribe(e -> log.info("onNext {}", e));
    }
```

subscribe 메서드의 람다는 1, 2, 3이 스트림에서 생성되고 처리되더라도 4와 5만 받습니다.

```java
09:02:04.739 [main] INFO chap04.ElementMapTest - onNext 4
09:02:04.740 [main] INFO chap04.ElementMapTest - onNext 5
```

### 리액티브 스트림 조합하기

리액터 프로젝트를 사용하면 여러 개의 입력 스트림을 하나의 출력 스트림으로 결합할 수 있습니다. 연산자의 버전은 여러 가지지만, 기본적으로 다음과 같은 동작을 수행합니다.

- concat 연산자는 수신된 원소를 모두 연결해 다운스트림으로 전달합니다. 연산자가 두 개의 스트림을 연결하면 처음에는 첫 번째 스트림의 모든 원소를 소비한 후 다시 보내고 두 번째 스트림에 대해 동일한 작업을 수행합니다.
- merge 연산자는 업스트림 시퀀스의 데이터를 하나의 다운스트림 시퀀스로 병합합니다. concat 연산자와 달리 업스트림 소스는 각각 별개로 구독됩니다.
- zip 연산자는 모든 업스트림을 구독하고 모든 소스가 하나의 원소를 내보낼 때까지 대기한 다음, 수신된 원소를 출력 원소로 결합합니다.
  리액터에서 zip연산자는 리액티브 게시자뿐만 아니라 Iterable 컨테이너에서도 동작할 수 있습니다. 이를 zipWithIterable 연산자를 사용할 수 있습니다.
- combineLatest 연산자는 zip 연산자와 비슷하게 작동합니다. 그러나 최소한 하나의 업스트림 소스가 값을 내면 바로 새 값을 생성합니다.

```java
@Test
    public void p_152(){
        Flux.concat(
                Flux.range(1, 3),
                Flux.range(4, 2),
                Flux.range(6, 5)
                ).subscribe(e->log.info("onNext: {}",e))
                ;
    }
```

앞의 코드는 1에서 10까지 값을 생성합니다. ([1, 2, 3]+[4, 5]+[6, 7, 8, 9, 10])

### 스트림 내의 원소 일괄 처리하기

리액터 프로젝트는 두 가지 방법으로 스트림(Flux <T>)에 대한 일괄 처리를 지원합니다.

- List와 같은 컨테이너를 이용한 버퍼링. 출력되는 스트림의 타입은 `Flux<List<T>>` 입니다.
- `Flux<Flux<T>>` 와 같은 형태로 스트림을 스트림으로 윈도우잉. 이 경우 스트림 내부 원소는 값이 아니라 다른 스틀미이 되므로 별도의 추가적인 처리를 할 수 있습니다.
- `Flux<GroupedFlux<K,T>` 유형의 스트림으로 그룹화(Grouping), 각각의 새로운 키는 새로운 GroupFlux 인스턴스를 가르키고 해당 키를 가진 스트림 원소는 GroupFlux 클래스의 인스턴스를 통해 스트림에 추가됩니다.

버퍼링 및 윈도우 처리는 다음 경우에 발생할 수 있습니다.

- 처리된 원소의 수에 기반 10개의 원소를 처리할 때마다 신호를 보내야 할 때
- 시간 기반. 5분마다 신호를 보내야 할 때
- 특정 로직에 기반, 새로운 짝수를 전달받기 전에.
- 실행을 제어하는 다른 Flux에서 전달된 이벤트 기반

크기가 4인 리스트에 정수 원소를 버퍼링해 봅시다.

```java
@Test
    public void p_153_1(){
        Flux.range(1, 13)
                .buffer(4)
                .subscribe(e -> log.info("onNext: {}", e));
    }
```

이 코드의 실행 결과는 다음과 같습니다.

```java
09:19:55.303 [main] INFO chap04.ElementMapTest - onNext: [1, 2, 3, 4]
09:19:55.303 [main] INFO chap04.ElementMapTest - onNext: [5, 6, 7, 8]
09:19:55.303 [main] INFO chap04.ElementMapTest - onNext: [9, 10, 11, 12]
09:19:55.303 [main] INFO chap04.ElementMapTest - onNext: [13]
```

buffer 연산자는 여러개의 이벤트를 묶어서 이벤트 컬렉션을 만들어냅니다. 이 컬랙션은 다운스트림 연산자를 위한 이벤트가 됩니다. buffer 연산자는 하나의 원소만 가진 많은 작은 요청 대신에 컬렉션을 이용해 요청 횟수를 줄이는 수 있습니다. 예를 들어 스트림 원소를 데이터베이스에 하나씩 삽입하는 대신 몇 초 동안 버퍼링하여 일괄 삽입을 수행할 수 있습니다. 물론 이것은 데이터 정합성에 대한 요구 사항을 위배하지 않는 경우에만 해당합니다.

window 연산자를 학습하기 위해 원소가 소수 일 때마다 숫자 시퀀스를 분할해 보겠습니다. 이를 위해 window 연산자의 windowUntil 변형을 사용할 수 있습니다. 코드는 다음과 같습니다.

```java
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
```

1. 101부터 20까지 정수를 생성합니다.
2. 여기에서는 숫자가 소수일 때마다 스트림 원소를 분할합니다. windowUtil 연산자의 두 번째 인수를 이용해 소수를 발견했을 때 해당 원소 앞에서 스트림을 분할할지, 해당 원소 뒤에서 스트림을 분할할지를 정의합니다. 앞의 코드에서 `true` 로 매개변수를 정의했고 이는 새로운 소수가 나타나면 즉시 분할합니다. 결과 스트림은 `Flux<Flux<Integer>>` 타입입니다.
3. 2의 결과인 windowedFlux 스트림을 구독할 수 있습니다. 그러나 windowedFlux 스트림의 각 원소는 자체적으로 리액티브 스트림입니다. 그래서 각 원소에 대해 여기서는 또 다른 리액티브 변환 처리를 합니다.
4. 분할된 스트림에 대해 collectList 연산자로 원소를 수집해 `Mono<List<Integer>>` 타입으로 컬렉션화 해서 개수를 축소합니다.
5. 각 내부에 Mono에 대해 별도의 구독을 만들고 수신된 이벤트를 로그로 출력합니다.

첫 번째 슬라이스는 비어 있습니다. 이는 원본 스트림을 시작하면 새로운 슬라이스를 만들고, 첫 번째 원소가 101이기 때문입니다.(`onComplete` 시그널 발생)

buffer연산자는 버퍼가 닫힐 때만 컬렉션을 내보내는 반면, window 연산자는 원소가 도착하자마자 이벤트를 전달하므로 더 빨리 반응하고 복잡한 워크플로를 구현할 수있습니다.

```java
09:30:31.659 [main] INFO chap04.ElementMapTest - window: []
09:30:31.660 [main] INFO chap04.ElementMapTest - window: [101, 102]
09:30:31.660 [main] INFO chap04.ElementMapTest - window: [103, 104, 105, 106]
09:30:31.660 [main] INFO chap04.ElementMapTest - window: [107, 108]
09:30:31.660 [main] INFO chap04.ElementMapTest - window: [109, 110, 111, 112]
09:30:31.660 [main] INFO chap04.ElementMapTest - window: [113, 114, 115, 116, 117, 118, 119, 120]
```

groupBy 연산자를 사용해 리액티브 스트림의 원소를 몇 가지 기준으로 그룹화할 수 있습니다. 다음은 홀수와 짝수로 정수 시퀀스를 나눠서 각 그룹의 마지막 두 원소만 확인하는 코드입니다.

```java
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
```

1. 숫자 시퀀스를 생성합니다.
2. groupBy, 연산자를 사용해 홀수, 짝수를 구분합니다. `Flux<GroupedFluex<String, Integer>>` 타입의 스트림을 반환합니다.
3. 여기서 메인 Flux를 구독하고 각각의 GroupedFlux에 대해 scan 연산자를 적용합니다.
4. scan 연산자는 비어있는 리스트로 시작합니다. GroupedFlux의 각 원소는 리스트에 추가되고 (4.2) 리스트의 크기가 2보다 큰 경우 가장 오래된 원소를 제거합니다.(4.3)
5. scan 연산자는 우선 비어 있는 리스트를 전파한 다음, 다시 계산한 값을 전파합니다. 이 경우 필터 연산자를 사용하면 scan 결과에서 비어 있는 결과를 제거할 수 있습니다.
6. 마지막으로 GroupedFlux를 개별적으로 구독하고 scan 연산자가 내보낸 결과를 표시합니다.

```java
09:49:28.910 [main] INFO chap04.ElementMapTest - Odd: [1]
09:49:28.911 [main] INFO chap04.ElementMapTest - Even: [2]
09:49:28.911 [main] INFO chap04.ElementMapTest - Odd: [1, 3]
09:49:28.912 [main] INFO chap04.ElementMapTest - Even: [2, 4]
09:49:28.912 [main] INFO chap04.ElementMapTest - Odd: [3, 5]
09:49:28.912 [main] INFO chap04.ElementMapTest - Even: [4, 6]
09:49:28.912 [main] INFO chap04.ElementMapTest - Odd: [5, 7]
```

### flatMap, concatMap, flatMapSequential 연산자

flatMap 연산자는 논리적으로 map과 flatten의 2가지 작업으로 구성됩니다.(merge  연산자와 비슷합니다.) flatMap연산자의 map파트는 들어오는 각 원소를 리택티브스트림(`T  → Flux<R>` 로 변환하고 Flatten 파트는 생성된 모든 리액티브 시퀀스를 R 타입의 원소를 통과시키는 새로운 리액터 시퀀스로 병합합니다.

리액터 프로젝트는 flatMap 연산자의 다양한 변형을 제공합니다. 연산자 오버라이딩뿐만 아니라, flatMapSequential 연산자와 concatMap 연산자도 제공합니다. 이 세 연산자는 다음과 같은 몇 가지 차이점이 있습니다.

- 연산자가 내부 스트림을 하나씩 구독하는 지 여부(flatMap, flatMapSequential 연산자는 하나씩 구독합니다. concatMap은 하위 스트림을 생성하고 구독하기 전에 스트림 내부 처리가 완료되기를 기다립니다.)
- 연산자가 생성된 원소의 순서를 유지하는 지 여부(concatMap은 원본과 동일한 순서를 유지하고 flatMapSequential 연산자는 큐에 넣어 순서를 역순으로 유지하지만, flatMap 연산자는 원래 순서를 유지하지는 않습니다.)
- 연산자가 다른 하위 스트림의 원소를 끼워 넣을 수 있는 지 여부(flatMap 연산자는 허용. concatMap 및 flatMapSequential은 허용하지 않음)

사용자들이 좋아하는 책을 확인하는 알고리즘을 구현해 보겠습니다. 사용자가 좋아하는 책을 질의하는 서비스는 다음과 같습니다.

```java
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
```

1. 서비스가 임의의 정수 값을 생성합니다.
2. 그런 다음 각 번호를 책 제목에 매핑합니다.
3. 서비스가 데이터베이스와의 통신을 시뮬레이션하기 위해 일정 시간 지연합니다.

```java
10:22:34.646 [parallel-1] INFO chap04.ElementMapTest - onNext: user-1/book-1
10:22:34.647 [parallel-1] INFO chap04.ElementMapTest - onNext: user-2/book-1
10:22:34.647 [parallel-1] INFO chap04.ElementMapTest - onNext: user-3/book-1
10:22:34.661 [parallel-5] INFO chap04.ElementMapTest - onNext: user-3/book-2
10:22:34.661 [parallel-5] INFO chap04.ElementMapTest - onNext: user-2/book-2
10:22:34.677 [parallel-6] INFO chap04.ElementMapTest - onNext: user-2/book-3
10:22:34.677 [parallel-6] INFO chap04.ElementMapTest - onNext: user-3/book-3
```

결과를 통해 flatMap 연산자의 출력이 다른 스레드의 구독자 핸들러에 도착한다는 것을 알 수 있습니다. 그러나 리액티브 스트림 스펙은 발생 순서를 보장합니다. 따라서 원소가 다른 스레드에 도착할 수 있는 경우에도 원소가 동시에 도착하지 않습니다.

또한 라이브러리는 flatMapDelayError, flatMapSequentialDelayError, concatMapDelayError 연산자를 사용해 onError 시그널을 지연시킬 수 있습니다. 이 외에도 concatMapIterable 연산자는 변환 함수가 리액티브 스트림 대신 각 원소에 대한 iterator를 생성할 때 유사한 연산을 허용합니다. 이 경우 끼워 넣기는 발생하지 않습니다.

flatMap 연산자(및 그 변형 연산자)는 한 줄의 코드로 복잡한 워크플로를 구현할 수 있으므로 함수형 프로그래밍 및 리액티브 프로그래밍 모두에서 중요합니다.

### 샘플링하기

처리량이 많은 시나리오의 경우 샘플링 기술을 적용해 일부 이벤트만 처리하는 것이 좋습니다. 리액터에는 sample 및 sampleTimeout 연산자가 있습니다. 이 연산자를 사용하면 시퀀스는 특정 기간 내 가장 최근에 본 값을 주기적으로 출력할 수 있습니다. 다음과 같은 코드가 있다고 합시다.

```java
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
        latch.await(2, TimeUnit.SECONDS);
    }
```

프로그램 실행 시에 밀리초 단위로 숫자를 순차적으로 생성하더라도 구독자는 원하는 한도 내에서 이벤트의 일부만 수신한다는 것을 알 수 있습니다. 이러한 접근 방식을 이용하면 모든 이벤트가 필요하지 않은 경우 직접 처리량을 제한할 수 있습니다.

```java
10:43:44.470 [parallel-1] INFO chap04.ElementMapTest - onNext : 6
10:43:44.486 [parallel-1] INFO chap04.ElementMapTest - onNext : 7
10:43:44.518 [parallel-1] INFO chap04.ElementMapTest - onNext : 9
```

### 리액티브 시퀀스를 블로킹 구조로 전환하기

리액터 프로젝트 라이브러리는 리액티브 시퀀스를 블로킹 구조로 변환하기 위한 API를 제공합니다. 리액티브 애플리케이션에서 블로킹 처리를 해서는 안 되지만, 상위 API에서 필요로 하는 경우도 있습니다. 스트림을 차단하고 결과를 동기적으로 생성하기 위한 다음과 같은 옵션이 있습니다.

- tolerable 메서드는 Flux를 블로킹 Iterable를 변환합니다.
- toStream 메서드는 리액티브 Flux를 블로킹 스트림 API로 변환합니다.
- blockFirst 메서드는 업스트림이 첫 번째 값을 보내거나 완료될 때까지 현재 스레드를 차단합니다.
- blockLast 메서드는 업스트림이 마지막 값을 보내거나 완료될 때까지 현재 스레드를 차단합니다. onError 신호의 경우 차단된 스레드에 예외가 발생합니다.

blockFirst 및 blockLast 연산자는 스레드 차단 기간을 설정할 수 있는 추가 매개변수를 받을 수도 있습니다. 그렇게 하면 스레드가 무한대로 차단되지 않습니다. 또한 toIterable 및 toStream 메서드를 사용하면 클라이언트 코드가 Iterable 또는 Stream을 차단하는 것보다 빨리 도착한 이벤트를 큐에 저장할 수 있습니다.

### 시퀀스를 처리하는 동안 처리 내역 살펴보기

프로세스 파이프라인의 중간에 있는 각 원소나 특정 시그널을 처리해야 하는 경우가 있습니다. 리액터 프로젝트는 이러한 요구 사항을 충족하기 위해 다음과 같은 방법을 제공합니다.

- `doOnNext(Consumer <T>)` 는 Flux나 Mono의 각 원소에 대해 어떤 액션을 수행할 수 있게 해줍니다.
- `doOnComplete()` 및 `doOnError(Throwable)` 은 대응 이벤트 발생 시에 호출됩니다.
- `doOnSubscribe(Consumer<Subscription>)` , `doOnRequest(LongConsumer)` , `doOnCancel(Runnable)` 을 사용하면 구독 라이프 사이클 이벤트에 대응할 수 있습니다.
- `doOnTerminate(Runnable)` 는 스트림 종료 시에 종료의 원인과 관계없이 기동됩니다.

또한 Flux 및  Mono는 리액티브 스트림 도메인의 `onSubscribe` , `onNext` , `onError` , `onComplete` 를 포함한 모든 신호를 처리하는 `doOnEach(Consumer<Signal>)` 메서드를 제공합니다.

```java
@Test
    public void p_160_1(){
        Flux.just(1, 2, 3)
                .concatWith(Flux.error(new RuntimeException("conn error")))
                .doOnEach(s -> log.info("signal: {}", s))
                .subscribe();
    }
```

이 코드에서는 concat 연산자를 사용하는 래퍼인 concatWith 연산자를 사용합니다. 이 코드는 다음과 같은 출력을 생성합니다.

```java
11:02:52.816 [main] INFO chap04.ElementMapTest - signal: doOnEach_onNext(1)
11:02:52.817 [main] INFO chap04.ElementMapTest - signal: doOnEach_onNext(2)
11:02:52.817 [main] INFO chap04.ElementMapTest - signal: doOnEach_onNext(3)
11:02:52.820 [main] INFO chap04.ElementMapTest - signal: onError(java.lang.RuntimeException: conn error)
```

이 예 에서는 onNext 시그널뿐만 아니라 onError 시그널도 모두 수신했습니다.

### 데이터와 시그널 변환하기

때로는 데이터가 아니라 시그널을 이용해 스트림을 처리하는 것이 유용할 때가 있습니다. 데이터 스트림을 시그널 스트림으로 변환하고 다시 되돌리기 위해 Flux 및 Mono는 materialize 및 dematerialize 메서드를 제공합니다. 예를 들면 다음과 같습니다.

```java
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
```

여기에서 시그널 스트림을 처리할 때 doOnNext 메서드는 데이터 있는 onNext 이벤트만 아니라 onComplete 이벤트를 Signal 클래스로 래핑합니다. 이 접근 방식은 동일한 상속 구조 내에 존재하는 onNext, onError 및 onComplete 이벤트를 처리할 수 있습니다.

```java
11:09:03.659 [main] INFO chap04.ElementMapTest - data : 3
11:09:03.659 [main] INFO chap04.ElementMapTest - signal: onNext(3)
11:09:03.659 [main] INFO chap04.ElementMapTest - signal: onComplete()
11:09:03.659 [main] INFO chap04.ElementMapTest - result: [1, 2, 3]
```
## 코드를 통해 스트림 만들기
배열 future, 블로킹 요청을 이용해 리액티브 스트림을 만드는 방법은 이미 다뤘습니다. 그러나 때로는 스트림 내에서 시그널을 생성하거나 객체의 라이프 사이클을 리액티브 스트림의 라이프 사이클에 바인딩하는 것 보다 복잡한 방법이 필요합니다. 이 절에서는 리액터를 이용해 스트림을 프로그래밍 방식으로 생성하는 법을 설명합니다.

### 팩토리 메서드 push 와 create

push 팩토리 메서드를 사용하면 단일 스레드 생성자를 적용해 Flux 인스턴스를 프로그래밍 방식으로 생성할 수 있습니다. 이 접근법은 배압과 cancel에 대한 걱정 없이 비동기,  단일 스레드, 다중 값을 가지는 API를 적용하는 데 유용합니다. 구독자가 부하를 처리할 수 없는 경우 배압과 취소는 모두 큐를 이용해 처리됩니다.

```java
    @Test
    public void p_162(){
        Flux.push(emitter-> IntStream.range(2000, 3000)//1
                .forEach(emitter::next))//1.1
                .delayElements(Duration.ofMillis(1))//2
                .subscribe(e -> log.info("onNext: {}",e));//3
    }
```

1. push 팩토리 메서드를 사용해 기존 API에 리액티브 패러다임을 적용합니다. 단순화를 위해 여기서는 자바 스트림 API를 사용해 1000개의 정수를 생성하고(1.1) FluxSink 타입으로 전송합니다.(1.2) push 메서드 내에서는 배압과 취소에 신경 쓰지 않아도 좋습니다. 푸시 메서드 자체에서 이런 기능을 지원하기 때문입니다.
2. 배압 상황을 시뮬레이션하기 위해 스트림의 각 원소를 지연시킵니다.
3. 여기서는 onNext 이벤트를 구독합니다.

push 팩토리 메서드는 기본 배압 및 취소 전략을 사용해 비동기 API를 적용할 때 유용하게 사용할 수 있습니다. 또한 push 팩토리 메서드와 비슷하게 동작하는 create 팩토리 메서드도 있습니다. 하지만 create 메서드를 사용하면 FluxSink 인스턴스를 추가로 직렬화하므로 다른 스레드에서 이벤트를 보낼 수 있습니다. 두 메서드는 모두 오버플로 전략을 재정의할 수 있으며 다음 코드와 같이 추가적인 핸들러를 등록해 리소스 정리를 활성화할 수도 있습니다.

```java
@Test
    public void p_162_2(){
        Flux.create(emitter->{
            emitter.onDispose(() -> log.info("Disposed"));
        }).subscribe(e->log.info("onNext : {}",e));
    }
```

### 팩토리 메서드 - generate

generate 메서드는 메서드를 호출하는 오브젝트의 내부 전달 상태를 기반으로 복잡한 시퀀스를 만들 수 있도록 설계됐습니다. 이전 값을 기반을 다음 내부 상태를 계산하고 onNext 신호를 다운스트림 구독자에게 전송하기 위해 초깃값과 함수 하나가 필요합니다. 예를 들어 피보나치 시퀀스를 생성하는 간단한 리액티브 스트림을 생성해 보겠습니다.

```java
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
```

1. generate 메서드를 사용해 사용자 정의 리액티브 시퀀스를 생성할 수 있습니다. `Tuples.of(0L, 1L)`를 시퀀스의 초깃값으로 사용합니다.(1,1). 생성 단계에서 state 쌍의 두 번째 값을 참조해 onNext 신호를 보내고(1.2) 피보나치 시퀀스의 다음 값을 기반으로 새로운 state쌍을 다시 계산합니다.(1.3)
2. delayElements 연산자를 사용해 onNext 시그널 중간에 지연 시간을 추가합니다.
3. 예제를 단순화하기 위해 처음 7개 원소만 사용합니다.
4. 시퀀스 생성을 위해 이벤트를 구독합니다.

코드 실행 결과는 다음과 같습니다.

```java
12:32:34.943 [main] INFO chap04.MakeStreamByCodeTest - generated value : 1
12:32:34.943 [main] INFO chap04.MakeStreamByCodeTest - onNext: 1
12:32:34.944 [main] INFO chap04.MakeStreamByCodeTest - generated value : 1
12:32:34.944 [main] INFO chap04.MakeStreamByCodeTest - onNext: 1
12:32:34.944 [main] INFO chap04.MakeStreamByCodeTest - generated value : 2
12:32:34.944 [main] INFO chap04.MakeStreamByCodeTest - onNext: 2
12:32:34.944 [main] INFO chap04.MakeStreamByCodeTest - generated value : 3
12:32:34.944 [main] INFO chap04.MakeStreamByCodeTest - onNext: 3
12:32:34.944 [main] INFO chap04.MakeStreamByCodeTest - generated value : 5
12:32:34.944 [main] INFO chap04.MakeStreamByCodeTest - onNext: 5
12:32:34.944 [main] INFO chap04.MakeStreamByCodeTest - generated value : 8
12:32:34.944 [main] INFO chap04.MakeStreamByCodeTest - onNext: 8
12:32:34.944 [main] INFO chap04.MakeStreamByCodeTest - generated value : 13
12:32:34.944 [main] INFO chap04.MakeStreamByCodeTest - onNext: 13
```

### 일회성 리소스를 리액티브 스트림에 배치

using 팩토리 메서드를 이용하면 일회성 리소스에 의존하는 스트림을 만들 수 있습니다. 이는 리액티브 프로그래밍에서 `try-with-resources` 방식의 접근법이라고 할 수 있습니다. 다음과 같이 단순하게 표현된 `Collection` 클래스로 블로킹 API를 래핑하는 것을 가정해보겠습니다.

```java
@Slf4j
public class Connection implements AutoCloseable{

    private final Random rnd = new Random();

    public Iterable<String> getData(){
        if(rnd.nextInt(10) < 3){
            throw new RuntimeException("Communication Error");
        }
        return Arrays.asList("Some","data");
    }

    @Override
    public void close() throws Exception {
        log.info("IO Connection closed");
    }

    public static Connection newConnection(){
        log.info("IO Connection created");
        return new Connection();
    }
}
```

1. 자바의 try-catch-resources 문을 사용해 새 연결을 만들고 코드 블록을 벗어날 때 자동으로 닫습니다.
2. 비즈니스 데이터를 가져와 처리합니다.
3. 예외가 발생하면 로깅합니다.

```java
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
```

위 코드와 동일한 처리를 하는 리액티브 코드는 다음과 같습니다.

```java
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
```

1. using 팩토리 메서드를 사용하면 Connection 인스턴스 라이프 사이클을 스트림의 라이프 사이클에 래핑할 수 있습니다. using 메서드는 일회성 리소스를 만드는 방법을 알아야합니다. 이 경우 (1.1)이 그에 해당합니다. 그런 다음 방금 생성된 리소스를 리액티브 스트림으로 변환하는 방법을 알아야 합니다. 이 경우 fromIterable 메서드를 호출합니다. (1.2) 처리가 끝나면 Connection 인스턴스의 close 메서드를 호출합니다.(1.3)
2. 실제 처리를 시작하여면 onNext, onError, onComplete 시그널에 대한 핸들러를 사용해 구독을 생성해야 합니다.

```java
13:10:22.930 [main] INFO chap04.dto.Connection - IO Connection created
13:10:22.933 [main] INFO chap04.MakeStreamByCodeTest - Recieved data: Some
13:10:22.934 [main] INFO chap04.MakeStreamByCodeTest - Recieved data: data
13:10:22.934 [main] INFO chap04.dto.Connection - IO Connection closed
13:10:22.934 [main] INFO chap04.MakeStreamByCodeTest - Stream finished
```

using 연산자는 스트림 종료 시그널을 보내기 전 후에 정리 작업을 할 지 선택할 수 있게 합니다.

### usingWhen 팩토리를 사용해 리액티브 트랜잭션 래핑

using 연산자와 마찬가지로 usingWhen 연산자를 사용해 수동으로 자원을 관리할 수 있습니다. using 연산자는 (Callable 인스턴스를 호출해) 관리 자원을 동기적으로 검색합니다. 반면 usingWhen 연산자는 (Publisher의 인스턴스에 가입해) 관리되는 리소스를 리액티브 타입으로 검색합니다. 또한 usingWhen연산자는 메인 스트림의 성공 및 실패에 대해 각각 다른 핸들러를 사용할 수 있습니다. 이러한 핸들러는 publisher를 이용해 구현합니다. 이 차이점 때문에 단 하나의 연산자만으로 완전한 논블로킹 리액티브 트랜잭션을 구현할 수 있습니다.

전체적으로 리액티브 트랜잭션을 구현한다고 가정해봅시다.

```java
@Slf4j
public class Transaction {
    private static final Random random = new Random();
    private final int id;

    public Transaction(int id) {
        this.id = id;
        log.info("[T: {} created]",id);
    }
    public static Mono<Transaction> beginTransaction(){// 1
        return Mono.defer(()->
            Mono.just(new Transaction(random.nextInt(1000))));
    }
    public Flux<String> insertRows(Publisher<String> rows){// 2
        return Flux.from(rows)
                .delayElements(Duration.ofMillis(100))
                .flatMap(r->{
                    if(random.nextInt(10) <2){
                        return Mono.error(new RuntimeException("Error :"+r));
                    }else{
                        return Mono.just(r);
                    }
                });
    }
    public Mono<Void> commit(){// 3
        return Mono.defer(()->{
            log.info("[T: {}] commit",id);
            if(random.nextBoolean()){
                return Mono.empty();
            }else{
                return Mono.error(new RuntimeException("Conflict"));
            }
        });
    }
    public Mono<Void> rollback(){// 4
        return Mono.defer(()->{
           log.info("[T: {}] rollback", id);
           if(random.nextBoolean()){
               return Mono.empty();
           }else{
               return Mono.error(new RuntimeException("Conn Error"));
           }
        });
    }
}
```

1. 새 트랜잭션을 작성할 수 있는 정적 팩토리 입니다.
2. 각 트랜잭션에서는 트랜잭션 내에서 새로운 row를 저장하는 메서드가 있습니다. 일부 내부 문제(임의 동작)으로 인해 프로세스가 실패하는 경우가 있습니다. insertRows는 리액티브 스트림을 사용하고 반환합니다.
3. 비동기 commit 메서드 입니다. 경우에 따라 트랜잭션이 커밋되지 않을 수 있습니다.
4. 비동기 rollback 메서드입니다. 트랜잭션이 롤백되지 못할 수 있습니다.

```java
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

        Thread.sleep(5000); // 충분한 대기 시간 (딜레이와 트랜잭션 처리 시간 포함)
    }
```

1. beginTransaction 정적 메서드는 Mono<Transaction> 타입을 반환하므로 비동기적으로 새로운 트랜잭션을 반환합니다.
2. 주어진 트랜잭션 인스턴스에서 새로운 데이터를 삽입하려고 시도합니다.
3. 단계2가 성공한 경우 트랜잭션을 커밋합니다.
4. 단계2가 실행한 경우 롤백합니다.

위 코드에서 커밋에 실패했을 경우 아래와 같이 로그가 표시됩니다.

```java
13:52:44.403 [parallel-1] INFO chap04.MakeStreamByCodeTest - onNext: A
13:52:44.514 [parallel-2] INFO chap04.dto.Transaction - [T: 627] rollback
13:52:44.515 [parallel-2] INFO chap04.MakeStreamByCodeTest - orError: Error :B
```

usingWhen 연산자를 사용하면 완벽하게 리액티브한 방법으로 자원의 라이프 사이클을 관리할 수 있습니다.
### 에러 처리하기
외부 서비스와 많은 커뮤니케이션을 하는 리액티브 애플리케이션을 설계할 때는 모든 종류의 예외 상황을 처리해야 합니다. 다행히 onError 시그널은 리액티브 스트림 스펙의 필수 요소라서 예외를 처리할 수 있는 경로로 전파할 수 있습니다. 그러나 최종 구독자가 onError 시그널에 대한 핸들러를 정의하지 않으면  onError는 `UnsupportedOperationException` 을 발생시킵니다.

또한 리액티브 스트림은 onError 스트림이 종료됐다고 정의하고 있기 때문에 시그널을 받으면 시퀀스가 실행을 중지합니다. 이 시점에서 다음 전략 중 하나를 적용해 다른 방식으로 대응할 수 있습니다.

- subscribe 연산자에서 onError 신호에 대한 핸들러를 정의해야 합니다.
- onErrorReturn 연산자를 사용하면 예외 발생 시 사전 정의된 정적 ㄱ밧 또는 예외로 계산된 값으로 대체할 수 있습니다.
- onErrrorResume 연산자를 적용해 예외를 catch하고 대체 워크플로를 실행할 수 있습니다.
- onErrorMap 연산자를 사용해 예외를  catch하고 상황을 잘 나타내는 다른 예외로 변환할 수 있습니다.
- 오류가 발생할 경우 다시 실행을 시도하는 리액티브 워크 플로를 정의할 수 있습니다. retry 연산자는 오류 시그널을 보내는 경우 소스 리액티브 시퀀스를 다시 구독합니다. 재시도는 무한대로 하거나 제한된 시간 동안 할 수 있습니다. retryBackoff 연산자는 지수적인 백오프 알고리즘을 지원해 재시도할 때 마다 대기 시간을 증가시킬 수 있습니다.

또한 비어 있는 스트림은 때때로 원하는 입력이 아닐 수 있습니다. 이 경우 defaultIfEmpty 연산자를 사용해 기본값을 반환하거나 switchIfEmpty 연산자를 사용해 완전히 다른 리액티브 스트림을 반환할 수 있습니다.timeout 연산자를 사용하면 작업 대기 시간을 제한하고 `TimeoutException` 을 발생시킬 수 있습니다. 그에 따라 시간이 오래 걸리는 경우 적절한 전약으로 대응할 수 있습니다. 지금 까지 전략의 일부를 예제를 통해 확인해 보겠습니다.

```java
@Slf4j
public class BookService {
    static final Random random = new Random();

    static Flux<String> recommendBooks(String userId){
        return Flux.defer(()->{
            if(random.nextInt(10)<7){// 1
                return Flux.<String>error(new RuntimeException("Error"))// 2
                        .delaySequence(Duration.ofMillis(100));
            }else {
                return Flux.just("Blue Mars", "The Expanse")// 3
                        .delayElements(Duration.ofMillis(50));
            }
        }).doOnSubscribe(s->log.info("Request for {}",userId));// 4
    }
}
```

1. 구독자가 도착할 때까지 계산을 연기합니다.
2. 신뢰할 수 없는 서비스로 인해 오류가 발생할 가능성이 큽니다. 그러나 delaySequence 연산자를 적용해 모든 시그널을 지연 시킵니다.
3. 고객이 운이 좋으면 약간의 지연 시간 이후에 추천 결과를 받습니다.
4. 서비스 요청 내역을 기록합니다.

```java
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
```

1. 여기에서는 영화 추천을 요청하는 사용자 스트림을 생성합니다.
2. 각 사용자에 대해 신뢰할 수 없는 recommendedBooks 서비스를 호출합니다.(2.1) 만약 호출이 실패하면 지수적인 백오프로 재시도 합니다. 그러나 재시도 전략이 3초 후에도 아무런 결과를 가져오지 않으면 오류 시그널이 발생합니다. (2.3) 마지막으로 오류가 발생하면 onErrorResume 연산자를 사용해 사전에 정의된 보편적인 추천 영화를 반환합니다.(2.4)
3. 물론 구독을 생성해야 한다.

```java
17:31:17.949 [main] INFO chap04.dto.BookService - Request for user-1
17:31:18.082 [parallel-2] INFO chap04.dto.BookService - Request for user-1
17:31:18.386 [parallel-3] INFO chap04.dto.BookService - Request for user-1
17:31:18.446 [parallel-4] INFO chap04.MakeStreamByCodeTest - onNext : Blue Mars
17:31:18.509 [parallel-6] INFO chap04.MakeStreamByCodeTest - onNext : The Expanse
17:31:18.509 [parallel-6] INFO chap04.MakeStreamByCodeTest - onComplete
```

로그를 보면 서비스가 user-1에 대한 추천을 시도 했음을 알 수 있습니다.

또한 재시도 지연이 150밀리초에서 1.5초로 증가했습니다. 마지막으로 코드는 recommendBooks 메서드에서 결과를 검색하는 것을 중지하고 기본 값을 반환하고 스트림을 완료합니다.

요약하면, 리액터 프로젝트는 예외적인 상황을 처리할 수 있게 해주고 겨로가적으로 응용 프로그램의 복원력을 향상시키는 데 도움이 되는 다양한 도구를 제공합니다.

### 배압 다루기

리액티브 스트림 스펙에서는 프로듀서와 컨슈머 간의 의사소통에 배압이 필요하지만, 컨슈머에서 오버플로가 발생할 가능성은 여전히 존재합니다. 일부 컨슈머는 제한되지 않은 데이터에 순진하게 응답한 다음 생성된 부하를 처리하지 못합니다. 일부 컨슈머는 수신 메시지 비율에 대해 엄격한 제한을 가하기도 합니다. 예를 들어, 일부 데이터베이스 클라이언트는 초당 1000개가 넘는 레코드를 삽입하지 못하도록 제한합니다. 이 경우 배치 처리 기법이 도움이 될 수도 있습니다. 또는 다음과 같은 방법으로 배압을 처리하도록 스트림을 구성할 수 있습니다.

- onBackPressureBuffer 연산자는 제한되지 않은 요구를 요청하고 결과를 다운 스트림으로 푸시합니다. 그러나 다운스트림 컨슈머의 부하를 유지할 수 없는 경우 큐를 이용해 버퍼링합니다. onBackPresureBuffer 연산자는 여러가지 매개변수를 이용해 다양한 옵션을 제공하므로 동작을 쉽게 조정할 수 있습니다.
- onBackPressureDrop 연산자는 제한되지 않은 요구를 요청하고 데이터를 하위로 푸쉬합니다. 다운스트림의 처리 용량이 충분하지 않으면 일부 데이터가 삭제됩니다. 사용자 정의 핸들러를 사용해 삭제된 원소를 처리할 수 있습니다.
- onBackPressure 연산자는 onBackPressureDrop과 유사하게 동작합니다. 그러나 가장 최근에 수신된 원소를 기억하고 요청이 발생하면 이를 다운스트림으로 푸시합니다. 오버 플로 상황에서도 항상 최신 데이터를 수신하는 데 도움이 될 수 있습니다.
- onBackPressureError 연산자는 데이터 다운스트림으로 푸시하는 동안 크기를 제한하지 않고 요청합니다. 다운스트림 컨슈머가 처리를 계속 유지할 수 없으면 게시자는 오류를 발생합니다.

배압을 관리하는 또 다른 방법은 속도 제한 기술을 사용하는 것입니다.

limitRate(n) 연산자는 다운 스트림 수요를 n보다 크지 않은 작은 규모로 나눕니다. 이렇게 하면 다운 스트림 컨슈머의 부적절한 규모 데이터 요청으로부터 섬세한 게시자를 보호할 수 있습니다. limitRequest(n) 연산자를 이용하면 다운스트림 컨슈머의 수요(총 요청 값)을 제한할 수 있습니다. 예를 들어 limitRequest(100)은 게시자가 총 100개 이상의 원소에 대해 요청되지 않도록 합니다. 100개의 이벤트를 전송한 후 게시자는 스트림을 성공적으로 닫습니다.

### Hot 스트림과 Cold 스트림

리액티브 게시자에 대해 이야기할 때 게시자를 hot과 cold 두가지 유형으로 분류할 수 있습니다.

콜드 퍼블리셔는 구독자가 나타날 때마다 해당 구독자에 대해 모든 시퀀스 데이터가 생성되는 방식으로 동작합니다. 또한 콜드 퍼블리셔의 경우 구독자 없이는 데이터가 생성되지 않습니다.

```java
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
```

코드 실행 결과는 다음과 같습니다.

```java
17:56:27.896 [main] INFO chap04.MakeStreamByCodeTest - Generating new items
17:56:28.252 [main] INFO chap04.MakeStreamByCodeTest - onNext : d9d089ad-7f82-466a-830d-7e1eaa3f4f60
17:56:28.254 [main] INFO chap04.MakeStreamByCodeTest - Generating new items
17:56:28.254 [main] INFO chap04.MakeStreamByCodeTest - onNext : 3d9c364f-e534-42ef-9ba4-97eec06f046c
17:56:28.254 [main] INFO chap04.MakeStreamByCodeTest - Data was generated twice for two subscribers
```

결과를 보면 알 수 있듯이 구독자가 나타날 때 마다 새로운 시퀀스가 생성됩니다. 대표적으로 HTTP 요청이 이런식으로 동작합니다. 새로운 구독자가 HTTP 요청을 할 때까지 호출이 생성되지 않습니다.

### 스트림 원소를 여러 곳으로 보내기

콜드 퍼블리셔를 핫 퍼블리셔로 전환할 수 있습니다. 예를 들어 콜드 퍼블리셔의 결과를 데이터 생성 준비가 완료되는 대로 일부 구독자에게 공유하는 경우가 있습니다. 또한 여기서는 각 구독자를 위해 중복된 데이터를 생성하지 않으려고 합니다. 리액터 프로젝트에는 이러한 용도로 ConnectableFlux가 있습니다. ConnectableFlux를 이용하면 가장 수요가 많은 데이터를 생성하고 다른 모든 가입자가 데이터를 처리할 수 있도록 캐싱합니다.

ConnectableFlux의 동작을 예제로 설명해 보겠습니다.

```java
 @Test
    public void p_174(){
        Flux<Integer> source = Flux.range(0,3)
                .doOnSubscribe(s -> log.info("new subscription for the cold publisher"));

        ConnectableFlux<Integer> conn = source.publish();

        conn.subscribe(e -> log.info("[Subscriber 1] onNext: {}", e));
        conn.subscribe(e -> log.info("[Subscriber 2] onNext: {}", e));

        log.info("all subscribers are ready, connecting");
        conn.connect();
    }
```

콜드 퍼블리셔는 구독을 받았으며 결과적으로 한 번만 항목을 생성했습니다. 그러나 두 구독자는 각각 이벤트 집합을 받았습니다.

```java
08:57:21.578 [main] INFO chap04.MakeStreamByCodeTest - all subscribers are ready, connecting
08:57:21.581 [main] INFO chap04.MakeStreamByCodeTest - new subscription for the cold publisher
08:57:21.583 [main] INFO chap04.MakeStreamByCodeTest - [Subscriber 1] onNext: 0
08:57:21.584 [main] INFO chap04.MakeStreamByCodeTest - [Subscriber 2] onNext: 0
08:57:21.584 [main] INFO chap04.MakeStreamByCodeTest - [Subscriber 1] onNext: 1
08:57:21.584 [main] INFO chap04.MakeStreamByCodeTest - [Subscriber 2] onNext: 1
08:57:21.584 [main] INFO chap04.MakeStreamByCodeTest - [Subscriber 1] onNext: 2
08:57:21.584 [main] INFO chap04.MakeStreamByCodeTest - [Subscriber 2] onNext: 2
```

### 스트림 내용 캐싱하기

ConnectableFlux를 사용하면 다양한 데이터 캐싱 전략을 쉽게 구현할 수 있습니다. 그러나 리액터에는 이벤트 캐싱을 위한 연산자로 cache가 존재합니다. 내부적으로는 cache 연산자는 ConnectableFlux를 사용하므로 간단한 연쇄형 API는 덤으로 얻을 수 있습니다. 캐시가 보유할 수 있는 데이터의 양과 캐시된 각 항목의 만료 시간을 조정할 수 있습니다. 다음 예제를 통해 작동 원리를 설명하겠습니다.

```java
@Test
    public void p_175() throws InterruptedException {
        Flux<Integer> source = Flux.range(0, 2)// 1
                .doOnSubscribe(s -> log.info("new subscription for the cold publisher"));
        Flux<Integer> cachedSource = source.cache(Duration.ofSeconds(1));// 2
        cachedSource.subscribe(e-> log.info("[s 1] onNext : {}",e));// 3
        cachedSource.subscribe(e-> log.info("[s 2] onNext : {}",e));// 4
        Thread.sleep(1200);// 5
        cachedSource.subscribe(e-> log.info("[s 3] onNext : {}",e));// 5
    }
```

1. 일단 몇 가지 아이템을 만들 콜드 퍼블리셔를 만듭니다.
2. 1초 동안 cache연산자로 콜드 퍼블리셔를 캐시 합니다.
3. 첫 번째 가입자를 연결합니다.
4. 첫 번째 가입자 연결 직후에 두 번째 가입자를 연결합니다.
5. 캐시된 데이터가 만료될때까지 잠시 기다립니다.
6. 마지막 세 번째 가입자를 연결합니다.

```java
09:08:35.246 [main] INFO chap04.MakeStreamByCodeTest - new subscription for the cold publisher
09:08:35.246 [main] INFO chap04.MakeStreamByCodeTest - [s 1] onNext : 0
09:08:35.247 [main] INFO chap04.MakeStreamByCodeTest - [s 1] onNext : 1
09:08:35.248 [main] INFO chap04.MakeStreamByCodeTest - [s 2] onNext : 0
09:08:35.248 [main] INFO chap04.MakeStreamByCodeTest - [s 2] onNext : 1
09:08:36.452 [main] INFO chap04.MakeStreamByCodeTest - new subscription for the cold publisher
09:08:36.452 [main] INFO chap04.MakeStreamByCodeTest - [s 3] onNext : 0
09:08:36.452 [main] INFO chap04.MakeStreamByCodeTest - [s 3] onNext : 1
```

로그를 기반으로 처음 두 구독자가 첫 번째 구독의 동일한 캐시된 데이터를 공유했다고 결론을 내릴 수 있습니다. 그런 다음 지연 시간이 지난 후 세 번째 구독자가 캐시된 데이터를 검색할 수 없어 콜드 퍼블리셔에 대해새로운 구독이 발생했습니다.

### 스트림 내용 공유

ConnectableFlux를 사용해 여러 개의 구독자에 대한 이벤트를 멀티 캐스트합니다. 그러나 구독자가 나타나고 나서야 처리가 시작됩니다. share 연산자를 사용하면 콜드 퍼블리셔를 핫 퍼블리셔로 변환할 수 있습니다. share 연산자는 구독자가 각 신규 구독자에게 이벤트를 전파하는 방식으로 작동 합니다.

다음 예제를 보겠습니다.

```java
@Test
    public void p_177() throws InterruptedException {
        Flux<Integer> source= Flux.range(0, 5)
                .delayElements(Duration.ofMillis(100))
                .doOnSubscribe(s ->
                        log.info("new subscription for the cold publisher"));

        Flux<Integer> cachedSource = source.share();//첫 번째 구독자가 생기는 순간 데이터 흐름 시작
        cachedSource.subscribe(e -> log.info("[s 1] onNext : {}", e));
        Thread.sleep(400);
        cachedSource.subscribe(e -> log.info("[s 2] onNext : {}", e));
        Thread.sleep(2000);
    }
```

이 코드에서는 100밀리초마다 이벤트를 생성하는 콜드 스트림을 공유했습니다. 그런 다음 약간의 지연 시간을 두고 두명의 구독자가 공유된 게시자를 구독합니다.

로그에서 첫 번째 구독자가 0 부터 시작해 이벤트를 수신하기 시작한 반면 두 번째 구독자는 자신이 생성되기 전에 발생한 이벤트는 수신하지 못했습니다.

```java
09:26:54.558 [main] INFO chap04.MakeStreamByCodeTest - new subscription for the cold publisher
09:26:54.687 [parallel-1] INFO chap04.MakeStreamByCodeTest - [s 1] onNext : 0
09:26:54.798 [parallel-2] INFO chap04.MakeStreamByCodeTest - [s 1] onNext : 1
09:26:54.909 [parallel-3] INFO chap04.MakeStreamByCodeTest - [s 1] onNext : 2
09:26:55.019 [parallel-4] INFO chap04.MakeStreamByCodeTest - [s 1] onNext : 3
09:26:55.019 [parallel-4] INFO chap04.MakeStreamByCodeTest - [s 2] onNext : 3
09:26:55.130 [parallel-5] INFO chap04.MakeStreamByCodeTest - [s 1] onNext : 4
```

### 시간 다루기

리액티브 프로그래밍은 비동기적이므로 본질적으로 시간의 축이 있다고 가정합니다. 리액터 프로젝트를 사용하면 interval 연산자로 주기적으로 이벤트를 생성하고 delayElements 연산자로 원소를 지연시킬 수 있으며 delaySequence 연산자로 신호를 지연 시킬 수 있습니다. 리액터의 API를 사용하면 앞서 설명한 timestamp 및 timeout 같은 연산자를 이용해 시간 관련 이벤트를 처리할 수 있습니다. timestamp와 마찬가지로 elapsed 연산자는 이전 이벤트와의 시간 간격을 측정합니다. 다음 코드를 살펴보겠습니다.

```java
@Test
    public void p_178() throws InterruptedException {
        Flux.range(0, 5)
                .delayElements(Duration.ofMillis(100))
                .elapsed()
                .subscribe(e ->log.info("Elapsed {} ms: {}",e.getT1(),e.getT2()));

        Thread.sleep(1000);

    }
```

위 결과로 볼 때 이벤트가 100밀리초 간격 이내 정확하게 도착하지 않는다는 것이 분명합니다. 이것은 리액터가 예정된 이벤트에 대해 자바의  `ScheduledExecutorService` 를 사용하기 때문에 그렇습니다. 따라서 리액터 라이브러리에서 너무 정확한 시간을 요구하지 않도록 주의해야 합니다.

### 리액티브 스트림을 조합하고 변환하기

복잡한 리액티브 워크플로를 구축할 때 서로 다른 위치에서 동일한 순서의 연산자를 사용해야 할 때가 있습니다. transform 연산자를 활용하면 이런 공통 부분을 별도의 객체로 추출해 필요할 때 마다 재사용할 수 있습니다.

이전까지는 스트림 내에서 이벤트를 변환했습니다. transform 연산자를 이용하면 스트림 구조 자체를 강화할 수 있습니다.

```java
@Test
    public void p_179(){
        Function<Flux<String>,Flux<String>> logUserInfo = // 1
                stream -> stream.index()// 1.1
                        .doOnNext(// 1.2
                        tp -> log.info("[{}] User : {}",tp.getT1(), tp.getT2())
                ).map(Tuple2::getT2);// 1.3
        Flux.range(1000, 3)// 2
                .map(i -> "user-"+i)
                .transform(logUserInfo)// 3
                .subscribe(e -> log.info("onNext : {}", e));
    }
```

1. `Function<Flux<String>,Flux<String>>` 타입을 반환하는 logUserInfo 함수를 정의합니다. 이 함수는 String 타입의 리액티브 스트림으로 변환하면서 String 값을 생성합니다. 이 예제에서는 onNext 시그널에 대해 함수가 사용자에 대한 세부 정보를 로깅하고(1.2) index 연산자로 수신 이벤트를 열거형으로 전환합니다.(1.1) 최종적으로 발신되는 스트림에는 index()에 의해 변환된 정보가 포함돼 있지 않습니다. 그 이유는 map(Tuple2::getT2)가 호출되면서 이 정보가 제거됐기 때문입니다. (1.3)
2. 사용자 ID를 생성합니다.
3. transform 연산자를 적용해 logUserInfo 함수로 정의된 변환을 적용합니다.

```java
09:47:06.332 [main] INFO chap04.MakeStreamByCodeTest - [0] User : user-1000
09:47:06.332 [main] INFO chap04.MakeStreamByCodeTest - onNext : user-1000
09:47:06.332 [main] INFO chap04.MakeStreamByCodeTest - [1] User : user-1001
09:47:06.333 [main] INFO chap04.MakeStreamByCodeTest - onNext : user-1001
09:47:06.333 [main] INFO chap04.MakeStreamByCodeTest - [2] User : user-1002
09:47:06.333 [main] INFO chap04.MakeStreamByCodeTest - onNext : user-1002
```

각 원소는 logUserInfo 함수에서 한 번 로깅되고 최족적으로 구독을 통해 한 번 더 기록됩니다. transform 연산자는 스트림 라이프 사이클의 결합 단계에서 스트림 동작을 한 번만 변경합니다. 리액터에는 똑같은 일에 하는 composer 연산자가 있습니다. 이 연산자는 구독자가 도착할 때마다 동일한 스트림 변환 작업을 수행합니다. 다음 코드를 보면서 동작 원리를 살펴보겠습니다.

```java
@Test
    public void p_180(){
        Random random = new Random();
        Function<Flux<String>, Flux<String>> logUserInfo = (stream) ->{// 1
            if(random.nextBoolean()){
                return stream.doOnNext(e -> log.info("[path A] User : {}", e));
            }else{
                return stream.doOnNext(e -> log.info("[path B] User : {}", e));
            }
        };
        Flux<String> publisher = Flux.just("1", "2")// 2
                .compose(logUserInfo);// 3
        publisher.subscribe();// 4
        publisher.subscribe();
    }
```

1. 이 예제에서는 함수가 매번 스트림 변환 방법을 임의로 선택합니다. 두 경로는 로그 메시지 접두사만 다릅니다.
2. 데이터를 생성하는 게시자를 만듭니다.
3. compose 연산자를 사용해 logUserInfo 함수를 실행 워크 플로에 포함합니다.
4. 다른 행동을 관찰하기 위해 2번 구독합니다.

```java
10:08:22.636 [main] INFO chap04.MakeStreamByCodeTest - [path B] User : 1
10:08:22.636 [main] INFO chap04.MakeStreamByCodeTest - [path B] User : 2
10:08:22.637 [main] INFO chap04.MakeStreamByCodeTest - [path A] User : 1
10:08:22.637 [main] INFO chap04.MakeStreamByCodeTest - [path A] User : 2
```

로그 메시지를 통해 첫 번째 구독이 경로 B를 두 번째 구독이 경로 A를 거쳤음을 알 수 있습니다. 물론 compose 연산자를 사용하면 로그 메시지 접두어를 무작위로 선택하는 것보다 복잡한 비즈니스 로직을 구현할 수 있습니다. transform 및 compose는 리액티브 응용 프로그램에서 코드를 재사용할 수 있는 강력한 도구 입니다.
## 리액터 프로젝트 심화학습
리액티브 스트림의 수명 주기, 멀티스레딩, 리액터 프로젝트에서 내부 최적화가 작동하는 방식에 대해 알아봅시다.

### 리액티브 스트림의 수명 주기

리액터에서 멀티스레딩이 작동하는 방법과 내부 최적화가 구현되는 방법을 이해하기 위해서는 리액터에서 리액티브 타입의 수명 주기를 이해해야 합니다.

### 조립 단계

스트림 수명 주기의 첫 번째 부분은 조립 단계입니다. 앞 절에서 학습한 것 처럼 리액터는 복잡한 처리 흐름을 구현할 수 있는 연쇄형 API를 제공합니다. 언뜻 보면 리액터가 제공하는 API는 처리 흐름에서 사용하는 연산자를 조합한 빌더 API 처럼 보입니다. 빌더 패턴은 가변적인 객체를 생성하며, 다른 객체를 생성하기 위해 build()와 같은 최종 함수를 호출하는 것이 일반적입니다. 일반적인 빌더 패턴과 달리 리액터 API는 불변성을 제공합니다. 따라서 적용된 각각의 연산자가 새로운 객체를 생성합니다. 따라서 적용된 각각의 연산자가 새로운 객체를 생성합니다. 리액티브 라이브러리에서 실행 흐름을 작성하는 프로세스를 조립(assembling) 이라고 합니다. 조립 방법을 더 잘 이해하기 위해 리액터 빌더 API가 없는 경우의 조립 방식을 보여주는 다음 코드를 살펴봅시다.

```java
@Test
    public void p_185(){
        Flux<Integer> sourceFlux = new FluxArray(1, 20 ,30,4000);
        Flux<String> mapFlux = new FluxMap(sourceFlux, String::valueOf);
        Flux<String> filterFlux = new FluxFilter(mapFlux, s->length() > 1);
    }
```

이 코드는 연쇄형 빌더 API가 없는 경우 코드를 작성하는 방법을 보여줍니다. 여러 Flux가 내부적으로 서로 조합돼 있음은 분명 합니다. 조립 프로세스가 끝나면 다음 게시자가 이전 게시자를 참조하는 게시자 체인이 생성됩니다. 아래 의사 코드는 이를 보여줍니다.

```java
 public void p_185_2(){
        FluxFilter(
                FluxMap(
                        FluxArray(1, 2, 3, 40, 500, 6000)
                )
        )
    }
```

앞의 코드는 Flux에 just → map → filter와 같은 순서로 연산자를 적용하면 결과가 어떤 형태가 될지를 보여줍니다. 스트림 수명 주기에서 조립 단계가 중요한 이유는 스트림의 타입을 확인해 연산자를 서로 바꿀 수 있기 때문입니다. 다음 코드는 리액터에서 이를 어떻게 수행하는 지 보여줍니다.

```java
public final Flux<T> concatWith(Publisher<? extends T> other) {
        if (this instanceof FluxConcatArray) {
            FluxConcatArray<T> fluxConcatArray = (FluxConcatArray)this;
            return fluxConcatArray.concatAdditionalSourceLast(other);
        } else {
            return concat(this, other);
        }
    }
```

매개변수로 받은 Flux가 FluxConcatArray 인스턴스인 경우 `FluxConcatArray(FluxConcatArray(FluxA, FluxB), FluxC)` 로 동작하는 것이 아니라 하나의 `FluxConcatArray(FluxA, FluxB, FLuxC)` 를 만들어 전체적인 성능을 향상시킵니다.

또한 조립 단계에서 스트림에 몇 가지 훅을 사용하면 디버깅이나 스트림 모니터링 중에 유용한 로깅, 추적, 메트릭 수집 또는 기타 중용한 기능을 사용할 수 있습니다.

리액티브 스트림 수명 주기 중 조립 단계의 역살을 요학하면 스트림 구성을 조작하고 리액티브 시스템을 구축하는 데 필수적인 디버깅 최적화나 모니터링, 더 나은 스트림 전달을 위한 다양한 기술을 적용할 수 있는 단계라고 할 수 있습니다.

### 구독단계

구독은 특정 publisher를 구독할 때 발생합니다.

```java
filteredFlux.subscribe();
```

실행 플로를 만들기 위해 내부적으로 Publisher를 다른 Publisher에게 전달합니다. 따라서 일련의 Publisher 체인이 있다고 할 수 있습니다. 일단 최상위 래퍼를 구독하면 해당 체인에 대한 구독 프로세스가 시작됩니다. 다음 코드는 구독 단계 동안 Subscriber 체인을 통해 Subscriber가 전파되는 방식을 보여줍니다.

```java
filterFlux.subscribe(Subscriber){
	mapFlux.subscribe(new FilterSubscriber(Subscriber)){
		arrayFlux.subscribe(new MapSubscriber(FilterSubscriber(Subscriber))){
			// 여기에서 실제 데이터를 송신하기 시작합니다.
		}
	}
}
```

이 코드는 조립된 Flux 내부에서 구독 단계 동안 발생하는 상황을 보여줍니다. 보다시피 filteredFlux.subscribe 메서드를 실행하면 각 내부 Publisher에 대한 subscribe 메서드가 실행됩니다.주석이 있는 행에서 실행이 끝나고 나면 내부에는 다음과 같이 연결된 Subscriber 시퀀스가 존재합니다.

```java
ArraySubscriber(
	MapSubscriber(
		FilterSubscriber(
			Subscriber
		)	
	)
)
```

Subscriber 피라미드 맨위에 ArraySubscriber 래퍼가 있습니다. Flux 피라미드의 경우에는 FluxArray가 중간에 존재합니다.

구독 단계가 중요한 이유는 이 단계에서 조립 단계와 동일한 최적화를 수행할 수 있기 때문입니다. 또다른 중요한 점은 리액터에서 멀티 스레딩을 지원하는 일부 연산자는 구독이 발생하는 작업자를 변경할 수 있다는 것입니다.

### 런타임 단계

스트림 실행의 마지막 단계는 런타임 단계입니다. 이 단계에서 게시자와 구독자 간에 실제 신호가 교환됩니다. 리액티브 스트림 스펙에 정의돼 있듯이 게시자와 구독자가 교환하는 처음 두 신호는 onSubscribe 시그널과 request 시그널 입니다.

onSubscribe 메서드는 최상위 소스에서 호출됩니다. 구독자는 메서드를 호출해 구독을 시작합니다. 이 과정을 설명하는 의사 코드는 다음과 같습니다.

```java
MapSubscriber(filterSubscriber(Subscriber).onSubscribe(
	new ArraySubscription()
){
	FilterSubscriber(Subscriber).onSubscribe(
		new MapSubscription(ArraySubscription())
	){
		Subscriber.onSubscribe(
			FilterSubscription(MapSubscription(ArraySubscription()))
		){
			// 여기에 요청 데이터를 기술합니다.
		}
	}
}
```

구독이 모든 구독자 체인을 통과하고 체인에 포함된 각 구독자가 지정된 구독을 자신의 표현으로 래핑하면 최종적으로 다음 코드와 같이 Subscription 래퍼의 피라미드를 얻습니다.

```java
FilterSubscription(
	MapSubscription(
		ArraySubscription()
	)
)
```

마지막으로, 마지막 구독자가 구독 체인에 대한 정보를 수신하고 메시지를 시작하면 Subscription#request 메서드를 호출해 전송을 시작해야 합니다. 다음 의사 코드는 request가 어떻게 동작하는 지 보여 줍니다.

```java
FilterSubscription(MapSubscription(ArraySubscription(...)))
	.request(10){
		MapSubscription(ArraySubscription(...))
		.request(10) {
			ArraySubscription(...)
			.request(10){
				// 데이터 전송 시작
			}
		}
	}
```

모든 구독자가 요청한 수요를 통과하고 ArraySubscription이 이를 수신하고 나면 ArrayFlux는 데이터를 MapSubscriber(FilterSubscriber(Subscriber)) 체인으로 보내기 시작합니다. 다음은 모든 구독자를 통해 데이터를 보내는 프로세스를 설명하는 의사 코드 입니다.

```java
ArraySubscription.request(10){
	MapSubscriber(FilterSubscriber(Subscriber)).onNext(1){
		//데이터 변환 로직을 작성
		FilterSubscriber(Subscriber).onNext("1"){
			//필터 처리
			//원소가 일치하지 않으면
			//추가 데이터를 요청
			MapSubscription(ArraySubscription(...)).request(1).{...}
		}
	}
	
	MapSubscription(FilterSubscriber(Subscriber)).onNext(20){
		//데이터 변환 로직을 작성
		FilterSubscriber(Subscriber).onNext("20"){
			//필터 처리
			//원소가 일치하면
			//다운스트림 구독자에게 전송
			Subscriber.onNext("20"){...}
		}
	}
}
```

이 코드에서 알 수 있듯이, 런타임 중에 데이터는 소스로부터 각 Subscriber 체인을 거쳐 단계마다 다른 기능을 실행합니다.

런타임 단계가 중요한 이유는 런타임 중에 신호 교환량을 줄이기 위한 최적화를 적용할 수 있기 때문입니다. 예를 들어 Subscription#request 호출 횟수를 줄임으로써 스트림의 성능을 향상시킬 수 있습니다.

## 리액터에서 스레드 스케줄링 모델
리액터가 멀티스레딩 실행을 위해 제공하는 연산자와 연산자 사이의 차이점을 알아보겠습니다.

### publishOn 연산자

publishOn 연산자는 런타임 실행의 일부를 지정된 워커로 이동할 수 있게 해줍니다.

- 스케줄러의 기본 개념이 작업을 동일한 스레드의 큐에 넣는 것이기 때문에 여기서 스레드라는 단어는 사용하지 않겠습니다. 하지만 실제 작업 실행은 스케줄러 인스턴스 관점에서 다른 워커에 의해 수행될 수 있습니다.

리액터는 런타임에 데이터를 처리할 워커를 지정하기 위해 Scheduler라는 개념을 도입했습니다. Scheduler는 리액터 프로젝트에서 워커 또는 워커 풀을 나타내는 인터페이스입니다. publishOn 연산자를 사용하는 방법을 더 잘 이해하기 위해 아래 예제 코드를 살펴보겠습니다.

```java
public void p_190_1(){
        Scheduler scheduler = new Scheduler() {// 1
            @Override
            public Disposable schedule(Runnable runnable) {
                return null;
            }

            @Override
            public Worker createWorker() {
                return null;
            }
        };
        Flux.range(0, 100)// 2
                .map(String::valueOf)// 3
                .filter(s -> s.length() > 1)// 4
                .publishOn(scheduler)// 5
                .map(this::calculateHash)// 6
                .map(this::doBusinessLogic)// 7
                .subscribe();// 8
    }
```

위 코드에서 publishOn 메서드가 실행되고 나서 2 ~ 4단계의 작업은 메인 스레드에서 실행됩니다. 즉 해시 계산은 스레드 A에서만 실행되므로 caculateHash 및 doBusinessLogic은 메인 스레드 워커가 아닌 다른 워커에서 실행됩니다.  publishOn 연산자는런타임 실행에 초점을 맞춥니다. publishOn 연산자는 내부적으로 전용 워커가 메시지를 하나씩 처리할 수 있도록 새로운 원소를 제공하는 큐를 가지고 있습니다. 여기서 플로의 두 부분을 독립적으로 처리했습니다. 하나 중요한 점은 리액티브 스트림의 모든 원소는 하나씩 처리되므로 모든 이벤트에 순서를 엄격하게 정의할 수 있다는 것입니다. 이 속성을 직렬성이라고도 합니다. 즉 원소가 publishOn에 오면 큐에 추가되고 차례가되면 큐에서 꺼내서 처리합니다하나의 작업자가 큐를 처리하므로 원소의 순서는 예측 가능합니다.

### publishOn 연산자

멀티쓰레딩을 위한 리액터의 또 다른 중요한 요소는 subscribeOn 연산자 입니다. publishOn과 달리 subscribeOn을 사용하면 구독 체인에서 워커의 작업 위치를 변경할 수 있습니다. 이 연산자는 함수를 실행해 스트림 소스를 만들 때 유용하게 사용할 수 있습니다. 일반적으로 이러한 실행은 구독 시간에 수행되므로 .subscribe 메서드를 실행하기 위한 데이터 원천 소스를 제공하는 함수가 호출됩니다. 예를 들어 Mono. fromCallable을 사용해 정보를 제공하는 방법을 보여주는 예제를 살펴 보겠습니다.

```java
ObjectMapper objectMapper = ...
String json = "{\"color" : \"Black\",\"type\" : \"BMW\"}";
Mono.fromCallable(() -> objectMapper.readValue(json, Car.class)
```

여기서 Mono.fromCallable은 Callable<T>에서 Mono를 생성하고 실행 결과를 각 구독자에게 전달합니다. Callable 인스턴스는 .subscribe 메서드를 호출할 때 실행되므로 내부적으로 Mono.fromCallable은 다음과 같은 작업을 수행합니다.

```java
public void subscribe(Subscriber actual){
	Subscription subscription =...
	try{
		T t = cacllable.call();
		if (t == null){
			subscription.onComplete();
		}else{
			subscription.onNext(t);
			subscription.onComplete();
		}
	}catch (Throwable e){
		actual.onError(
			Operators.onOperatorError(e, actual.currentContext()));
	}
}
```

이 코드에서 알 수 있듯이 subscribe 메서드에서 calllable이 실행됩니다.
이것은 publishOn을 사용해 Callable이 실행될 워커를 변경할 수 있음을 의미합니다. 다행히도 subscribeOn을 사용하면 구독을 수행할 워커를 지정할 수 있습니다. 다음 코드는 어떻게 워커를 지정하는 지 보여 줍니다.
내부적으로 subscribeOn은 부모 Publisher에 대한 구독을 Runnable 안에서 실행합니다. 예제에서 이 Runnable은 Scheduler 타입의 scheduler 인스턴스 입니다.

### parallel 연산자

실행 플로 일부를 직접 처리하기 위해 스레드를 관리하는 몇 가지 중요한 연산자와 함께 리액터는 병렬 처리를 위해 parrallel 연산자를 제공하며 이 연산자는 하위 스트림에 대한 플로 분할과 분할된 프롤 간 균현 조정 역할을 합니다.

```java
        Flux.range(0,10000)
                .parallel()
                .runOn(Schedulers.parallel())
                .map()
                .filter()
                .subscribe();
```

parrellel 연산자를 사용함으로 ParallelFlux라는  다른 유형의 Flux를 동작시킨다는 것입니다. ParallelFlux는 다수의 Flux를 추상화한 것으로 Flux 간에 데이터 크기가 균형을 이룹니다.
### Scheduller

스케줄러는 Scheduler.schedule과 Scheduler.createWorker라는 두 가지 핵심 메서드를 가진 인터페이스 입니다. schedule 메서드를 사용하면 Runnable 작업을 예약하는 것이 가능합니다. 반면에 createWorker 메서드는 동일한 방법으로 Runnable 작업을 예약하는 것이 가능합니다. 반면에 createWorker 메서드는 동일한 방법으로 Runnable 작업을 예약할 수 있는 Worker 인터페이스를 제공합니다. Scheduler 인터페이스와 Worker 인터페이스의 주요 차이점은 Scheduler 인터페이스가 워커 풀을 나타내는 반면 Worker는 Thread 또는 리소스를 추상화한 것 이라는 점입니다. 기본적으로 리액터는 스케줄 인터페이스에 대해 주요한 3가지 구현체를 제공합니다.

- SingleScheduler를 사용하면 모든 작업을 한 개의 전용 워커에 예약할 수 있습니다. 이 스케줄러는 시간에 의존적인 방식이며, 주기적인 이벤트를 예약할 수 있습니다. 이 스케줄러는 Scheduler.single()을 호출해 생성할 수 있습니다.
- ParallelScheduler는 고정된 크기의 작업자 풀에서 작동합니다. (기본적으로 크기는 CPU 코어 수로 제한됨) 이 스케줄러는 CPU 제한적인 작업에 적합합니다. 또한 기본적으로Flux.interval(Duration.ofSecond(1))과 같은 시간 관련 예약 이벤트 처리를 합니다. 이 스케줄러는 Scheduler.parallel()을 호출해 생성할 수 있습니다.
- ElasticScheduler는 동적으로 작업자를 만들고 스레드 풀을 캐시합니다. 생성된 스레드 풀의 최대 개수는 제한되지 않으므로 IO 집약적인 작업에 적합합니다. 이 스케줄러는 Scheduler.elastic() 메서드를 호출해 생성할 수 있습니다.

### 리액터 컨텍스트

Context는 스트림을 따라 전달되는 인터페이스 입니다. Context는 런타임 단계에서 필요한 컨텍스트 정보에 액세스할 수 있도록 하는 것입니다. 이는 단일 쓰레드로 동작하는 애플리케이션이 아니기 때문에 필요합니다.
비동기 처리 방식을 사용하면 ThreadLocal을 사용할 수 있는 구간이 매우 짧아집니다. 예를 들어, 다음과 같이 실행하면 ThreadLocal을 사용하지 못하게 됩니다.

```java
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
```

- ThreadLocal 인스턴스를 생성합니다. 또한 1.1에서 ThreadLocal에 값을 추가했기 때문에 이후 코드에서 이 값을 사용할 수 있습니다.
- 0에서 9까지의 범위를 생성하는 FLux 스트림을 선언합니다. (2.1)

```java
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
```

- 리액터의 Context에 어떻게접근하는지 대한 예가 나와 있습니다. 리액터가 제공하는 정적 연산자 subscriberContext를 사용하면 현재 스트림의 Context 인스턴스에 액세스할 수 있습니다. 예제와 같이 Context를 획득하면 (1.1) 생성된 값을 Map에 저장합니다.(1.2) 마지막으로 flatMap의 초기 매개변수를 반환합니다.
- 스레드가 변경된 후 리액터의 Context에 다시 액세스합니다.
  예제는 ThreadLocal을 사용한 이전 예제와 동일하지만, (2.1)에서 저장된 맵에 성공적으로 접근해 랜덤 가우스 double을 반환합니다.
- 마지막으로 “randoms”를 만들기 위해 새로운 Context 인스턴스를 반환합니다.

앞의 예제처럼 Context는 인수가 없는 Mono.subscriberContext 연산자를 통해 액세스할 수 있으며, subscriberContext(Context) 연산자를 사용해 스트림을 제공할 수 있습니다.

앞의 예제에서 Context 인터페이스가 이미 Map 인터페이스와 비슷한 메서드를 가지고 있는데도 데이터를 전송하기 위해 Map을 또 사용할 필요가 있는 지 궁금할 것입니다. Context는 본질적으로 Immutable 객체라서 새로운 요소를 추가하면 Context는 새로운 인스턴스로 변경됩니다. 이러한 설계는 멀티스레딩 엑세스 모델을 고려해 이루어져있습니다. 즉 스트림에 컨텍스트를 제공할 수 있는 유일한 방법일 뿐만 아니라 조립 단계나 구독 단계를 포함해 전체 런타임 동안 사용할 수 있는 데이터를 동적으로 제공하는 유일한 방법입니다. Context가 조립 단계에서 제공되면 모든 구독자는 동일한 정적으로 제공하는 유일한 일뿐만 아니라 조립 단계나 구독 단계를 포함해 전체 런타임 동안 사용할 수 있는 방법입니다. Context가 조립 단계에서 제공되면 모든 구독자는 동일한 정적 컨텍스트를 공유하게 되며 이는 각 Subscriber가 별도의 Context를 가져야 하는 경우에는 유용하지 않을 수 있습니다. 따라서 전체 생명 주기에서 각 Subscriber에게 별도의 컨테스트가 제공될 수 있는 유일한 단계는 구독 단계 입니다.

이전 절의 내용을 다시 떠올려보면 구독 단계 동안 Subscriber는 Publisher 체임을 따라 스트림 아래쪽에서부터 위쪽으로 이동하면서 추가 런타임 로직을 적용하는 로컬 Subscriber로  각 단계를 래핑합니다. 이 프로세스를 변경하지 않고 스트림을 통해 추가 Context 객체를 전달하기 위해 리액터는 CoreSubscriber라는 특정 Subscriber 인터페이스 구현체를 사용합니다. CoreSubscriber는 내부 필드로 Context를 전달할 수 있습니다. 다음은 CoreSubscriber 인터페이스의 내부입니다.

## 스프링 부트2에서 리액티브

이 장에서는 스프링 부트의 중요성과 그 특징에 관해 설명하고자 합니다. 또한 스프링 프레임워크 5와 스프링 부트2로 인한 변화를 알아보고 스프링 생태계가 리액티브 프로그래밍 접근 방식을 어떻게 받아들였는 지 살펴볼 예정입니다.

이 장에서는 다음 주제를 다룹니다.

- 스프링 부트가 해결한 문제와 해결방법
- 스프링 부트의 필수 요소
- 스프링 부트 2.0 및 스프링 프레임워크의 반응성

### 제어의 역전

5가지 방법으로 컨테이너를 설정할 수 있습니다.

```java
@Slf4j
public class SpringApp {
    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        new XmlBeanDefinitionReader(context).loadBeanDefinitions("services.xml");
        new GroovyBeanDefinitionReader(context).loadBeanDefinitions("services.groovy");
        new PropertiesBeanDefinitionReader(context).loadBeanDefinitions("services.properties");
        context.refresh();

        System.out.println(context.getBean("xmlBean"));
        System.out.println(context.getBean("groovyBean"));
        System.out.println(context.getBean("propsBean"));

    }
}
```

스프링 프레임워크는 빈을 유연하게 구성할 수 있게 해주지만 문제가 있습니다. 대표적인 문제 중 하나는 쉽게 디버깅할 수 없는 XML 설정입니다. 또 다른 문제는 Intellij 혹은 spring suite같은 도구 없는 XML 설정의 정확성을 검증할 수 없다는 것 입니다. 마지막으로 코딩 스타일 부재 및 개발 규칙 적용의 어려움으로 대규모 프로젝트의 복잡성이 크게 증가합니다. Bean 정의에 대한 접근 방식이 적절하지 않으면 팀의 개발자가 XML에서 Bean을 설정하고 다른 개발자가 properties 파일을 통해 설정을 변경할 수 있기 때문에 프로젝트 관리가 복잡해질 수 있습니다.

단순한 IOC와 함께 스프링 프레임워크는 스프링 데이터 모듈과 같은 훨씬 복잡한 기능을 제공합니다. 두 모듈 모두 응용 프로그램을 실행하기까지 많은 설정이 필요합니다. 문제는 응용 프로그램이 플랫폼 독립적이어야 할 때 발생합니다. 비즈니스 관련 코드에 비해 설정 및 상용구 코드가 많은 비중을 차지하기 때문입니다.

예를 들어 간단한 웹 애플리케이션을 구성하기 위해 최소 7행의 코드가 필요합니다.

```java
public class MyWebApplication implements WebApplicationInitializer {
    @Override
    public void onStartup(javax.servlet.ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext cxt = new AnnotationConfigWebApplicationContext();
        cxt.register();
        cxt.refresh();
        //DispatcherServlet ...
        //ServletRegistation registration...
        // registration.setLoadOnStartup(1);
        // regstration.addMapping("/app/*);
    }
```

이 코드에는 보안 설정이나 콘텐츠 렌더링과 가틍ㄴ 필수 기능이 포함돼 있지 않습니다. 언제부터인가 각각의 스프링 기반 애플리케이션이 최적화되지 않고 개발자들이 추가적인 작업을 해야 하는 비슷한 코드를 가지게 됐고 결과적으로 아무런 이유 없이 비용을 낭비합니다.

### Spring Roo를 사용해 애플리케이션 개발 속도 향상
2009년 초 Spring Roo라는 새로운 프로젝트가 발표됐습니다. 이 프로젝트는 신속한 응용 프로그램 개발을 목표로 했습니다. Spring Roo의 핵심 아이디어는 설정보다 관습(convention-over-configuration) 접근법을 사용하는 것입니다. 이를 위해 Spring Roo는 인프라 및 도메인 모델을 초기화하고, 몇 가지 명령으로 Rest API를 작성할 수 있는 커맨드라인 사용자 인터페이스를 제공합니다. 현장에서의 프로젝트의 구조가 복잡해지거나 사용된 기술이 스프링 프레임워크의 범위를 벗어나면서 문제가 발생했습니다. 결정적으로 Spring Roo는 일반적인 용도로 인가가 없었습니다. 결국 신속한 응용 프로그램 개발에 대한 고민은 해결이 되지 못했습니다.

### 빠르게 성장하는 애플리케이션에대한 핵심 요소로서의 스프링 부트

2012년 말 마이크 영스트롬은 스프링 아키텍처 전체를 변경하고, 스프링 프레임 워크의 사용을 단순화해 개발자가 비즈니스 로직을 보다 빨리 구축할 수도록 하는 의견을 제시 했습니다. 그 제안은 거부됐지만 스프링 팀이 스프링 프레임 워크 사용을 극적으로 단순화하는 새로운 프로젝트를 만들도록 동기 부여를 했습니다. 2013년 중반에 스프링 팀은 스프링 부트라는 프로젝트를 출시 했습니다. 스프링 부트의 핵심 아이디어는 애플리케이션 개발 프로세스를 단순화하고 사용자가 추가적인 인프라 설정 없이 새 프로젝트를 시작할 수 있도록 하는 것 입니다.

이와 함께 스프링 부트는 컨테이너가 없이 실행되는 웹 애플리케이션 아이디어와 실행 가능한 fat JAR 기술을 도입했습니다. 이 방법을 사용하면 한줄의 명령으로 앱을 실행할 수 있습니다. 다음의 예제는 스프링 부트 웹 애플리케이션을 보여줍니다.

```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

여기서 가장 중요한 부분은 IOC 컨테이너를 실행하는 데 필요한 `@SpringBootApplication` 이라는 어노테이션이 있다는 것입니다. MVC서버 뿐만 아니라다른 응용 프로그램 구성 요소도 있습니다. 우선 스프링 부트는 그래이들이나 메이븐과 같은 현대 빌드 툴과 모듈들의 묶음 입니다.

일반적으로 스프링 부트는 두 개의 핵심 모듈에 의존합니다.

첫 번째는 Spring-IOC 컨테이너와 관련된 모든 가능한 기본 구성과 함께 제공되는 spring-boot 모듈입니다.

두 번째는 spring-boot-autoconfigure인데, 이 모듈은 스프링 데이터, 스프링 MVC, 스프링 웹플럭스 등과 같은 기존 스프링 프로젝트에서 필요한 모든 설정을 제공합니다. 언뜻 보기에 모든 사전 정의된 설정이 그 필요와 상관없이 모두 활성화된 것처럼 보입니다. 그러나 실제로는 그렇지 않으며 특정 의존성이 추가될 때까지 모든 설정은 비활성화 되어 있습니다. 스프링 부트는 일반적으로 이름에 starter라는 단어가 들어 있는 모듈에 대한 새로운 개념을 정의합니다. 기본적으로 starter는 자바 코드에 포함하지 않지만, spring-boot-autoconfigure에 의해 모든 관련 의존성을 가져와 설정을 활성화 합니다. 스프링 부트를 사용하면 별도의 노력 없이도 필요한 모든 인프라 설정을 완료할 수 있는 -starter-web, starter-data-jpa 모듈을 사용할 수 있습니다. 쉽게 설정할 수 있는 기본 설정과 함꼐 스프링 부트는 사용자 정의 starter를 만들기 위한 연쇄형 API를 제공합니다.

### 스프링 부트 2.0에서 리액티브

방응성이 스프링 생태계에 어떻게 반영되는 지 알아보겠습니다. 스프링 MVC와 스프링 데이터 모듈의 블로킹 특성으로 인해 프로그래밍 패러다임을 리액티브로 전환하는 것만으로는 별다른 이점이 없었습니다. 그래서 스프링 팀은 이러한 모듈 내부의 전체 패러다임을 변경하기로 결정했습니다. 이를 위해 스프링 생태계는 다수의 리액티브 모듈을 제공합니다. 이 절에서는 이 모듈을 간단히 다루겠습니다.

### 스프링 코어 패키지에서의 리액티브

스프링 코어는 스프링 생태계의 핵심 모듈입니다. 스프링 프레임워크 5.x에서 소개된 눈에 띄는 개선 사항 중 하나는  RxJava 1/2 및 리액터 프로젝트 3과 같은 리액티브 스트림 및 리액티브 라이브러리에 대한 기본 지원이었습니다.

### 리액티브 타입으로 현 변환 지원

리액티브 스트림 스펙을 지원하기 위한 가장 대표적인 개선 사항 중 하난느 ReactiveAdapter 및 ReactiveAdapterRegistry의 도입입니다. ReactorAdapter 클래스는 다음 코드와 같이 리액티브 타입 변환을 위한 두가지 메서드를 제공합니다.

```java
public class ReactiveAdapter {
...
	   public <T> Publisher<T> toPublisher(@Nullable Object source) {
	        if (source == null) {
	            source = this.getDescriptor().getEmptyValue();
	        }
	
	        return (Publisher)this.toPublisherFunction.apply(source);
	    }
	
	    public Object fromPublisher(Publisher<?> publisher) {
	        return this.fromPublisherFunction.apply(publisher);
	    }
	}
}
```

이 예제에서는 ReactiveAdapter는 임의의 타입을 Publisher<T>로 변환하거나 임의의 Publisher<T>를 Object로 변환하는 두 가지 메서드를 보여줍니다. 예를 들어, RxJava 2의 Maybe 타입에 대한 변환을 제공하기 위해 다음과 같은 방식으로 자체 ReactiveAdapter를 만들 수 있습니다.

```java
public MaybeReactiveAdapter(){
        super(
                ReactiveTypeDescriptor.singleOptionalValue(Maybe.class,Maybe::empty),
                rawMaybe -> ((Maybe<?>) rawMaybe).toFlowable(),
                publisher -> Flowable.fromPublisher(publisher)
                        .singleElement()
        );
    }
```

## 6 리액티브 웹의 핵심
리액티브 API 구축하기

```java
interface ServerHttpRequest {// 1
	...
	Flux<Databuffer> getBody(); // 1.1
	...
}

interface ServerHttpResponse {// 2
	...
	Mono<void> writeWith(Publisher<? extends DataBuffer> body);// 2.1
	...
}
 interface ServerWebExchange {// 3

    ServerHttpRequest getRequest();// 3.1

    ServerHttpResponse getResponse();// 3.2

    ...

    Mono<WebSession> getSession();// 3.3
    }
```

코드에 대한 설명

1. 수신 메시지를 나타내는 인터페이스 초안입니다. 보는 바와 같이, (1.1)에서 수신되는 바이트에 대한 엑세스를 제공하는 핵심 추상화는 Flux입니다. 정의에 의하면 이것은 리액티브 엑세스가 있음을 의미합니다. 요청 본문과 함께 모든 HTTP 요청에는 일반적으로 요청 헤더, 패스, 쿠키 및 쿼리 매개변수에 대한 정보가 포함돼 있으므로 각 정보를 해당 인터페이스 또는 하위 인터페이스에서 별도의 메서드로 표시할 수 있습니다.
2. 응답 인터페이스의 초안입니다. (2.1)을 보면 ServerHttpRequest#getBody 메서드와 달리 Publisher<? extends> 데이터 타입을 매개 변수로 받을 수 있습니다. 이 경우 Publisher 타입은 특정 리액티브 타입과의 결합도를 낮춰서 코드의 유연성을 확보할 수 있습니다. 그에 따라 적합한 인터페이스 구현체를 골라서 사용할 수 있고 비즈니스 로직을 프레임워크에서 분리할 수 있습니다. 이 메서드는 네트워크에 데이터를 보내는 비동기 프로세스인 Mono<Void>를 반환합니다. 여기서 중요한 점은 주어진 Mono를 구독하는 경우에만 데이터를 보내는 프로세스가 실행된다는 것입니다. 또한 수신 서버는 전송 프로토콜의 제어 흐름에 따라 배압을 제어할 수 있습니다.
3. ServerWebExchange 인터페이스 선언입니다. 여기서 인터페이스는 HTTP 요청 및응답 인스턴스의 컨테이너 역할을 합니다.인터페이스는 인프라 스트럭처 역할을 수행하고 HTTP 상호 작용을 처리할 뿐만 아니라 프레임워크와 관련된 정보를 저장할 수 있습니다. 예를 들어(3.3)과 같이 요청에서 추출한 WebSession에 대한 정보를 포함할 수 있습니다.

잠재적으로 이 인터페이스들은 서블릿에 있는 API에 있는 것과 유사합니다. 리액티브 스트림의 비동기 논블로킹 특성으로 인해 스트리밍 기반의 기능을 즉시 사용할 수 있고 얽히고선킨 콜백 기반 API와 콜백 지옥으로부터 보호해줍니다.

핵심적인 인터페이스와 별도로 전체 흐름을 수행하기 위해서는 다음과 같이 요청-응답 핸들러 및 필터 API를 정의해야 합니다.

```java
public interface WebHandler {//1
    Mono<Void> handle(ServerWebExchange var1);
}
public interface WebFilter {//2
    Mono<Void> filter(ServerWebExchange var1, WebFilterChain var2);
}
public interface WebFilterChain {//3
    Mono<Void> filter(ServerWebExchange var1);
}
```

1. 이것은 WebHandler라고 하는 HTTP 상호 작용의 핵심 진입점입니다. 이 인터페이스가 DispatcherServlet 역할을 하므로 그 위에 모든 구현을 할 수 있습니다. 인터페이스의 책임은 요청 핸들러를 찾아서 ServerHttpResponse에 실행하므로 그 위에 모든 구현을 할 수 있습니다. 인터페이스의 책임은 요청 핸들러를 찾아서 ServerHttpResponse에 실행 결과를 기록하는 뷰의 렌더러를 구선하는 것이므로 DispatchServlet#handle 메서드는 결과를 반환하지 않아도 됩니다. 그러나 처리가 완료되면 알림을 받는 것이 유용할 수 있습니다. 이와 같은 알림에 의존함으로써 지정한 시간 내 알림을 받지 못하면 실행을 취소할 수 있습니다. 이러한 이유로 메서드는 반드시 Void 타입 Mono를 반환하게 되어 있어 결과를 반드시 처리하지 않고도 비동기 처리가 완료될 때까지 기다릴 수 있습니다.
2. Servelr API와 유사하게 몇 개의 WebFilter 인스턴스 체인에 연결할 수 있는 인터페이스 입니다.
3. 리액티브 Filter에 대한 표현 입니다.

또한 이 계층에서는 ServerWebExchange를 만들어야 합니다. 특히 세션 저장소 Locale 확인 및 이와 유사한 인프라들이 이 계층에 존재합니다.


### 리액티브 웹 MVC 프레임워크
스프링 웹 MVC 모듈의 핵심은 애노테이션 기반 프로그래밍 모델입니다. 따라서 리액티브 방식으로 웹을 대체하더라도 애노테이션 기반 모델은 지원해야합니다. 새로운 리액티브 MVC 인프라를 구축하는 대신 기존 MVC 인프라를 재사용하고 동기 통신을 Flux, Mono 및 Publisher와 같은 리액티브 타입으로 교체 할 수도 있습니다.
