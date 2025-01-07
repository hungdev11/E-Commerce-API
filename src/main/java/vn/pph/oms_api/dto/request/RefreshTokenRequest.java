package vn.pph.oms_api.dto.request;

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
