package com.webflux.gateway.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.client.loadbalancer.ResponseData;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import lombok.Data;
import reactor.core.publisher.Mono;

@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> implements WebFilter{
	
	private static final Logger logger = LogManager.getLogger(GlobalFilter.class);
	
    private PathPattern basePattern;
    private List<PathPattern> excludePatterns;

    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
    	// session체크 필터
    	// 현재 로그인페이지와 회원가입만 필터 제외
    	basePattern = new PathPatternParser()
                 .parse("/**");
	    excludePatterns = new ArrayList<>();
	    excludePatterns.add(new PathPatternParser().parse("/user/checkSession**"));
	    excludePatterns.add(new PathPatternParser().parse("/user/login**"));
	    excludePatterns.add(new PathPatternParser().parse("/user/join**"));
	    excludePatterns.add(new PathPatternParser().parse("/user/checkId**"));
	    excludePatterns.add(new PathPatternParser().parse("/user/logout**"));
	    
        return ((exchange, chain) -> {
            logger.info("===========================================================");
            if (config.isPreLogger()) {
                logger.info("GlobalFilter Start>>>>>>" + exchange.getRequest());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                if (config.isPostLogger()) {
                	logger.info("GlobalFilter End>>>>>>" + exchange.getRequest());
                }
                logger.info("===========================================================");
            }));
        });
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
    
    // session check
    @Override
	public Mono<Void> filter(final ServerWebExchange serverWebExchange, final WebFilterChain webFilterChain) {
		ServerHttpRequest request = serverWebExchange.getRequest();
		logger.info("##{} : {} {}", request.getHeaders().getFirst("X-Forwarded-For") == null ? request.getRemoteAddress() : request.getHeaders().getFirst("X-Forwarded-For"), request.getMethodValue(), request.getURI().toString());
        // header에서 세션체크 (존재시 key값으로 redis에서 조회, 미존재시 로그인필요)
        if (basePattern.matches(request.getPath().pathWithinApplication())
            && !excludePatterns.stream()
                               .anyMatch(pathPattern -> pathPattern.matches(request.getPath().pathWithinApplication()))
           ) {
            return serverWebExchange.getSession()
            .doOnNext(session -> Optional.ofNullable(session.getAttribute("userId"))
                                         .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "logout"))
            )
            .then(webFilterChain.filter(serverWebExchange));
        } else {
            return webFilterChain.filter(serverWebExchange);
        }
	}
}
