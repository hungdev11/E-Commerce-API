package vn.pph.OrderManagementSysAPI.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.pph.OrderManagementSysAPI.dto.request.ProductCreationRequest;
import vn.pph.OrderManagementSysAPI.dto.response.PageResponse;
import vn.pph.OrderManagementSysAPI.dto.response.ProductResponse;
import vn.pph.OrderManagementSysAPI.model.Product;

import java.util.List;

public interface ProductService {
    long addProduct(ProductCreationRequest product);
    ProductResponse getProductById(Long productId);
    PageResponse<?> getProductList(int page, int size, String sortBy, String direction);
    Product getProduct(Long productId);
    PageResponse<?> convertToPageResponse(Page<Product> productPage, Pageable pageable);
}
