package com.mmatejovic.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}


	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p
						.path("/mmario/accounts/**")
						.filters(f -> f.rewritePath("/mmario/accounts/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Response-Time",new Date().toString()))
						.uri("lb://ACCOUNTS")).
				route(p -> p
						.path("/mmario/loans/**")
						.filters(f -> f.rewritePath("/mmario/loans/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Response-Time",new Date().toString()))
						.uri("lb://LOANS")).
				route(p -> p
						.path("/mmario/cards/**")
						.filters(f -> f.rewritePath("/mmario/cards/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Response-Time",new Date().toString()))
						.uri("lb://CARDS")).build();
	}
}
