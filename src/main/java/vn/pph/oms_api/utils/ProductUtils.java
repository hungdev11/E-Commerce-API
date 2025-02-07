package vn.pph.oms_api.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.model.sku.Product;
import vn.pph.oms_api.model.sku.Sku;
import vn.pph.oms_api.repository.ProductRepository;
import vn.pph.oms_api.repository.SkuRepository;
import vn.pph.oms_api.repository.UserRepository;

import java.util.StringJoiner;
@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductUtils {
    SkuRepository skuRepository;
    UserRepository userRepository;
    ProductRepository productRepository;

    public String convertProductNameToSlug(String productName) {
        String[] splitedArr = productName.strip().toLowerCase().split("\\s+");
        StringJoiner stringJoiner = new StringJoiner("-");
        for (String word : splitedArr) {
            stringJoiner.add(word.replaceAll("[^a-z0-9]", ""));
        }
        return stringJoiner.toString();
    }

    public boolean checkProductOfShop(Long productId, Long shopId) {
        log.info("Processing productId {} from shopId {}", productId, shopId);
        if (!userRepository.existsById(shopId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return productRepository.findById(productId)
                .map(product -> product.getProductShopId().equals(shopId))
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public void checkProductSkuShop(Long productId, Long shopId, String skuNumber) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if (product.isDraft()) {
            log.warn("Product {} is not public", product.getId());
            throw new AppException(ErrorCode.PRODUCT_IS_PRIVATE);
        }
        if (!product.getProductShopId().equals(shopId)) {
            log.warn("Product {} does not belong to shop {}", productId, shopId);
            throw new AppException(ErrorCode.PRODUCT_NOT_BELONG_TO_SHOP);
        }
        boolean skuExists = product.getSkuList().stream()
                .anyMatch(sku -> sku.getSkuNo().equals(skuNumber));
        if (!skuExists) {
            log.warn("SKU {} not found in Product {}", skuNumber, productId);
            throw new AppException(ErrorCode.SKU_INCOMPATIBLE_PRODUCT);
        }
    }


}
