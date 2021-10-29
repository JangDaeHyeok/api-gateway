package com.webflux.gateway.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RoutsConfig {
	@Bean
    public RouterFunction<ServerResponse> boardRouter(GatewayHandler handler) {
        return RouterFunctions.route()
        		.GET("/readiness", handler::gatewayReadiness)
                .build(); 
    }
}
