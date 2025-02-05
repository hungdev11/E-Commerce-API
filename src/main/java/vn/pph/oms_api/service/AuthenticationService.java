package vn.pph.oms_api.service;

import vn.pph.oms_api.dto.request.token.RefreshTokenRequest;
import vn.pph.oms_api.dto.request.user.UserLogOutRequest;
import vn.pph.oms_api.dto.request.user.UserSignInRequest;
import vn.pph.oms_api.dto.request.user.UserSignUpRequest;
import vn.pph.oms_api.dto.response.token.RefreshTokenResponse;
import vn.pph.oms_api.dto.response.user.SignUpResponse;

public interface AuthenticationService {
    SignUpResponse signUp(UserSignUpRequest request);
    Object signIn(UserSignInRequest request, String privateKeyString);
    void logOut(UserLogOutRequest request);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
}
