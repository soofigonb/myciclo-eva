package cl.myciclo.eva.seguimiento_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "SEGUIMIENTO_DB_URL=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "SEGUIMIENTO_DB_USER=sa",
        "SEGUIMIENTO_DB_PASSWORD=",
        "SEGUIMIENTO_DB_DRIVER=org.h2.Driver",
        "JWT_SECRET=MiClaveSuperSecretaDeAlMenos32CaracteresMyCiclo2026!",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class SeguimientoServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
