package vn.pph.oms_api.service;

import vn.pph.oms_api.dto.request.UserSignInRequest;
import vn.pph.oms_api.dto.request.UserSignUpRequest;
import vn.pph.oms_api.dto.response.SignUpResponse;

public interface AuthenticationService {
    SignUpResponse signUp(UserSignUpRequest request);
    Object signIn(UserSignInRequest request, String privateKeyString);
}
