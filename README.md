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