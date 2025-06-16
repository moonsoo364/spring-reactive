package security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.regex.Pattern;

@Configuration
public class SecurityConfiguration {

    private static final Pattern PASSWORD = Pattern.compile("^\\{.+}.*$");
    private static final String NOOP_PASSWORD = "{noop}";

    @Bean
    public MapReactiveUserDetailsService reactiveUserDetailsService(
            ObjectProvider<PasswordEncoder> passwordEncoder
    ){
        //임의로 유저를 생성
        return new MapReactiveUserDetailsService(
                User.withUsername("user")
                        .password("user")
                        .passwordEncoder(p -> getOrDeducePassword(p, passwordEncoder.getIfAvailable()))
                        .roles("USER")
                        .build(),
                User.withUsername("admin")
                        .password("admin")
                        .passwordEncoder(p -> getOrDeducePassword(p, passwordEncoder.getIfAvailable()))
                        .roles("USER","ADMIN")
                        .build()
        );
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity){
        return httpSecurity
                .authorizeExchange()
                .anyExchange().permitAll()
                .and()
                .httpBasic()
                .and()
                .formLogin()
                .and()
                .build();
    }


    private String getOrDeducePassword(
            String password,
            PasswordEncoder encoder
    ){
        if(encoder != null || PASSWORD.matcher(password).matches()){
            return password;
        }
        return NOOP_PASSWORD + password;
    }
}
