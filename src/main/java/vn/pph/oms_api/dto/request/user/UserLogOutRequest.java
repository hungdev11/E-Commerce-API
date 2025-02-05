package vn.pph.oms_api.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLogOutRequest {
    Long userId;
    String accessToken;
}
