package com.beauty.gateway.config;

import com.beauty.gateway.filters.AuthenticationPrefilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "services.auth-service.url=http://localhost:8081",
        "services.product-service.url=http://localhost:8082",
        "services.cart-service.url=http://localhost:8083"
})
class GatewayConfigTest {
    @MockBean
    private AuthenticationPrefilter authenticationPrefilter;

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void routeLocator_shouldContainAllRoutes() {
        var routes = routeLocator.getRoutes().collectList().block();

        assertThat(routes).isNotNull();
        assertThat(routes).anyMatch(route -> route.getId().equals("auth-service"));
        assertThat(routes).anyMatch(route -> route.getId().equals("product-service-public"));
        assertThat(routes).anyMatch(route -> route.getId().equals("product-service-secured"));
        assertThat(routes).anyMatch(route -> route.getId().equals("cart-service"));
    }
} 