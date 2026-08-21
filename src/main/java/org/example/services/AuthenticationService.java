package org.example.services;

import lombok.AllArgsConstructor;
import org.example.client.AppUser;
import org.example.dao.AppUserDAO;
import org.example.enums.AccountType;
import org.example.enums.Role;
import org.example.models.AuthenticationRequest;
import org.example.models.AuthenticationResponse;
import org.example.models.RefreshRequest;
import org.example.models.RegisterRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import javax.security.auth.login.AccountLockedException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private PasswordEncoder passwordEncoder;
    private AppUserDAO appUserDAO;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        AppUser appUser = AppUser
                .builder()
                .firstname(registerRequest.getFirstname())
                .lastname(registerRequest.getLastname())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .accountType(AccountType.BASIC)
                .accountActive(true)
                .build();

        String jwtToken = jwtService.generateToken(appUser);
        String refreshToken = jwtService.generateRefreshToken(appUser);
        appUser.setRefreshToken(refreshToken);
        appUserDAO.save(appUser);

        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws AccountLockedException {
        AppUser user = appUserDAO.findAppUserByEmail(authenticationRequest.getEmail()).orElseThrow(() -> new RuntimeException("user not found"));
        if (!user.isAccountNonLocked()) {
            throw new AccountLockedException("account locked");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );
        AppUser appUser = appUserDAO
                .findAppUserByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("user not found"));

        String token = jwtService.generateToken(appUser);
        String refreshToken = jwtService.generateRefreshToken(appUser);
        appUser.setRefreshToken(refreshToken);
        appUserDAO.save(appUser);

        return AuthenticationResponse
                .builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refresh(RefreshRequest refreshRequest) {
        String token = refreshRequest.getRefreshToken();
        String username = jwtService.extractUsername(token);
        AppUser appUser = appUserDAO
                .findAppUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("user not found"));

        String newAccessToken = null;
        String newRefreshToken = null;

        if (appUser.getRefreshToken().equals(token)) {
            newAccessToken = jwtService.generateToken(appUser);
            newRefreshToken = jwtService.generateRefreshToken(appUser);
            appUser.setRefreshToken(newRefreshToken);
            appUserDAO.save(appUser);
        }
        return AuthenticationResponse
                .builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
