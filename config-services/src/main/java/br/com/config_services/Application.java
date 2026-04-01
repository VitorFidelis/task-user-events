package br.com.config_services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Aplicativo de serviços de configuração
 *
 * Este aplicativo serve como um Spring Cloud Config Server, fornecendo gerenciamento de configuração centralizado
 * para sistemas distribuídos. Ele permite que os clientes busquem propriedades de configuração de um repositório central.
 */
@SpringBootApplication
@EnableConfigServer
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
