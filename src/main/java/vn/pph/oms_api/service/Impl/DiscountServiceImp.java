package vn.pph.oms_api.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.pph.oms_api.dto.request.discount.DiscountCreationRequest;
import vn.pph.oms_api.dto.request.order.MockProductOrder;
import vn.pph.oms_api.dto.request.discount.RequestGetAmountDiscount;
import vn.pph.oms_api.dto.response.cart.AmountRequest;
import vn.pph.oms_api.dto.response.discount.DiscountCreationResponse;
import vn.pph.oms_api.dto.response.discount.DiscountResponse;
import vn.pph.oms_api.dto.response.PageResponse;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.model.Discount;
import vn.pph.oms_api.model.User;
import vn.pph.oms_api.model.sku.Product;
import vn.pph.oms_api.repository.DiscountRepository;
import vn.pph.oms_api.repository.ProductRepository;
import vn.pph.oms_api.repository.UserRepository;
import vn.pph.oms_api.service.DiscountService;
import vn.pph.oms_api.utils.DiscountApplyTo;
import vn.pph.oms_api.utils.DiscountStatus;
import vn.pph.oms_api.utils.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        // Check value with percentage
        if (request.getType().equals(DiscountType.PERCENTAGE)) {
            log.info("Checking discount value with Percentage");
            if (request.getValue().doubleValue() > 100) {
                log.info("Percent value is out of range [0, 100]");
                throw new AppException(ErrorCode.INVALID_PERCENT_VALUE);
            }
        }

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

    @Override
    public PageResponse<?> getAllDiscountsForShop(Long shopId, int page, int size, DiscountStatus status) {
        log.info("Get all discount of shop id {}", shopId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Discount> discountList = discountRepository.findAllByShopId(shopId, status, pageable);
        List<DiscountResponse> responses = discountList.stream()
                .map(d -> DiscountResponse.builder()
                        .name(d.getName())
                        .code(d.getCode())
                        .type(d.getType())
                        .value(d.getValue())
                        .minOrderValue(d.getMinOrderValue())
                        .description(d.getDescription())
                        .maximumQuantity(d.getMaximumQuantity())
                        .maxPerUser(d.getMaxPerUser())
                        .startDate(d.getStartDate())
                        .endDate(d.getEndDate())
                        .productIds(d.getProductIds())
                        .build())
                .toList();
        return PageResponse.builder()
                .page(pageable.getPageNumber())
                .size(discountList.getSize())
                .total(discountList.getTotalElements())
                .items(responses)
                .build();
    }

    @Override
    @Transactional // Not yet check prices in db to compare prices FE send, just calculate based on FE's prices
    public AmountRequest getDiscountAmount(RequestGetAmountDiscount request) {
        // check user
        User user = userRepository.findById(request.getUserId()).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        Discount discount = discountRepository.findByCode(request.getCode()).orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));
        // check date
        if (discount.getStartDate().isAfter(LocalDateTime.now())
                || discount.getEndDate().isBefore(LocalDateTime.now())
                || discount.getStatus().equals(DiscountStatus.INACTIVE)
        ){
            log.info("Can not use this code anymore, cuz it out of time");
            throw new AppException(ErrorCode.CANNOT_USE_DISCOUNT);
        }
        // check discount quantity
        int maxQuantity = discount.getMaximumQuantity();
        if (maxQuantity == 0) {
            log.info("Can not use this code anymore, cuz it out of quantity");
            throw new AppException(ErrorCode.DISCOUNT_OUT_OF_QUANTITY);
        }
        // check discount per user can use
        long usedTimes = discount.getUsersUsed().stream().filter(u -> Objects.equals(user.getId(), u.getId())).count();
        log.info("This user use this discount code {} times", usedTimes);
        if (discount.getMaxPerUser() <= usedTimes) {
            log.info("Can not use this code anymore, cuz user use so many times");
            throw new AppException(ErrorCode.DISCOUNT_OUT_OF_QUANTITY);
        }
        // check is order: is product of that shop can apply discount, check total is enough
        List<MockProductOrder> productOrders = request.getProductOrders();
        BigDecimal totalOfOrder = productOrders.stream()
                .reduce(
                        BigDecimal.ZERO,
                        (sum, order) -> sum.add(order.getPrice().multiply(new BigDecimal(order.getQuantity()))),
                        BigDecimal::add
                );
        List<MockProductOrder> canApplyDiscount = productOrders.stream()
                .filter(p -> discount.getProductIds().contains(p.getProductId()))
                .toList();
        BigDecimal totalOfProductCanApplyDiscount = canApplyDiscount.stream()
                .reduce(
                        BigDecimal.ZERO,
                        (sum, order) -> sum.add(order.getPrice().multiply(new BigDecimal(order.getQuantity()))),
                        BigDecimal::add
                );
        BigDecimal totalApplied = BigDecimal.ZERO;
        if (discount.getMinOrderValue().compareTo(totalOfProductCanApplyDiscount) <= 0) {
            if (discount.getType().equals(DiscountType.FIX_AMOUNT)) {
                totalApplied = totalOfProductCanApplyDiscount.subtract(discount.getValue());
            } else {
                BigDecimal discountRate = BigDecimal.ONE.subtract(discount.getValue().divide(BigDecimal.valueOf(100)));
                totalApplied = totalOfProductCanApplyDiscount.multiply(discountRate);
            }
        }
        // its just check amount, not pay yet
//        discount.getUsersUsed().add(user);
//        discount.setMaximumQuantity(maxQuantity - 1);
//        discountRepository.save(discount);
        return AmountRequest.builder()
                .userId(user.getId())
                .originPrice(totalOfOrder)
                .discountPrice(totalApplied.compareTo(BigDecimal.ZERO) == 0
                        ? BigDecimal.ZERO
                        : totalOfOrder.subtract(totalOfProductCanApplyDiscount).add(totalApplied))
                .build();
    }

    // delete discount (*admin/shop)
    @Override
    @Transactional
    public boolean deleteDiscount(Long shopId, Long codeId) {
        Discount discount = discountRepository.findById(codeId).orElseThrow(()-> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));
        if (!discount.getUsersUsed().isEmpty()) {
            throw new AppException(ErrorCode.DISCOUNT_HAS_USED);
        }
        // check code belongs to shop
        if (!discount.getShopId().equals(shopId)) {
            throw new AppException(ErrorCode.DISCOUNT_NOT_BELONG_TO_SHOP);
        }
        discount.setDiscountDeleted(true);
        return discountRepository.save(discount).isDiscountDeleted();
    }

    //cancel discount (user)
    @Override
    public boolean cancelDiscount(Long shopId, Long codeId, Long userId) {
        // Fetch user and discount, throwing an exception if not found
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Discount discount = discountRepository.findById(codeId)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));

        if (!discount.getShopId().equals(shopId)) {
            throw new AppException(ErrorCode.DISCOUNT_NOT_BELONG_TO_SHOP);
        }
        // Increase available discount quantity
        discount.setMaximumQuantity(discount.getMaximumQuantity() + 1);

        // Remove user from the discount's used list (only one occurrence)
        List<User> allUsers = discount.getUsersUsed();
        for (int i = allUsers.size() - 1; i >= 0; i--) {
            if (allUsers.get(i).getId().equals(userId)) {
                allUsers.remove(i); // Xóa lần gần nhất
                break; // Dừng ngay sau khi xóa
            }
        }
        discountRepository.save(discount);
        return true; // Indicate successful cancellation
    }

}
