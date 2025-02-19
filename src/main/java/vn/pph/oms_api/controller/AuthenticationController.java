package vn.pph.oms_api.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.pph.oms_api.dto.request.token.RefreshTokenRequest;
import vn.pph.oms_api.dto.request.user.UserLogOutRequest;
import vn.pph.oms_api.dto.request.user.UserSignInRequest;
import vn.pph.oms_api.dto.request.user.UserSignUpRequest;
import vn.pph.oms_api.dto.response.APIResponse;
import vn.pph.oms_api.service.AuthenticationService;

@RestController
@RequestMapping("/authentication")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/sign-up")
    public APIResponse<?> signUp(@RequestBody UserSignUpRequest request) {
        return APIResponse.builder()
                .code(200)
                .message("Registration successful")
                .data(authenticationService.signUp(request))
                .build();
    }
    @PostMapping("/sign-in")
    public APIResponse<?> signIn(@RequestBody UserSignInRequest request, @RequestParam(required = false) String privateKey) {
        return APIResponse.builder()
                .code(200)
                .message("Login successfully")
                .data(authenticationService.signIn(request, privateKey))
                .build();
    }
    @PostMapping("/log-out")
    public APIResponse<?> logOut(@RequestBody UserLogOutRequest request) {
        authenticationService.logOut(request);
        return APIResponse.builder()
                .code(200)
                .message("Log out successfully")
                .build();
    }
    @PostMapping("/refresh-token")
    public APIResponse<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return APIResponse.builder()
                .code(200)
                .message("Got new pair of tokens")
                .data(authenticationService.refreshToken(request))
                .build();
    }
}