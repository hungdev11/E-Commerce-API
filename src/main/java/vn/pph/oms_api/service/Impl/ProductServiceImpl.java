package vn.pph.oms_api.service.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import vn.pph.oms_api.dto.request.ProductCreationRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.pph.oms_api.dto.request.SkuCreationRequest;
import vn.pph.oms_api.dto.response.PageResponse;
import vn.pph.oms_api.dto.response.ProductResponse;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.mapper.ProductMapper;
import vn.pph.oms_api.model.sku.Attribute;
import vn.pph.oms_api.model.sku.AttributeValue;
import vn.pph.oms_api.model.sku.Product;
import org.springframework.stereotype.Service;
import vn.pph.oms_api.model.sku.Sku;
import vn.pph.oms_api.repository.*;
import vn.pph.oms_api.service.ProductService;
import vn.pph.oms_api.utils.ProductUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ProductResponse addProduct(ProductCreationRequest productRequest) {
        log.info("Service: Checking shop id for shop {}", productRequest.getShopId());

        if (!userRepository.existsById(productRequest.getShopId())) {
            log.error("Service: Shop with id {} not found", productRequest.getShopId());
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

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

        return ProductResponse.builder()
                .name(productSave1st.getProductName())
                .stock(totalStock)
                .price(productSave1st.getProductPrice())
                .countSku(skuReqList.size())
                .build();
    }
//
//    @Override
//    public ProductResponse getProductById(Long productId) {
//        log.info("Service: get product by id {} ", productId);
//        Product product = getProduct(productId);
//        return productMapper.toProductResponse(product);
//    }
//
//    @Override
//    public PageResponse<?> getProductList(int page, int size, String sortBy, String direction) {
//        log.info("Service: get product list with size {}, page {}, by {}, direction {}", size, page, sortBy, direction);
//        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
//        Sort sortWith = Sort.by(sortDirection, sortBy);
//        Pageable pageable = PageRequest.of(page, size, sortWith);
//        Page<Product> products = productRepository.findAll(pageable);
//        return convertToPageResponse(products, pageable);
//    }
//
//    @Override
//    public Product getProduct(Long productId) {
//        log.info("Service: find product with product id");
//        return productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
//    }
//
//    @Override
//    public PageResponse<?> convertToPageResponse(Page<Product> productPage, Pageable pageable) {
//        log.info("Service: convert product to page response");
//        List<ProductResponse> responses = productPage.map(productMapper::toProductResponse).toList();
//        return PageResponse.builder()
//                .page(pageable.getPageNumber())
//                .size(productPage.getSize())
//                .total(productPage.getTotalPages())
//                .items(responses)
//                .build();
//    }
////    static String LIKE_FORMAT = "%%%s%%";
////    @Override
////    public ProductResponse getProductsByName(String productName) {
////        if (StringUtils.hasLength(productName)) {
////
////        }
//    }
}
