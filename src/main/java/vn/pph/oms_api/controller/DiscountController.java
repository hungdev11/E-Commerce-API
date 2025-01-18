package vn.pph.oms_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.pph.oms_api.dto.request.DiscountCreationRequest;
import vn.pph.oms_api.dto.response.ApiResponse;
import vn.pph.oms_api.dto.response.DiscountCreationResponse;
import vn.pph.oms_api.service.DiscountService;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/discount")
public class DiscountController {
    private final DiscountService discountService;

    @PostMapping("/")
    ApiResponse<DiscountCreationResponse> createDiscount(@Valid @RequestBody DiscountCreationRequest request) {
        log.info("Controller: create new discount");
        return ApiResponse.<DiscountCreationResponse>builder()
                .message("Create new discount")
                .code(201)
                .data(discountService.createNewDiscount(request))
                .build();
    }
}
