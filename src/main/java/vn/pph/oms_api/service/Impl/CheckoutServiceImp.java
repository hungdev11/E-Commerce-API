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
import vn.pph.oms_api.model.sku.Sku;
import vn.pph.oms_api.repository.DiscountRepository;
import vn.pph.oms_api.repository.ProductRepository;
import vn.pph.oms_api.repository.SkuRepository;
import vn.pph.oms_api.service.CheckoutService;
import vn.pph.oms_api.service.DiscountService;
import vn.pph.oms_api.service.ProductService;
import vn.pph.oms_api.utils.CartStatus;
import vn.pph.oms_api.utils.ProductUtils;
import vn.pph.oms_api.utils.UserUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckoutServiceImp implements CheckoutService {
    UserUtils userUtils;
    ProductUtils productUtils;
    DiscountService discountService;
    SkuRepository skuRepository;
    ProductService productService;
    ProductRepository productRepository;
    DiscountRepository discountRepository;

    @Override
    public CheckoutResponse review(ReviewOrderRequest request) {
        log.info("Start review order for userId: {}", request.getUserId());

        Cart cart = userUtils.checkCartOfUser(request.getUserId());
        if (!cart.getStatus().equals(CartStatus.ACTIVE)) {
            log.error("Cart status is invalid for userId: {} (cartId: {})", request.getUserId(), request.getCartId());
            throw new AppException(ErrorCode.CART_STATUS_INVALID);
        }
        log.info("Cart {} is valid for userId: {}", request.getCartId(), request.getUserId());

        List<ShopOrder> shopOrders = request.getShopOrders();
        List<ShopOrderRes> orderResponses = new ArrayList<>();

        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (ShopOrder shopOrder : shopOrders) {
            log.info("Processing shop order for shopId: {}", shopOrder.getShopId());

            ShopOrderRes shopOrderRes = ShopOrderRes.builder()
                    .shopId(shopOrder.getShopId())
                    .items(new ArrayList<>())
                    .build();

            List<Product> shopProducts = productRepository.findAllByProductShopId(shopOrder.getShopId());
            Map<Long, Product> productMap = shopProducts.stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));

            BigDecimal totalPerShop = BigDecimal.ZERO;
            List<MockProductOrder> productsApplyDiscount = new ArrayList<>();

            for (ItemProduct item : shopOrder.getItemProducts()) {
                log.info("Checking productId: {} (quantity: {}) in shop {}", item.getProductId(), item.getQuantity(), shopOrder.getShopId());

                productUtils.checkProductSkuShop(item.getProductId(), shopOrder.getShopId(), item.getSkuCode());
                log.info("shop, product, sku are compatible");

                Product product = productMap.get(item.getProductId());
                Sku sku = product.getSkuList().stream().filter(s -> s.getSkuNo().equals(item.getSkuCode())).findFirst().get();
                BigDecimal price = sku.getSkuPrice();

                productsApplyDiscount.add(MockProductOrder.builder()
                        .shopId(shopOrder.getShopId())
                        .price(price)
                        .quantity(item.getQuantity())
                        .productId(product.getId())
                        .build());

                shopOrderRes.getItems().add(Item.builder()
                        .price(price)
                        .quantity(item.getQuantity())
                        .productId(product.getId())
                        .skuNo(sku.getSkuNo())
                        .build());

                totalPerShop = totalPerShop.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
            }

            log.info("Total price for shop {} before discount: {}", shopOrder.getShopId(), totalPerShop);

            BigDecimal shopDiscountAmount = BigDecimal.ZERO;
            List<ShopDiscount> shopDiscounts = shopOrder.getShopDiscounts();
            for (ShopDiscount shopDiscount : shopDiscounts) {
                log.info("Applying discount code {} for shop {}", shopDiscount.getCodeId(), shopOrder.getShopId());

                if (!shopDiscount.getShopId().equals(shopOrder.getShopId())) {
                    log.info("Shop {} in request different to shop in discount {}", shopOrder.getShopId(), shopDiscount.getShopId());
                    throw new AppException(ErrorCode.DISCOUNT_NOT_BELONG_TO_SHOP);
                }

                Discount discount = discountRepository.findByCode(shopDiscount.getCodeId())
                        .orElseThrow(() -> {
                            log.error("Invalid discount code {} for shop {}", shopDiscount.getCodeId(), shopOrder.getShopId());
                            return new AppException(ErrorCode.DISCOUNT_NOT_BELONG_TO_SHOP);
                        });

                AmountRequest amount = discountService.getDiscountAmount(RequestGetAmountDiscount.builder()
                        .code(discount.getCode())
                        .userId(request.getUserId())
                        .productOrders(productsApplyDiscount)
                        .build());

                shopDiscountAmount = shopDiscountAmount.add(amount.getDiscountPrice());
            }

            log.info("Total discount for shop {}: {}", shopOrder.getShopId(), shopDiscountAmount);

            boolean hasDiscount = shopDiscountAmount.compareTo(BigDecimal.ZERO) != 0;
            shopOrderRes.setDiscount( hasDiscount
                    ? totalPerShop.subtract(shopDiscountAmount)
                    : shopDiscountAmount);

            shopOrderRes.setPrice(totalPerShop);
            shopOrderRes.setNewPrice(hasDiscount ? shopDiscountAmount : totalPerShop);

            totalPrice = totalPrice.add(totalPerShop);

            totalDiscount = hasDiscount
                    ? totalDiscount.add(totalPerShop.subtract(shopDiscountAmount))
                    : BigDecimal.ZERO;

            orderResponses.add(shopOrderRes);
        }

        BigDecimal feeShip = new BigDecimal(6);
        totalPrice = totalPrice.add(feeShip);

        log.info("Final totalPrice: {}, totalDiscount: {}, feeShip: {}, finalCheckout: {}", totalPrice, totalDiscount, feeShip, totalPrice.subtract(totalDiscount));

        return CheckoutResponse.builder()
                .totalCheckout(totalPrice.subtract(totalDiscount))
                .feeShip(feeShip)
                .totalPrice(totalPrice)
                .totalDiscount(totalDiscount)
                .shopOrders(orderResponses)
                .build();
    }
}
