package security;

import reactor.core.publisher.Mono;

public interface ProfileService {

    Mono<Profile> getByUser(String name);
}