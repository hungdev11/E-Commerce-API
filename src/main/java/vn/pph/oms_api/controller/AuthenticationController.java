package vn.pph.oms_api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.pph.oms_api.dto.request.UserLogOutRequest;
import vn.pph.oms_api.dto.request.UserSignInRequest;
import vn.pph.oms_api.dto.request.UserSignUpRequest;
import vn.pph.oms_api.dto.response.ApiResponse;
import vn.pph.oms_api.service.AuthenticationService;

@RestController
@RequestMapping("/authentication")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/sign-up")
    public ApiResponse<?> signUp(@RequestBody UserSignUpRequest request) {
        return ApiResponse.builder()
                .code(200)
                .message("Registration successful")
                .data(authenticationService.signUp(request))
                .build();
    }
    @PostMapping("/sign-in")
    public ApiResponse<?> signIn(@RequestBody UserSignInRequest request, @RequestParam(required = false) String privateKey) {
        return ApiResponse.builder()
                .code(200)
                .message("Login successfully")
                .data(authenticationService.signIn(request, privateKey))
                .build();
    }
    @PostMapping("log-out")
    public ApiResponse<?> logOut(@RequestBody UserLogOutRequest request) throws Exception {
        authenticationService.logOut(request);
        return ApiResponse.builder()
                .code(200)
                .message("Log out successfully")
                .build();
    }
}