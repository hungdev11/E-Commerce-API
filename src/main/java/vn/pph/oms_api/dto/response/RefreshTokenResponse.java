package vn.pph.oms_api.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponse {
    private Long userId;
    private  String accessToken;
    private String refreshToken;
}
