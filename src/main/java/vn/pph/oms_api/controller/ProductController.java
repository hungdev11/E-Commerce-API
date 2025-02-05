package vn.pph.oms_api.controller;

import vn.pph.oms_api.dto.request.product.ProductCreationRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.pph.oms_api.dto.response.ApiResponse;
import vn.pph.oms_api.dto.response.PageResponse;
import vn.pph.oms_api.dto.response.product.ProductResponse;
import vn.pph.oms_api.service.ProductService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductController {
    ProductService productService;

    @PostMapping("/")
    public ApiResponse<?> addNewProduct(@RequestBody ProductCreationRequest request) {
        log.info("Controller: add product");
        return ApiResponse.builder()
                .code(201)
                .message("Add product successfully")
                .data(productService.addProduct(request))
                .build();
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long productId) {
        log.info("Controller: get product with id {}", productId);
        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .message("Get product successfully")
                .data(productService.getProductById(productId))
                .build();
    }

    @GetMapping("/")
    public PageResponse<?> getProductList(
            @RequestParam Long shopId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5")int size,
            @RequestParam(defaultValue = "updateTime")String sortBy,
            @RequestParam(defaultValue = "asc")String direction) {
        log.info("Controller: get product list with size {}, page {}, by {}, direction {}", size, page, sortBy, direction);
        return productService.getAllProductsOfShop(shopId, page, size, sortBy, direction);
    }

    // Lấy danh sách sản phẩm Draft
    @GetMapping("/drafts")
    public PageResponse<?> getDraftProducts(
            @RequestParam Long shopId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        log.info("Controller: get draft products for shop {}", shopId);
        return productService.productDraftList(shopId, page, size);
    }

    // Lấy danh sách sản phẩm Publish
    @GetMapping("/published")
    public PageResponse<?> getPublishedProducts(
            @RequestParam Long shopId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        log.info("Controller: get published products for shop {}", shopId);
        return productService.productPublishList(shopId, page, size);
    }

    // Chuyển sản phẩm sang trạng thái Publish
    @PatchMapping("/publish/{productId}")
    public ApiResponse<?> publishProduct(
            @RequestParam Long shopId,
            @PathVariable Long productId) {
        log.info("Controller: publish product {} for shop {}", productId, shopId);
        boolean result = productService.publishProduct(shopId, productId);
        return ApiResponse.builder()
                .data(result)
                .code(200)
                .build();
    }

    // Chuyển sản phẩm sang trạng thái Draft
    @PatchMapping("unpublish/{productId}")
    public ApiResponse<?> unPublishProduct(
            @RequestParam Long shopId,
            @PathVariable Long productId) {
        log.info("Controller: unpublish product {} for shop {}", productId, shopId);
        boolean result = productService.unPublishProduct(shopId, productId);
        return ApiResponse.builder()
                .data(result)
                .code(200)
                .build();
    }

    @GetMapping("/sku-list")
    public PageResponse<?> getSkusOfProduct(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        log.info("Controller: get sku list by product id {}", productId);
        return productService.listSkuByProductId(productId, page, size);
    }

    @GetMapping("sku-details")
    public ApiResponse<?> skuDetailsByProductId(@RequestParam Long productId, @RequestParam Long skuId) {
        log.info("Controller: get sku details with product id {}, sku id {}", productId, skuId);
        return ApiResponse.builder()
                .message("Get sku details successfully")
                .data(productService.skuDetails(productId, skuId))
                .code(200)
                .build();
    }

    @GetMapping("products-discount-with-code")
    public ApiResponse<?> getProductsByDiscountCode(
            @RequestParam String discountCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        log.info("Controller: get products is apply by code {}", discountCode);
        return ApiResponse.builder()
                .message("Get product successfully")
                .data(productService.getProductListByDiscountCode(discountCode, page, size))
                .code(200)
                .build();
    }
}
