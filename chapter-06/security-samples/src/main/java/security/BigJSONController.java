package security;

import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/json")
public class BigJSONController {

    private final ProfileService profileService;
    public BigJSONController(ProfileService service) {
        profileService = service;
    }

    @GetMapping(
            value = "/process-json",
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE
    )
    public Flux<ProcessedItem> getProfile(Flux<Item> bodyFlux){
        return bodyFlux.map(ProcessedItem::new);
    }


}
