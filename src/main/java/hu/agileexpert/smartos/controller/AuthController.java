package hu.agileexpert.smartos.controller;

import hu.agileexpert.smartos.dto.account.AccountResponse;
import hu.agileexpert.smartos.dto.auth.LoginRequest;
import hu.agileexpert.smartos.dto.auth.RegisterRequest;
import hu.agileexpert.smartos.dto.auth.RegistrationInfo;
import hu.agileexpert.smartos.service.AccountService;
import hu.agileexpert.smartos.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static hu.agileexpert.smartos.controller.constants.Endpoints.AUTH_MAPPING;

@RestController
@RequestMapping(AUTH_MAPPING)
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final AccountService accountService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationInfo> registerAccount(@Valid @RequestBody RegisterRequest registrationReq) {
        RegistrationInfo registrationInfo = accountService.registerAccount(registrationReq);
        log.info("HTTP POST {} - Registration successful", AUTH_MAPPING);
        return new ResponseEntity<>(registrationInfo, HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<AccountResponse> login(@Valid @RequestBody LoginRequest request,
                                                 HttpServletRequest httpRequest) {
        AccountResponse response = authService.login(request, httpRequest);
        log.info("HTTP POST {}/login - Login successful", AUTH_MAPPING);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
