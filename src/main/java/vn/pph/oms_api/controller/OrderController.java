package vn.pph.oms_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.pph.oms_api.dto.request.order.ReviewOrderRequest;
import vn.pph.oms_api.dto.request.product.AttributeCreationRequest;
import vn.pph.oms_api.dto.response.ApiResponse;
import vn.pph.oms_api.service.AttributeService;
import vn.pph.oms_api.service.CheckoutService;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final CheckoutService checkoutService;

    @PostMapping("/checkout")
    public ApiResponse<?> create(@RequestBody ReviewOrderRequest request) {
        return ApiResponse.builder()
                .code(200)
                .message("Review OK")
                .data(checkoutService.review(request))
                .build();
    }
}
