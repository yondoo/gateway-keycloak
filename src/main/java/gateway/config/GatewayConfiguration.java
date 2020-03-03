package gateway.config;

//import gateway.gateway.accesscontrol.KeycloakFilter;
import gateway.security.oauth2.AuthorizationHeaderUtil;
import io.github.jhipster.config.JHipsterProperties;

import gateway.gateway.accesscontrol.AccessControlFilter;
import gateway.gateway.responserewriting.SwaggerBasePathRewritingFilter;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
public class GatewayConfiguration {

    @Configuration
    public static class SwaggerBasePathRewritingConfiguration {

        @Bean
        public SwaggerBasePathRewritingFilter swaggerBasePathRewritingFilter(){
            return new SwaggerBasePathRewritingFilter();
        }
    }

    @Configuration
    public static class AccessControlFilterConfiguration {

        @Bean
        public AccessControlFilter accessControlFilter(RouteLocator routeLocator, JHipsterProperties jHipsterProperties, AuthorizationHeaderUtil headerUtil, JwtDecoder jwtDecoder){
            return new AccessControlFilter(routeLocator, jHipsterProperties, headerUtil, jwtDecoder);
        }
    }

//    @Configuration
//    public static class KeycloakFilterConfiguration {
//
//        @Bean
//        public KeycloakFilter accessControlFilter(RouteLocator routeLocator, JHipsterProperties jHipsterProperties, AuthorizationHeaderUtil headerUtil){
//            return new KeycloakFilter(routeLocator, jHipsterProperties, headerUtil);
//        }
//    }

}
