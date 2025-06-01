package functional_spring_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Hooks;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class DemoApplication {
    final ServerRedirectHandler serverRedirectHandler = new ServerRedirectHandler();

    public static void main(String[] args) {
        Hooks.onOperatorDebug();
        SpringApplication.run(DemoApplication.class,args);
    }

    @Bean
    public RouterFunction<ServerResponse> routes(OrderHandler orderHandler) {
        return
                nest(path("/orders"),// 1
                        nest(accept(APPLICATION_JSON),
                                route(GET("/{id}"), orderHandler::get)// 2
                                        .andRoute(method(HttpMethod.GET), orderHandler::list)
                        )
                                .andNest(contentType(APPLICATION_JSON),
                                        route(POST("/"), orderHandler::create)// 3
                                )
                                .andNest((serverRequest) -> serverRequest.cookies()
                                                .containsKey("Redirect-Traffic"),// 4
                                        route(all(), serverRedirectHandler)
                                )
                );
    }
}
