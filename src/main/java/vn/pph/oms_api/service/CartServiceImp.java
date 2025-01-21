package vn.pph.oms_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.pph.oms_api.dto.request.CartItem;
import vn.pph.oms_api.dto.request.CartUpdateRequest;
import vn.pph.oms_api.dto.request.OrderShop;
import vn.pph.oms_api.dto.request.ProductAddToCartRequest;
import vn.pph.oms_api.dto.response.CartUpdateResponse;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.model.Cart;
import vn.pph.oms_api.model.CartProduct;
import vn.pph.oms_api.model.sku.Product;
import vn.pph.oms_api.repository.CartRepository;
import vn.pph.oms_api.repository.ProductRepository;
import vn.pph.oms_api.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImp implements CartService{
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    /**
     * Call when user sign up
     * @param userId
     */
    @Transactional
    @Override
    public void createCart(Long userId) {
        log.info("Check cart is exists with userId {}", userId);
        if (cartRepository.existsByUserId(userId)) {
            log.info("User id already have cart, Some thing wrong in the system");
            throw new AppException(ErrorCode.SOME_THING_WENT_WRONG);
        }
        Cart newCart = Cart.builder()
                .userId(userId)
                .build();
        cartRepository.save(newCart);
        log.info("Save cart successfully to user id {}", userId);
    }

    @Override
    @Transactional
    public void addProductToCart(ProductAddToCartRequest request) {
        log.info("Validating user with id {}", request.getUserId());
        if (!userRepository.existsById(request.getUserId())) {
            log.error("User not found for userId {}", request.getUserId());
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> {
                    log.error("Cart not found for userId {}", request.getUserId());
                    return new AppException(ErrorCode.CART_NOT_FOUND);
                });

        List<CartProduct> cartProducts = cart.getProducts();
        Long productId = request.getProductId();
        CartProduct existingProduct = cartProducts.stream()
                .filter(product -> product.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingProduct != null) {
            log.info("Product {} already exists in the cart. Updating quantity.", productId);
            if (!existingProduct.getShopId().equals(request.getShopId())) {
                log.error("Product {} does not belong to shop {}", productId, request.getShopId());
                throw new AppException(ErrorCode.PRODUCT_NOT_BELONG_TO_SHOP);
            }
            existingProduct.setQuantity(existingProduct.getQuantity() + request.getQuantity());
        } else {
            log.info("Adding new product {} to cart.", productId);
            CartProduct newProduct = CartProduct.builder()
                    .productId(productId)
                    .shopId(request.getShopId())
                    .quantity(request.getQuantity())
                    .build();
            cartProducts.add(newProduct);
            cart.setCartCount(cart.getCartCount() + 1);
        }

        cartRepository.save(cart);
        log.info("Cart updated successfully for userId {}", request.getUserId());
    }


    @Override
    @Transactional
    public CartUpdateResponse updateCart(CartUpdateRequest request) {
        log.info("Validating cart with id {}", request.getCartId());
        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> {
                    log.error("Cart not found for id {}", request.getCartId());
                    return new AppException(ErrorCode.CART_NOT_FOUND);
                });

        List<CartProduct> cartProducts = cart.getProducts();
        List<OrderShop> productsShop = request.getOrderShopsList();

        Long updatedShopId = null;
        Long updatedProductId = null;
        int updatedQuantity = 0;

        for (OrderShop shop : productsShop) {
            for (CartItem item : shop.getItems()) {
                log.info("Processing productId {} from shopId {}", item.getProductId(), shop.getShopId());
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> {
                            log.error("Product not found for productId {}", item.getProductId());
                            return new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                        });

                if (!product.getProductShopId().equals(shop.getShopId())) {
                    log.error("Product {} does not belong to shop {}", item.getProductId(), shop.getShopId());
                    throw new AppException(ErrorCode.PRODUCT_NOT_BELONG_TO_SHOP);
                }

                CartProduct cartProduct = cartProducts.stream()
                        .filter(cp -> cp.getProductId().equals(item.getProductId()))
                        .findFirst()
                        .orElseThrow(() -> {
                            log.error("Product {} not found in cart for shop {}", item.getProductId(), shop.getShopId());
                            return new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                        });

                int diffQuantity = item.getNewQuantity() - item.getOldQuantity();
                if (diffQuantity != 0) {
                    cartProduct.setQuantity(cartProduct.getQuantity() + diffQuantity);
                    if (cartProduct.getQuantity() <= 0) {
                        log.info("Removing product {} from cart due to zero quantity", cartProduct.getProductId());
                        cartProducts.remove(cartProduct);
                        cart.setCartCount(cart.getCartCount() - 1);
                    }
                    updatedShopId = shop.getShopId();
                    updatedProductId = item.getProductId();
                    updatedQuantity = cartProduct.getQuantity();
                }
            }
        }

        cartRepository.save(cart);
        log.info("Cart successfully updated for cartId {}", request.getCartId());

        return CartUpdateResponse.builder()
                .shopId(updatedShopId)
                .productId(updatedProductId)
                .quantity(updatedQuantity)
                .cardCount(cart.getCartCount())
                .build();
    }



}
