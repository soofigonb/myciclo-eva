package cl.myciclo.eva.usuarios;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "USUARIOS_DB_URL=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "USUARIOS_DB_USER=sa",
        "USUARIOS_DB_PASSWORD=",
        "USUARIOS_DB_DRIVER=org.h2.Driver",
        "JWT_SECRET=MiClaveSuperSecretaDeAlMenos32CaracteresMyCiclo2026!",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class UsuariosServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
