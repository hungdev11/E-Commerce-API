package vn.pph.oms_api.service;

import vn.pph.oms_api.dto.request.CartUpdateRequest;
import vn.pph.oms_api.dto.request.ProductAddToCartRequest;
import vn.pph.oms_api.dto.response.CartResponse;
import vn.pph.oms_api.dto.response.CartUpdateResponse;

import java.util.List;

public interface CartService {
    void createCart(Long userId);
    void addProductToCart(ProductAddToCartRequest request);
    CartUpdateResponse updateCart(CartUpdateRequest request);
    CartResponse products(Long userId);
    CartResponse deleteCart(Long userId, Long cartId);
    CartResponse deleteCartItem(Long userId, Long cartId, Long shopId, Long productId);
}
