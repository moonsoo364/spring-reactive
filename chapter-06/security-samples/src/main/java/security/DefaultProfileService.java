package security;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DefaultProfileService implements ProfileService {

    @Override
    public Mono<Profile> getByUser(String name) {
        return Mono.empty();
    }
}
