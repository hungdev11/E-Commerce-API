package vn.pph.OrderManagementSysAPI.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    int page;
    int size;
    long total;
    T items;
}
