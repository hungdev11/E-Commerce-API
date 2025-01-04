package vn.pph.oms_api.dto.response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignUpResponse {
    String privateKey;
    Long userId;
    String accessToken;
    String refreshToken;
}
