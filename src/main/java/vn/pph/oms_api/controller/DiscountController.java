package vn.pph.oms_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.pph.oms_api.dto.request.discount.DiscountCreationRequest;
import vn.pph.oms_api.dto.request.discount.RequestGetAmountDiscount;
import vn.pph.oms_api.dto.response.APIResponse;
import vn.pph.oms_api.dto.response.discount.DiscountCreationResponse;
import vn.pph.oms_api.dto.response.PageResponse;
import vn.pph.oms_api.service.DiscountService;
import vn.pph.oms_api.utils.DiscountStatus;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/discounts")
public class DiscountController {
    private final DiscountService discountService;

    @PostMapping("/")
    APIResponse<DiscountCreationResponse> createDiscount(@Valid @RequestBody DiscountCreationRequest request) {
        log.info("Controller: create new discount");
        return APIResponse.<DiscountCreationResponse>builder()
                .message("Create new discount")
                .code(201)
                .data(discountService.createNewDiscount(request))
                .build();
    }
    @GetMapping("/for-shop")
    public PageResponse<?> getAllDiscountsForShop(
            @RequestParam Long shopId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "ACTIVE") DiscountStatus discountStatus)
    {
        log.info("Controller: get all discount {} of shop id {}", discountStatus, shopId);
        return discountService.getAllDiscountsForShop(shopId, page, size, discountStatus);
    }

    @PostMapping("/get-discount-amount")
    public APIResponse<?> getDiscountAmount(@RequestBody RequestGetAmountDiscount requestGetAmountDiscount)
    {
        log.info("Controller: get amount value of user's order");
        return APIResponse.builder()
                .data(discountService.getDiscountAmount(requestGetAmountDiscount))
                .code(200)
                .message("OK")
                .build();
    }
    @DeleteMapping("/delete")
    public APIResponse<?> deleteDiscount (
            @RequestParam Long shopId,
            @RequestParam Long codeId) {
        log.info("Controller: delete discount {} of shop id {}", codeId, shopId);
        return APIResponse.builder()
                .message("Deleted successfully")
                .code(204)
                .data(discountService.deleteDiscount(shopId, codeId))
                .build();
    }

    @GetMapping("/cancel")
    public APIResponse<?> cancelDiscount (
            @RequestParam Long shopId,
            @RequestParam Long codeId,
            @RequestParam Long userId) {
        log.info("Controller: cancel discount {} of shop id {}, user {}", codeId, shopId, userId);
        return APIResponse.builder()
                .message("Cancel successfully")
                .code(200)
                .data(discountService.cancelDiscount(shopId, codeId, userId))
                .build();
    }

}
