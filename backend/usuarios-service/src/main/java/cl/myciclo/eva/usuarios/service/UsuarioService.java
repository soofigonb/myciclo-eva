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
    public PerfilResponse registrar(RegistroRequest request) {
        String correoNormalizado = normalizarCorreo(request.correo());
        if (usuarioRepository.existsByCorreo(correoNormalizado)) {
            throw new CorreoYaRegistradoException("Ya existe una cuenta registrada con ese correo.");
        }

        String hash = passwordEncoder.encode(request.contrasena());
        Usuario nuevo = new Usuario(
                request.nombre().trim(),
                correoNormalizado,
                hash,
                request.fechaNacimiento()
        );

        Usuario guardado = usuarioRepository.save(nuevo);
        return mapearPerfil(guardado);
    }

    @Transactional(readOnly = true)
    public LoginResponse iniciarSesion(LoginRequest request) {
        String correoNormalizado = normalizarCorreo(request.correo());
        Usuario usuario = usuarioRepository.findByCorreo(correoNormalizado)
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos."));

        if (!passwordEncoder.matches(request.contrasena(), usuario.getContrasenaHash())) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos.");
        }

        String token = jwtTokenService.generarToken(usuario.getId(), usuario.getCorreo());
        return new LoginResponse(
                token,
                "Bearer",
                jwtTokenService.getExpirationSeconds(),
                mapearPerfil(usuario)
        );
    }

    @Transactional(readOnly = true)
    public PerfilResponse obtenerPerfil(Long usuarioId) {
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        return mapearPerfil(usuario);
    }

    @Transactional
    public PerfilResponse actualizarPerfil(Long usuarioId, ActualizarPerfilRequest request) {
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        usuario.setNombre(request.nombre().trim());
        usuario.setFechaNacimiento(request.fechaNacimiento());
        Usuario actualizado = usuarioRepository.save(usuario);
        return mapearPerfil(actualizado);
    }

    private Usuario buscarUsuarioPorId(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado."));
    }

    private String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
    }

    private PerfilResponse mapearPerfil(Usuario usuario) {
        return new PerfilResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getFechaNacimiento()
        );
    }
}
