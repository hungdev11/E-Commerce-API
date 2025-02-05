package vn.pph.oms_api.dto.response.user;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignInResponse {
    boolean authenticated; // ? have private key : vice versa
    Long userId;
    String accessToken;
    String refreshToken;
}
