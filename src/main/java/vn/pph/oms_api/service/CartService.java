package vn.pph.oms_api.service;

import vn.pph.oms_api.dto.request.CartUpdateRequest;
import vn.pph.oms_api.dto.request.ProductAddToCartRequest;
import vn.pph.oms_api.dto.response.CartUpdateResponse;

public interface CartService {
    void createCart(Long userId);
    void addProductToCart(ProductAddToCartRequest request);
    CartUpdateResponse updateCart(CartUpdateRequest request);
}
