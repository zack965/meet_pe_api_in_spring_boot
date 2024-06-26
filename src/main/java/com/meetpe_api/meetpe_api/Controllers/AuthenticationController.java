package com.meetpe_api.meetpe_api.Controllers;

import com.meetpe_api.meetpe_api.DTO.Requests.LoginUserDto;
import com.meetpe_api.meetpe_api.DTO.Requests.RegisterUserDto;
import com.meetpe_api.meetpe_api.DTO.Responses.LoginResponse;
import com.meetpe_api.meetpe_api.DTO.Responses.RegisterResponse;
import com.meetpe_api.meetpe_api.Entities.User;
import com.meetpe_api.meetpe_api.Services.JwtService;
import com.meetpe_api.meetpe_api.Services.UserService;
import com.meetpe_api.meetpe_api.configs.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/app/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;
    private UserService userService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService,UserService userService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }
    @PostMapping("/signup")
    @Operation(security = {@SecurityRequirement(name = "API-KEY") })
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        RegisterResponse registerResponse = new RegisterResponse();

        User registeredUser = authenticationService.signup(registerUserDto);
        this.userService.InitAccount(registeredUser);
        String jwtToken = jwtService.generateToken(registeredUser);
        registerResponse.setUser(registeredUser);
        registerResponse.setToken(jwtToken);
        registerResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/login")
    @Operation(security = {@SecurityRequirement(name = "API-KEY") })
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setUser(authenticatedUser);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
