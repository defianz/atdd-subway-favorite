package nextstep.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.auth.model.authentication.interceptor.SessionAuthenticationInterceptor;
import nextstep.auth.model.authentication.interceptor.TokenAuthenticationInterceptor;
import nextstep.auth.model.authentication.service.UserDetailsService;
import nextstep.auth.model.authorization.interceptor.SessionSecurityContextPersistenceInterceptor;
import nextstep.auth.model.authorization.interceptor.TokenSecurityContextPersistenceInterceptor;
import nextstep.auth.model.authorization.resolver.AuthenticationPrincipalArgumentResolver;
import nextstep.auth.model.token.JwtTokenProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AuthConfig implements WebMvcConfigurer {
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    public AuthConfig(UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionAuthenticationInterceptor(userDetailsService)).addPathPatterns("/login/session");
        registry.addInterceptor(new TokenAuthenticationInterceptor(userDetailsService, jwtTokenProvider, objectMapper)).addPathPatterns("/login/token");
        registry.addInterceptor(new SessionSecurityContextPersistenceInterceptor());
        registry.addInterceptor(new TokenSecurityContextPersistenceInterceptor(jwtTokenProvider, userDetailsService));
    }

    @Override
    public void addArgumentResolvers(List argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }
}