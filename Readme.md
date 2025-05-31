# 도서 감상문

## Spring MVC 과 리액티브 (WebFlux) 비교
스프링 MVC 아키텍처를 다시 돌아봤고 디스패처 서블릿부터 컨트롤러까지 요청 당 쓰레드 할당되는 
프로세스에 대해서도 다시 보게 되었다. SecurityContext의 기능으로 요청한 유저에 정보를 컨트롤러에서 받아오는 기능도 요청 당 쓰레드(Threadlocal)이기 때문에 사용가능한 것임을 이해했다.
결국 스프링으로 리액티브 앱을 사용하는 것은 기존 MVC 패턴에서 어떤 것이 문제고 개선을 했는 가를 살펴보는 것이 중요하다.

아래는 두 모델을 비교한 표 이다. 많은 요청이 있을 수록 WebFlux를 사용하는 것이 유리하고 
요청이 많지 않은 애플리케이션을 개발한다고 하면 개발 유지 관점에서는 MVC가 유리하다고 할 수 있다. 
| 항목           | Spring MVC                    | Spring WebFlux                        |
|----------------|-------------------------------|----------------------------------------|
| 처리 방식      | 동기(블로킹)                   | 비동기(논블로킹)                       |
| 스레드 모델    | 요청당 스레드 할당             | 이벤트 루프 기반                      |
| 성능           | 적은 트래픽에는 유리          | 대량의 트래픽에 유리                  |
| 응답 처리      | 서블릿 API (HttpServlet)       | Reactive Streams (Mono, Flux)         |
| 주요 API       | @Controller, @RestController  | @RestController, RouterFunction       |
| 백프레셔       | 지원 안함                      | Reactive Streams 스펙으로 지원        |

## DispacherServlet 과 대응대는 인터페이스

MVC 에서는 클라이언트의 요청을 DispatcherServlet이 수신하고 HandlerMapping과 같은 컨테이너가 컨트롤러로 요청을 전달한다.
이에 대응해서 WebFlux에서도 관련 인터페이스를 제공한다.


아래는 서블릿의 요청, 응답 객체에 대응되는 인터페이스 들이다.
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

아래에서 WebHandler는 디스패처서블릿과 대응되는 인터페이스이다.
WebFilter, WebFilterChain는 HTTP 요청을 받기 전 처리할 로직에 사용되는 필터 인터페이스이다.
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