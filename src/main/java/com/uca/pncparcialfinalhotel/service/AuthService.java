package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.LoginRequest;
import com.uca.pncparcialfinalhotel.dto.response.JwtAuthResponse;
import com.uca.pncparcialfinalhotel.entities.RefreshToken;
import com.uca.pncparcialfinalhotel.entities.Usuario;
import com.uca.pncparcialfinalhotel.exception.BusinessRuleException;
import com.uca.pncparcialfinalhotel.repository.RefreshTokenRepository;
import com.uca.pncparcialfinalhotel.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioService usuarioService;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMillis;

    public JwtAuthResponse login(LoginRequest request) {
        // Si el username/password no son válidos, esto lanza BadCredentialsException,
        // que ya está mapeada a 401 en el GlobalExceptionHandler.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = authentication.getName();
        String accessToken = jwtUtil.generateAccessToken(username);
        String refreshTokenValue = jwtUtil.generateRefreshToken(username);

        Usuario usuario = usuarioService.buscarPorUsernameOrThrow(username);
        persistirRefreshToken(usuario, refreshTokenValue);

        return JwtAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .build();
    }

    public JwtAuthResponse refrescar(String refreshTokenValue) {
        if (!jwtUtil.validateToken(refreshTokenValue) || !"refresh".equals(jwtUtil.getTokenType(refreshTokenValue))) {
            throw new BusinessRuleException("Refresh token inválido");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BusinessRuleException("Refresh token no reconocido"));

        if (refreshToken.isRevocado()) {
            throw new BusinessRuleException("Refresh token revocado, inicia sesión de nuevo");
        }
        if (refreshToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Refresh token expirado, inicia sesión de nuevo");
        }

        String username = jwtUtil.getUsernameFromToken(refreshTokenValue);
        String nuevoAccessToken = jwtUtil.generateAccessToken(username);

        // El refresh token se reutiliza tal cual hasta que expire o se revoque
        // (no se emite uno nuevo en cada refresh, solo el access token).
        return JwtAuthResponse.builder()
                .accessToken(nuevoAccessToken)
                .refreshToken(refreshTokenValue)
                .build();
    }

    private void persistirRefreshToken(Usuario usuario, String tokenValue) {
        RefreshToken refreshToken = RefreshToken.builder()
                .usuario(usuario)
                .token(tokenValue)
                .fechaExpiracion(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpirationMillis)))
                .revocado(false)
                .build();
        refreshTokenRepository.save(refreshToken);
    }
}
