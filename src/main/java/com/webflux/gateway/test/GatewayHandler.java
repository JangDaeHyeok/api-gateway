package com.webflux.gateway.test;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class GatewayHandler {

	public Mono<ServerResponse> gatewayReadiness(ServerRequest request) {
		return ServerResponse.ok().body(Mono.just("gateway OK"), String.class);
	}
}
