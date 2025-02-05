package vn.pph.oms_api.service.Impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.pph.oms_api.dto.request.discount.RequestGetAmountDiscount;
import vn.pph.oms_api.dto.request.order.*;
import vn.pph.oms_api.dto.response.cart.AmountRequest;
import vn.pph.oms_api.dto.response.order.CheckoutResponse;
import vn.pph.oms_api.dto.response.order.Item;
import vn.pph.oms_api.dto.response.order.ShopOrderRes;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.model.Cart;
import vn.pph.oms_api.model.Discount;
import vn.pph.oms_api.model.sku.Product;
import vn.pph.oms_api.repository.DiscountRepository;
import vn.pph.oms_api.repository.ProductRepository;
import vn.pph.oms_api.service.CheckoutService;
import vn.pph.oms_api.service.DiscountService;
import vn.pph.oms_api.service.ProductService;
import vn.pph.oms_api.utils.CartStatus;
import vn.pph.oms_api.utils.UserUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckoutServiceImp implements CheckoutService {
    UserUtils userUtils;
    DiscountService discountService;
    ProductService productService;
    ProductRepository productRepository;
    DiscountRepository discountRepository;

    @Override
    public CheckoutResponse review(ReviewOrderRequest request) {
        log.info("Start review order for userId: {}", request.getUserId());

        // Kiểm tra giỏ hàng của user
        Cart cart = userUtils.checkCartOfUser(request.getUserId());
        if (!cart.getStatus().equals(CartStatus.ACTIVE)) {
            log.error("Cart status is invalid for userId: {} (cartId: {})", request.getUserId(), request.getCartId());
            throw new AppException(ErrorCode.CART_STATUS_INVALID);
        }
        log.info("Cart {} is valid for userId: {}", request.getCartId(), request.getUserId());

        List<ShopOrder> shopOrders = request.getShopOrders();
        List<ShopOrderRes> orderRes = new ArrayList<>();

        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (ShopOrder shopOrder : shopOrders) {
            log.info("Processing shop order for shopId: {}", shopOrder.getShopId());

            ShopOrderRes shopOrderRes = ShopOrderRes.builder()
                    .shopId(shopOrder.getShopId())
                    .items(new ArrayList<>())
                    .build();

            // Lấy danh sách sản phẩm của shop từ database
            List<Product> shopProducts = productRepository.findAllByProductShopId(shopOrder.getShopId());
            Map<Long, Product> productMap = shopProducts.stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));

            BigDecimal totalPerShop = BigDecimal.ZERO;
            List<MockProductOrder> productCanApplyDiscount = new ArrayList<>();

            for (ItemProduct item : shopOrder.getItemProducts()) {
                log.info("Checking productId: {} (quantity: {}) in shop {}", item.getProductId(), item.getQuantity(), shopOrder.getShopId());

                Product product = productMap.get(item.getProductId());
                if (product == null) {
                    log.error("Product {} not found in shop {}", item.getProductId(), shopOrder.getShopId());
                    throw new AppException(ErrorCode.PRODUCT_NOT_BELONG_TO_SHOP);
                }

                productCanApplyDiscount.add(MockProductOrder.builder()
                        .shopId(shopOrder.getShopId())
                        .price(product.getProductPrice())
                        .quantity(item.getQuantity())
                        .productId(product.getId())
                        .build());

                shopOrderRes.getItems().add(Item.builder()
                        .price(product.getProductPrice())
                        .quantity(item.getQuantity())
                        .shopId(shopOrderRes.getShopId())
                        .build());

                totalPerShop = totalPerShop.add(product.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }

            log.info("Total price for shop {} before discount: {}", shopOrder.getShopId(), totalPerShop);

            BigDecimal shopDiscountAmount = BigDecimal.ZERO;
            for (ShopDiscount shopDiscount : shopOrder.getShopDiscounts()) {
                log.info("Applying discount code {} for shop {}", shopDiscount.getCodeId(), shopOrder.getShopId());

                Discount discount = discountRepository.findByCode(shopDiscount.getCodeId())
                        .orElseThrow(() -> {
                            log.error("Invalid discount code {} for shop {}", shopDiscount.getCodeId(), shopOrder.getShopId());
                            return new AppException(ErrorCode.DISCOUNT_NOT_BELONG_TO_SHOP);
                        });

                AmountRequest amount = discountService.getDiscountAmount(RequestGetAmountDiscount.builder()
                        .code(discount.getCode())
                        .userId(request.getUserId())
                        .productOrders(productCanApplyDiscount)
                        .build());

                shopDiscountAmount = shopDiscountAmount.add(amount.getDiscountPrice());
            }

            log.info("Total discount for shop {}: {}", shopOrder.getShopId(), shopDiscountAmount);

            shopOrderRes.setDiscount(totalPerShop.subtract(shopDiscountAmount));
            shopOrderRes.setPrice(totalPerShop);
            shopOrderRes.setNewPrice(shopDiscountAmount);

            totalPrice = totalPrice.add(totalPerShop);
            totalDiscount = totalDiscount.add(totalPerShop.subtract(shopDiscountAmount));

            orderRes.add(shopOrderRes);
        }

        BigDecimal feeShip = new BigDecimal(6);
        totalPrice = totalPrice.add(feeShip);

        log.info("Final totalPrice: {}, totalDiscount: {}, feeShip: {}, finalCheckout: {}", totalPrice, totalDiscount, feeShip, totalPrice.subtract(totalDiscount));

        return CheckoutResponse.builder()
                .totalCheckout(totalPrice.subtract(totalDiscount))
                .feeShip(feeShip)
                .totalPrice(totalPrice)
                .totalDiscount(totalDiscount)
                .shopOrders(orderRes)
                .build();
    }
}
