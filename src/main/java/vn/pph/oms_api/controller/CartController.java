package vn.pph.oms_api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.pph.oms_api.dto.request.CartUpdateRequest;
import vn.pph.oms_api.dto.request.ProductAddToCartRequest;
import vn.pph.oms_api.dto.response.ApiResponse;
import vn.pph.oms_api.dto.response.CartResponse;
import vn.pph.oms_api.dto.response.CartUpdateResponse;
import vn.pph.oms_api.service.CartService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CartController {
    CartService cartService;

    @PostMapping("/add-product")
    public ApiResponse<?> addProductToCart(@RequestBody ProductAddToCartRequest request) {
        log.info("Controller: add new product to cart of user {}", request.getUserId());
        cartService.addProductToCart(request);
        return ApiResponse.builder()
                .code(200)
                .message("Add product to cart successfully")
                .build();
    }
    @PostMapping("/update")
    public ApiResponse<CartUpdateResponse> updateCart(@RequestBody CartUpdateRequest request) {
        log.info("Controller: update cart {}", request.getCartId());
        return ApiResponse.<CartUpdateResponse>builder()
                .code(200)
                .data(cartService.updateCart(request))
                .message("Update successfully")
                .build();
    }
    @GetMapping("/products")
    public ApiResponse<CartResponse> products(@RequestParam Long userId) {
        log.info("Controller: get products in cart of user {}", userId);
        return ApiResponse.<CartResponse>builder()
                .code(200)
                .data(cartService.products(userId))
                .message("Get products' cart successfully")
                .build();
    }
    @DeleteMapping("/delete")
    public ApiResponse<CartResponse> deleteCart(
            @RequestParam Long userId,
            @RequestParam Long cartId) {
        log.info("Controller: delete cart id {} of user {}", cartId , userId);
        return ApiResponse.<CartResponse>builder()
                .code(204)
                .data(cartService.deleteCart(userId, cartId))
                .message("Delete all cart items successfully")
                .build();
    }
    @DeleteMapping("/delete-item")
    public ApiResponse<CartResponse> deleteCartItem(
            @RequestParam Long userId,
            @RequestParam Long cartId,
            @RequestParam Long shopId,
            @RequestParam Long productId) {
        log.info("Controller: delete product {} of shop {} in cart {} of user {}", productId, shopId, cartId, userId);
        return ApiResponse.<CartResponse>builder()
                .code(204)
                .data(cartService.deleteCartItem(userId, cartId, shopId, productId))
                .message("Delete item successfully")
                .build();
    }

}
