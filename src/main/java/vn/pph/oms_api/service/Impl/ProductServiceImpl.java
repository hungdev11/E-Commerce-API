package vn.pph.oms_api.service.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vn.pph.oms_api.dto.request.ProductCreationRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.pph.oms_api.dto.response.PageResponse;
import vn.pph.oms_api.dto.response.ProductResponse;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.mapper.ProductMapper;
import vn.pph.oms_api.model.Product;
import org.springframework.stereotype.Service;
import vn.pph.oms_api.repository.ProductRepository;
import vn.pph.oms_api.service.ProductService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    @Override
    public long addProduct(ProductCreationRequest productRequest) {
        log.info("Service: add product");
        if (productRepository.existsByName(productRequest.getName())) {
            log.error("Service: add failed");
            throw new AppException(ErrorCode.PRODUCT_EXITED);
        }
        Product product = productMapper.toProduct(productRequest);
        productRepository.save(product);
        log.info("Service: add product successfully");
        return product.getId();
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        log.info("Service: get product by id {} ", productId);
        Product product = getProduct(productId);
        return productMapper.toProductResponse(product);
    }

    @Override
    public PageResponse<?> getProductList(int page, int size, String sortBy, String direction) {
        log.info("Service: get product list with size {}, page {}, by {}, direction {}", size, page, sortBy, direction);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortWith = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sortWith);
        Page<Product> products = productRepository.findAll(pageable);
        return convertToPageResponse(products, pageable);
    }

    @Override
    public Product getProduct(Long productId) {
        log.info("Service: find product with product id");
        return productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public PageResponse<?> convertToPageResponse(Page<Product> productPage, Pageable pageable) {
        log.info("Service: convert product to page response");
        List<ProductResponse> responses = productPage.map(productMapper::toProductResponse).toList();
        return PageResponse.builder()
                .page(pageable.getPageNumber())
                .size(productPage.getSize())
                .total(productPage.getTotalPages())
                .items(responses)
                .build();
    }
//    static String LIKE_FORMAT = "%%%s%%";
//    @Override
//    public ProductResponse getProductsByName(String productName) {
//        if (StringUtils.hasLength(productName)) {
//
//        }
//    }
}
