package vn.pph.oms_api.dto.request.token;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
    @NotNull(message = "User Id can not be null")
    @Min(value = 0, message = "User Id can not be less than 0")
    private Long userId;

    @NotNull(message = "Refresh token can not be null")
    @NotBlank(message = "Refresh token can not be blank")
    private String refreshToken;

    @NotNull(message = "Private key can not be null")
    @NotBlank(message = "Private key can not be blank")
    private String privateKey;
}
