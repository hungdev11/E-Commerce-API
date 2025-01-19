package vn.pph.oms_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.pph.oms_api.dto.request.ProductCreationRequest;
import vn.pph.oms_api.dto.response.PageResponse;
import vn.pph.oms_api.dto.response.ProductCreationResponse;
import vn.pph.oms_api.dto.response.ProductResponse;
import vn.pph.oms_api.dto.response.SkuResponse;
import vn.pph.oms_api.model.sku.Product;

public interface ProductService {
      ProductCreationResponse addProduct(ProductCreationRequest product);
      ProductResponse getProductById(Long productId);
      /**
       * searchProduct(String keySearch)
       */
      PageResponse<?> convertToPageResponse(Page<Product> productPage, Pageable pageable);
      PageResponse<?> getAllProductsOfShop(Long shopId, int page, int size, String sortBy, String direction);
      PageResponse<?> productDraftList(Long shopId, int page, int size);
      PageResponse<?> productPublishList(Long shopId, int page, int size);
      PageResponse<?> listSkuByProductId(Long productId, int page, int size);
      boolean publishProduct(Long shopId, Long productId);
      boolean unPublishProduct(Long shopId, Long productId);
      SkuResponse skuDetails(Long productId, Long skuId);
      PageResponse<?> getProductListByDiscountCode(String discountCode, Long shopId, int page, int size);
}
