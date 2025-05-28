package com.beauty.gateway.config;

import com.beauty.gateway.filters.AuthenticationPrefilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final AuthenticationPrefilter authenticationPrefilter;
    @Value("${services.auth-service.url}")
    private String authServiceUrl;

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    @Value("${services.cart-service.url}")
    private String cartServiceUrl;

    public GatewayConfig(AuthenticationPrefilter authenticationPrefilter) {
        this.authenticationPrefilter = authenticationPrefilter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Service-Name", "auth-service"))
                        .uri(authServiceUrl))
                .route("product-service-public", r -> r
                        .path("/api/products/catalog")
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Service-Name", "product-service"))
                        .uri(productServiceUrl))
                .route("product-service-secured", r -> r
                        .path("/api/products/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Service-Name", "product-service")
                                .filter(authenticationPrefilter))
                        .uri(productServiceUrl))
                .route("cart-service", r -> r
                        .path("/api/cart/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Service-Name", "cart-service")
                                .filter(authenticationPrefilter))
                        .uri(cartServiceUrl))
                .build();
    }
}
