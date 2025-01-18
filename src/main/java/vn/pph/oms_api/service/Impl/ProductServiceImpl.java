package vn.pph.oms_api.service.Impl;

import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import vn.pph.oms_api.dto.request.ProductCreationRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.pph.oms_api.dto.request.SkuCreationRequest;
import vn.pph.oms_api.dto.response.PageResponse;
import vn.pph.oms_api.dto.response.ProductCreationResponse;
import vn.pph.oms_api.dto.response.ProductResponse;
import vn.pph.oms_api.dto.response.SkuResponse;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.model.sku.Attribute;
import vn.pph.oms_api.model.sku.AttributeValue;
import vn.pph.oms_api.model.sku.Product;
import org.springframework.stereotype.Service;
import vn.pph.oms_api.model.sku.Sku;
import vn.pph.oms_api.repository.*;
import vn.pph.oms_api.service.ProductService;
import vn.pph.oms_api.utils.ProductUtils;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {
    UserRepository userRepository;
    AttributeRepository attributeRepository;
    ProductRepository productRepository;
    AttributeValueRepository attributeValueRepository;
    SkuRepository skuRepository;

    @Override
    @Transactional
    public ProductCreationResponse addProduct(ProductCreationRequest productRequest) {
        checkShopId(productRequest.getShopId());
        log.info("Service: Adding product with name {}", productRequest.getProductName());

        // Check if product exists
        if (productRepository.existsByProductName(productRequest.getProductName())) {
            log.error("Service: Product with name {} already exists", productRequest.getProductName());
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }

        // Check if SKU list is provided
        List<SkuCreationRequest> skuReqList = productRequest.getSkuList();
        if (skuReqList.isEmpty()) {
            log.error("Service: Product {} requires SKU list but none provided", productRequest.getProductName());
            throw new AppException(ErrorCode.MISSING_SKU);
        }

        // Create product
        Product product = Product.builder()
                .productName(productRequest.getProductName())
                .productThumb(productRequest.getProductThumb())
                .productDesc(productRequest.getProductDesc())
                .productShopId(productRequest.getShopId())
                .slug(ProductUtils.convertProductNameToSlug(productRequest.getProductName()))
                .skuList(new ArrayList<>())
                .build();

        Product productSave1st = productRepository.save(product);
        log.info("Service: Product {} created with id {}", productRequest.getProductName(), productSave1st.getId());

        List<Sku> skus = new ArrayList<>();
        boolean isSet = false;
        int totalStock = 0;
        Map<String, String> attrs = new HashMap<>();

        for (SkuCreationRequest skuReq : skuReqList) {
            totalStock += skuReq.getSkuStock();

            // Set default price for the product

            if (!isSet && skuReq.getIsDefault()) {
                productSave1st.setProductPrice(skuReq.getSkuPrice());
                isSet = true;
                log.info("Service: Default SKU price set to {}", skuReq.getSkuPrice());
            }

            // Create SKU
            Sku sku = Sku.builder()
                    .skuNo(productSave1st.getId() + "-" + skuReq.getSkuNo())
                    .skuName(skuReq.getSkuName())
                    .skuDescription(skuReq.getSkuDescription())
                    .skuPrice(skuReq.getSkuPrice())
                    .skuStock(skuReq.getSkuStock())
                    .isDefault(skuReq.getIsDefault())
                    .valueList(new ArrayList<>())
                    .status(!(skuReq.getSkuStock() > 0))
                    .build();

            sku.setProduct(productSave1st);
            log.info("Service: SKU {} created with stock {}", sku.getSkuNo(), skuReq.getSkuStock());
            skuRepository.save(sku);

            attrs = skuReq.getAttributes();
            // Assign attribute values and save
            for (Map.Entry<String, String> entry : attrs.entrySet()) {
                String attribute = entry.getKey();
                String value = entry.getValue();

                Attribute attr = attributeRepository.findByName(attribute)
                        .orElseThrow(() -> new AppException(ErrorCode.ATTR_NOT_EXIST));

                AttributeValue attrValue = AttributeValue.builder()
                        .sku(sku)
                        .attribute(attr)
                        .value(value)
                        .build();

                // Save attribute value
                attributeValueRepository.save(attrValue);
                log.info("Service: Attribute value {} for attribute {} saved for SKU {}", value, attribute, sku.getSkuNo());

                // Add attribute value to both Attribute and SKU
                attr.getValueList().add(attrValue);
                sku.getValueList().add(attrValue);

                // Save attribute
                attributeRepository.save(attr);
            }

            // Add SKU to the product's SKU list
            productSave1st.getSkuList().add(sku);
        }

        // Set product status based on total stock
        productSave1st.setProductStatus((totalStock == 0));
        log.info("Service: Product {} set stock is {}", productSave1st.getProductName(), totalStock);

        // Save product again after updating its SKUs and stock status
        productRepository.save(productSave1st);
        log.info("Service: Product {} updated and saved", productSave1st.getProductName());

        return ProductCreationResponse.builder()
                .name(productSave1st.getProductName())
                .stock(totalStock)
                .price(productSave1st.getProductPrice())
                .countSku(skuReqList.size())
                .build();
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = findProductById(productId);
        List<Sku> skuList = product.getSkuList();
        ProductResponse productResponse = ProductResponse.builder()
                .name(product.getProductName())
                .price(product.getProductPrice())
                .description(product.getProductDesc())
                .thumb(product.getProductThumb())
                .stock(skuList.stream().mapToInt(Sku::getSkuStock).sum())
                .skuCount(skuList.size())
                .build();
        return productResponse;
    }

    @Override
    public PageResponse<?> getAllProductsOfShop(Long shopId, int page, int size, String sortBy, String direction) {
        checkShopId(shopId);
        log.info("Service: get product list with size {}, page {}, by {}, direction {}", size, page, sortBy, direction);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortWith = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sortWith);
        Page<Product> products = productRepository.findAllByProductShopId(shopId, pageable);
        return convertToPageResponse(products, pageable);
    }

    @Override
    public PageResponse<?> productDraftList(Long shopId, int page, int size) {
        checkShopId(shopId);
        log.info("Service: get draft product list with size {}, page {}", size, page);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository
                .findProductsByShopAndStatus(shopId, true, false, pageable);
        return convertToPageResponse(products, pageable);
    }

    @Override
    public PageResponse<?> productPublishList(Long shopId, int page, int size) {
        checkShopId(shopId);
        log.info("Service: get publish product list with size {}, page {}", size, page);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository
                .findProductsByShopAndStatus(shopId, false, true, pageable);
        return convertToPageResponse(products, pageable);
    }

    @Override
    public PageResponse<?> listSkuByProductId(Long productId, int page, int size) {
        log.info("Service: get sku list with product id {} size {}, page {}", productId, size, page);
        Product product = findProductById(productId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Sku> skuPage = skuRepository.findAllByProduct(product, pageable);
        List<SkuResponse> responses = skuPage.stream()
                .map(sku -> SkuResponse.builder()
                        .skuNo(sku.getSkuNo())
                        .skuName(sku.getSkuName())
                        .skuDescription(sku.getSkuDescription())
                        .skuPrice(sku.getSkuPrice())
                        .skuStock(sku.getSkuStock())
                        .isDefault(sku.isDefault())
                        .build())
                .toList();
        return PageResponse.builder()
                .page(pageable.getPageNumber())
                .size(skuPage.getSize())
                .total(skuPage.getTotalElements())
                .items(responses)
                .build();
    }


    @Override
    public boolean publishProduct(Long shopId, Long productId) {
        checkShopId(shopId);
        Product product = findProductById(productId);
        log.info("Publishing product id {}", productId);
        product.setPublish(true);
        product.setDraft(false);
        productRepository.save(product);
        return true;
    }

    @Override
    public boolean unPublishProduct(Long shopId, Long productId) {
        checkShopId(shopId);
        Product product = findProductById(productId);
        log.info("Drafting product id {}", productId);
        product.setPublish(false);
        product.setDraft(true);
        productRepository.save(product);
        return true;
    }

    @Override
    public SkuResponse skuDetails(Long productId, Long skuId) {
        Product product = findProductById(productId);
        List<Sku> skuList = skuRepository.findAllByProduct(product);
        Sku sku = skuRepository.findById(skuId).orElseThrow(()-> new AppException(ErrorCode.SKU_NOT_FOUND));
        if (!skuList.contains(sku)) {
            throw new AppException(ErrorCode.SKU_INCOMPATIBLE_PRODUCT);
        }
        return SkuResponse.builder()
                .skuNo(sku.getSkuNo())
                .skuName(sku.getSkuName())
                .skuDescription(sku.getSkuDescription())
                .skuPrice(sku.getSkuPrice())
                .skuStock(sku.getSkuStock())
                .isDefault(sku.isDefault())
                .build();
    }

    @Override
    public PageResponse<List<ProductResponse>> convertToPageResponse(Page<Product> productPage, Pageable pageable) {
        log.info("Service: convert product to page response");

        // Map từng sản phẩm trong Page<Product> sang ProductResponse
        List<ProductResponse> responses = productPage.stream()
                .map(product -> ProductResponse.builder()
                        .name(product.getProductName())
                        .price(product.getProductPrice())
                        .description(product.getProductDesc())
                        .thumb(product.getProductThumb())
                        .stock(product.getSkuList().stream().mapToInt(Sku::getSkuStock).sum())
                        .skuCount(product.getSkuList().size())
                        .build())
                .toList();

        // Tạo PageResponse
        return PageResponse.<List<ProductResponse>>builder()
                .page(pageable.getPageNumber())
                .size(productPage.getSize())
                .total(productPage.getTotalPages())
                .items(responses)
                .build();
    }
    public void checkShopId(Long shopId) {
        log.info("Service: Checking shop id for shop {}", shopId);
        if (!userRepository.existsById(shopId)) {
            log.error("Service: Shop with id {} not found", shopId);
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
    }
    private Product findProductById(Long id) {
        log.info("Service: Get product id {}", id);
        return productRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }
////    static String LIKE_FORMAT = "%%%s%%";
////    @Override
////    public ProductResponse getProductsByName(String productName) {
////        if (StringUtils.hasLength(productName)) {
////
////        }
//    }
}
