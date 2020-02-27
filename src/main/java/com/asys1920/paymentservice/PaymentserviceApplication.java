package com.asys1920.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;

import com.asys1920.util.GracefulShutdown;

@EntityScan("com.asys1920.*")
@SpringBootApplication
public class PaymentserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentserviceApplication.class, args);
	}


	@Bean
	public GracefulShutdown gracefulShutdown() {
		return new GracefulShutdown();
	}

	@Bean
	public ConfigurableServletWebServerFactory webServerFactory(final GracefulShutdown gracefulShutdown) {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		factory.addConnectorCustomizers(gracefulShutdown);
		return factory;
	}
}
