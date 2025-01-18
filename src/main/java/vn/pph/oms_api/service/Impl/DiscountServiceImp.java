package vn.pph.oms_api.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.pph.oms_api.dto.request.DiscountCreationRequest;
import vn.pph.oms_api.dto.response.DiscountCreationResponse;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.model.Discount;
import vn.pph.oms_api.model.sku.Product;
import vn.pph.oms_api.repository.DiscountRepository;
import vn.pph.oms_api.repository.ProductRepository;
import vn.pph.oms_api.repository.UserRepository;
import vn.pph.oms_api.service.DiscountService;
import vn.pph.oms_api.utils.DiscountApplyTo;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountServiceImp implements DiscountService {
    private final DiscountRepository discountRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public DiscountCreationResponse createNewDiscount(DiscountCreationRequest request) {
        log.info("Service: Create new discount with code: {}", request.getCode());

        // Validate the shop ID
        log.info("Validating shop ID: {}", request.getShopId());
        userRepository.findById(request.getShopId())
                .orElseThrow(() -> {
                    log.error("Shop ID {} not found.", request.getShopId());
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });
        log.info("Shop ID {} found.", request.getShopId());

        // Check if discount already exists for the shop and code
        if (discountRepository.existsByShopIdAndCode(request.getShopId(), request.getCode())) {
            log.error("Discount with code {} already exists for shop {}", request.getCode(), request.getShopId());
            throw new AppException(ErrorCode.DISCOUNT_EXISTED);
        }
        log.info("Discount code {} is unique for shop {}", request.getCode(), request.getShopId());

        // Validate discount quantities
        if (request.getMaximumQuantity() < request.getMaxPerUser()) {
            log.error("Maximum quantity {} is less than max per user {}", request.getMaximumQuantity(), request.getMaxPerUser());
            throw new AppException(ErrorCode.INVALID_DISCOUNT_QUANTITY);
        }
        log.info("Discount quantity validated. Maximum: {}, Max per user: {}", request.getMaximumQuantity(), request.getMaxPerUser());

        // If the discount applies to specific products, validate the product IDs
        boolean hasList = false;
        if (request.getApplyTo().equals(DiscountApplyTo.SPECIFIC)) {
            log.info("Checking product IDs list: {}", request.getProductIds());
            request.getProductIds().forEach(pId -> {
                Product product = productRepository.findById(pId).orElseThrow(() -> {
                    log.error("Product with ID {} not found.", pId);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                });
                if (product.getProductShopId() != request.getShopId()) {
                    log.error("Product id {} not belong to shop id {}", pId, request.getShopId());
                    throw new AppException(ErrorCode.PRODUCT_NOT_BELONG_TO_SHOP);
                }
            });
            hasList = true;
            log.info("Product IDs validated: {}", request.getProductIds());
        }

        // Map the DiscountCreationRequest to Discount entity
        log.info("Mapping DiscountCreationRequest to Discount entity.");
        Discount discount = Discount.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .value(request.getValue())
                .code(request.getCode())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .maximumQuantity(request.getMaximumQuantity())
                .maxPerUser(request.getMaxPerUser())
                .minOrderValue(request.getMinOrderValue())
                .shopId(request.getShopId())
                .status(request.getStatus())
                .applyTo(request.getApplyTo())
                .productIds(hasList ? request.getProductIds() : new ArrayList<>())
                .build();

        // Log the discount details before saving
        log.info("Saving the following discount: {}", discount);

        // Save the Discount entity
        Discount savedDiscount = discountRepository.save(discount);
        log.info("Discount saved with ID: {}", savedDiscount.getId());

        // Map the saved Discount entity to DiscountCreationResponse
        log.info("Mapping saved Discount entity to DiscountCreationResponse.");
        return DiscountCreationResponse.builder()
                .shopId(savedDiscount.getShopId())
                .name(savedDiscount.getName())
                .code(savedDiscount.getCode())
                .type(savedDiscount.getType())
                .value(savedDiscount.getValue())
                .description(savedDiscount.getDescription())
                .maximumQuantity(savedDiscount.getMaximumQuantity())
                .maxPerUser(savedDiscount.getMaxPerUser())
                .startDate(savedDiscount.getStartDate())
                .endDate(savedDiscount.getEndDate())
                .minOrderValue(savedDiscount.getMinOrderValue())
                .status(savedDiscount.getStatus())
                .productIds(savedDiscount.getProductIds())
                .build();
    }
}
