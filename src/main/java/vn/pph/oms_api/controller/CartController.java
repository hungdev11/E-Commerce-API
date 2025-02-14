package vn.pph.oms_api.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.pph.oms_api.dto.request.cart.CartUpdateRequest;
import vn.pph.oms_api.dto.request.product.ProductAddToCartRequest;
import vn.pph.oms_api.dto.response.APIResponse;
import vn.pph.oms_api.dto.response.cart.CartResponse;
import vn.pph.oms_api.dto.response.cart.CartUpdateResponse;
import vn.pph.oms_api.service.CartService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CartController {
    CartService cartService;

    @PostMapping("/add-product")
    public APIResponse<?> addProductToCart(@Valid @RequestBody ProductAddToCartRequest request) {
        log.info("Controller: add new product to cart of user {}", request.getUserId());
        cartService.addProductToCart(request);
        return APIResponse.builder()
                .code(200)
                .message("Add product to cart successfully")
                .build();
    }
    @PostMapping("/update")
    public APIResponse<CartUpdateResponse> updateCart(@RequestBody CartUpdateRequest request) {
        log.info("Controller: update cart {}", request.getCartId());
        return APIResponse.<CartUpdateResponse>builder()
                .code(200)
                .data(cartService.updateCart(request))
                .message("Update successfully")
                .build();
    }
    @GetMapping("/products")
    public APIResponse<CartResponse> products(@RequestParam Long userId) {
        log.info("Controller: get products in cart of user {}", userId);
        return APIResponse.<CartResponse>builder()
                .code(200)
                .data(cartService.products(userId))
                .message("Get products' cart successfully")
                .build();
    }
    @DeleteMapping("/delete")
    public APIResponse<CartResponse> deleteCart(
            @RequestParam Long userId,
            @RequestParam Long cartId) {
        log.info("Controller: delete cart id {} of user {}", cartId , userId);
        return APIResponse.<CartResponse>builder()
                .code(204)
                .data(cartService.deleteCart(userId, cartId))
                .message("Delete all cart items successfully")
                .build();
    }
    @DeleteMapping("/delete-item")
    public APIResponse<CartResponse> deleteCartItem(
            @RequestParam Long userId,
            @RequestParam Long cartId,
            @RequestParam Long shopId,
            @RequestParam Long productId) {
        log.info("Controller: delete product {} of shop {} in cart {} of user {}", productId, shopId, cartId, userId);
        return APIResponse.<CartResponse>builder()
                .code(204)
                .data(cartService.deleteCartItem(userId, cartId, shopId, productId))
                .message("Delete item successfully")
                .build();
    }

}
