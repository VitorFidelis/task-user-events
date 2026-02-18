package br.com.discovery_services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Aplicativo de serviços de descoberta
 *
 * Este aplicativo atua como um servidor Eureka, permitindo que os serviços registrem-se e descubram uns aos outros
 * em um ambiente de microsserviços. Ele facilita a comunicação entre serviços distribuídos.
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscoveryServicesApplication.class, args);
	}

}
