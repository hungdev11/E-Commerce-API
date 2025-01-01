package vn.pph.OrderManagementSysAPI.service.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vn.pph.OrderManagementSysAPI.dto.request.ProductCreationRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.pph.OrderManagementSysAPI.dto.response.PageResponse;
import vn.pph.OrderManagementSysAPI.dto.response.ProductResponse;
import vn.pph.OrderManagementSysAPI.exception.AppException;
import vn.pph.OrderManagementSysAPI.exception.ErrorCode;
import vn.pph.OrderManagementSysAPI.mapper.ProductMapper;
import vn.pph.OrderManagementSysAPI.model.Product;
import org.springframework.stereotype.Service;
import vn.pph.OrderManagementSysAPI.repository.ProductRepository;
import vn.pph.OrderManagementSysAPI.service.ProductService;

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
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortWith = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sortWith);
        Page<Product> products = productRepository.findAll(pageable);
        return convertToPageResponse(products, pageable);
    }

    @Override
    public Product getProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public PageResponse<?> convertToPageResponse(Page<Product> productPage, Pageable pageable) {
        List<ProductResponse> responses = productPage.map(productMapper::toProductResponse).toList();
        return PageResponse.builder()
                .page(pageable.getPageNumber())
                .size(productPage.getSize())
                .total(productPage.getTotalPages())
                .items(responses)
                .build();
    }
}
