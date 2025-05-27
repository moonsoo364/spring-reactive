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

최종 결과는 이전과 동일하게 15입니다. 하지만 이번에는 중간과정도 모두 출력됐습니다. 즉, 진행 중인 이벤트에 대한 정보가 필요한 애플리케이션에서 scan 연산자를 유용하게 쓸 수 있습니다. 예를 들어 다음과 같이 스트림의 이동 평균을 계산할 수 있습니다.\