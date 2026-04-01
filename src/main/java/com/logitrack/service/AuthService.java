package com.logitrack.service;

import com.logitrack.dto.auth.LoginRequestDTO;
import com.logitrack.dto.auth.LoginResult;
import com.logitrack.exception.BusinessException;
import com.logitrack.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public LoginResult login(LoginRequestDTO request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            UserDetails details = userDetailsService.loadUserByUsername(auth.getName());
            String token = jwtService.generateToken(details);
            return new LoginResult(details.getUsername(), token);
        } catch (Exception e) {
            throw new BusinessException("Credenciais inválidas");
        }
    }
}
