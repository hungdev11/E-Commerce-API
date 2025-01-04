package vn.pph.oms_api.service;

import vn.pph.oms_api.dto.request.UserSignUpRequest;
import vn.pph.oms_api.dto.response.ApiResponse;
import vn.pph.oms_api.dto.response.SignUpResponse;

public interface AuthenticationService {
    ApiResponse<SignUpResponse> signUp(UserSignUpRequest request);
}
