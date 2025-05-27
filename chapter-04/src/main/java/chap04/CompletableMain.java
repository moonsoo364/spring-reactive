package chap04;
import java.util.concurrent.CompletableFuture;

public class CompletableMain {
    public static void main(String[] args) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            // 비동기 작업
            return "Hello";
        });

        future.thenApply(result -> result + " World")
                //.thenAccept(System.out::println); // 결과 출력
        .thenAccept(t -> System.out.println(t)); // 결과 출력

        System.out.println("비동기 로직 실행 중...");
    }
}
