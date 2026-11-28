package cl.myciclo.eva.seguimiento_service;

import cl.myciclo.eva.seguimiento_service.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class SeguimientoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeguimientoServiceApplication.class, args);
	}

}
