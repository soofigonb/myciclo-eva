package cl.myciclo.eva.usuarios.service;

import cl.myciclo.eva.usuarios.config.JwtTokenService;
import cl.myciclo.eva.usuarios.dto.ActualizarPerfilRequest;
import cl.myciclo.eva.usuarios.dto.LoginRequest;
import cl.myciclo.eva.usuarios.dto.LoginResponse;
import cl.myciclo.eva.usuarios.dto.PerfilResponse;
import cl.myciclo.eva.usuarios.dto.RegistroRequest;
import cl.myciclo.eva.usuarios.exception.CorreoYaRegistradoException;
import cl.myciclo.eva.usuarios.exception.CredencialesInvalidasException;
import cl.myciclo.eva.usuarios.exception.UsuarioNoEncontradoException;
import cl.myciclo.eva.usuarios.model.Usuario;
import cl.myciclo.eva.usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public PerfilResponse registrar(RegistroRequest solicitud) {
        String correoNormalizado = normalizarCorreo(solicitud.correo());
        if (usuarioRepository.existsByCorreo(correoNormalizado)) {
            throw new CorreoYaRegistradoException("Ya existe una cuenta registrada con ese correo.");
        }

        Usuario nuevo = new Usuario(
                solicitud.nombre().trim(),
                correoNormalizado,
                passwordEncoder.encode(solicitud.contrasena()),
                solicitud.fechaNacimiento()
        );

        Usuario guardado = usuarioRepository.save(nuevo);
        return aPerfilResponse(guardado);
    }

    @Transactional(readOnly = true)
    public LoginResponse iniciarSesion(LoginRequest solicitud) {
        String correoNormalizado = normalizarCorreo(solicitud.correo());
        Usuario usuario = usuarioRepository.findByCorreo(correoNormalizado)
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos."));

        if (!passwordEncoder.matches(solicitud.contrasena(), usuario.getContrasenaHash())) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos.");
        }

        String token = jwtTokenService.generarToken(usuario.getId(), usuario.getCorreo());
        return new LoginResponse(
                token,
                "Bearer",
                jwtTokenService.getExpirationSeconds(),
                aPerfilResponse(usuario)
        );
    }

    @Transactional(readOnly = true)
    public PerfilResponse obtenerPerfil(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("No se encontró el usuario autenticado."));
        return aPerfilResponse(usuario);
    }

    @Transactional
    public PerfilResponse actualizarPerfil(Long usuarioId, ActualizarPerfilRequest solicitud) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("No se encontró el usuario autenticado."));

        usuario.setNombre(solicitud.nombre().trim());
        usuario.setFechaNacimiento(solicitud.fechaNacimiento());

        Usuario actualizado = usuarioRepository.save(usuario);
        return aPerfilResponse(actualizado);
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }

    private PerfilResponse aPerfilResponse(Usuario usuario) {
        return new PerfilResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getFechaNacimiento()
        );
    }
}
