package vn.pph.oms_api.dto.request.token;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
    private Long userId;
    private String refreshToken;
    private String privateKey;
}
