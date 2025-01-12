package vn.pph.oms_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.pph.oms_api.dto.request.ProductCreationRequest;
import vn.pph.oms_api.dto.response.PageResponse;
import vn.pph.oms_api.dto.response.ProductResponse;
import vn.pph.oms_api.model.sku.Product;

public interface ProductService {
    long addProduct(ProductCreationRequest product);
    ProductResponse getProductById(Long productId);
    PageResponse<?> getProductList(int page, int size, String sortBy, String direction);
    Product getProduct(Long productId);
    PageResponse<?> convertToPageResponse(Page<Product> productPage, Pageable pageable);
    //ProductResponse getProductsByName(String productName);
}
