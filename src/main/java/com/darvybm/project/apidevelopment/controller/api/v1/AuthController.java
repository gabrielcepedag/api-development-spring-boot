package com.darvybm.project.apidevelopment.controller.api.v1;

import com.darvybm.project.apidevelopment.dto.request.AuthRequest;
import com.darvybm.project.apidevelopment.dto.response.AuthResponse;
import com.darvybm.project.apidevelopment.exception.UnauthorizedException;
import com.darvybm.project.apidevelopment.model.User;
import com.darvybm.project.apidevelopment.service.JwtService;
import com.darvybm.project.apidevelopment.service.impl.UserServiceImpl;
import com.darvybm.project.apidevelopment.utils.response.CustResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustResponseBuilder custResponseBuilder;
    private final UserServiceImpl userService;

    @PostMapping("/api/v1/auth")
    public ResponseEntity<?> AuthenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            System.out.println("El usuario está autenticado");

            // Cargar el usuario autenticado
            User user = userService.loadUserByUsername(authRequest.getUsername());

            AuthResponse response = AuthResponse.builder()
                    .id(user.getId()) // Asume que el campo id está disponible en el usuario
                    .username(user.getUsername())
                    .accessToken(jwtService.generateToken(user))
                    .build();

            return custResponseBuilder.buildResponse(HttpStatus.OK.value(), "Usuario logueado correctamente!", response);
        } else {
            throw new UnauthorizedException("Username and Password incorrect");
        }
    }
}
