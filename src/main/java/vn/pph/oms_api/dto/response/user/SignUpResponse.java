package vn.pph.oms_api.dto.response.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignUpResponse {
    String privateKey;
    Long userId;
    String accessToken;
    String refreshToken;
}
