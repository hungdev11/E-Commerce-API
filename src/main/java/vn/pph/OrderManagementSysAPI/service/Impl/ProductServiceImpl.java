package vn.pph.OrderManagementSysAPI.service.Impl;

import vn.pph.OrderManagementSysAPI.request.ProductCreationRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.pph.OrderManagementSysAPI.model.Product;
import org.springframework.stereotype.Service;
import vn.pph.OrderManagementSysAPI.repository.ProductRepository;
import vn.pph.OrderManagementSysAPI.service.ProductService;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    @Override
    public long addProduct(ProductCreationRequest productRequest) {
        log.info("Service: add product");
        if (productRepository.existsByName(productRequest.getName())) {
            log.error("Service: add failed");
            throw new RuntimeException("Product already existed");
        }
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .stock(productRequest.getStock())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        log.info("Service: add product successfully");
        return product.getId();
    }
}
