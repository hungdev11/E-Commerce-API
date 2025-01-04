package vn.pph.oms_api.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSignUpRequest {
    String name;
    String email;
    String password;
}
