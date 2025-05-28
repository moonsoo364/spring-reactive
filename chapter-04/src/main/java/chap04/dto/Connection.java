package chap04.dto;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;

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
    public void close() {
        log.info("IO Connection closed");
    }

    public static Connection newConnection(){
        log.info("IO Connection created");
        return new Connection();
    }
}
