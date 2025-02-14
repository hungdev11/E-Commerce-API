package vn.pph.oms_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import vn.pph.oms_api.dto.request.product.ProductCreationRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.pph.oms_api.dto.response.APIResponse;
import vn.pph.oms_api.dto.response.PageResponse;
import vn.pph.oms_api.dto.response.product.ProductCreationResponse;
import vn.pph.oms_api.dto.response.product.ProductResponse;
import vn.pph.oms_api.service.ProductService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "Product API", description = "API for manage product")
public class ProductController {
    ProductService productService;

    @Operation(method = "POST", summary = "Adding new product", description = "API create new product")
    @ApiResponse(responseCode = "201", description = "Add new product successfully",
            content = @Content(schema = @Schema(implementation = ProductCreationResponse.class)
    ))
    @PostMapping("/")
    public APIResponse<?> addNewProduct(@RequestBody ProductCreationRequest request) {
        log.info("Controller: add product");
        return APIResponse.builder()
                .code(201)
                .message("Add product successfully")
                .data(productService.addProduct(request))
                .build();
    }

    @GetMapping("/{productId}")
    public APIResponse<ProductResponse> getProductById(@PathVariable Long productId) {
        log.info("Controller: get product with id {}", productId);
        return APIResponse.<ProductResponse>builder()
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
    public APIResponse<?> publishProduct(
            @RequestParam Long shopId,
            @PathVariable Long productId) {
        log.info("Controller: publish product {} for shop {}", productId, shopId);
        boolean result = productService.publishProduct(shopId, productId);
        return APIResponse.builder()
                .data(result)
                .code(200)
                .build();
    }

    // Chuyển sản phẩm sang trạng thái Draft
    @PatchMapping("unpublish/{productId}")
    public APIResponse<?> unPublishProduct(
            @RequestParam Long shopId,
            @PathVariable Long productId) {
        log.info("Controller: unpublish product {} for shop {}", productId, shopId);
        boolean result = productService.unPublishProduct(shopId, productId);
        return APIResponse.builder()
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
    public APIResponse<?> skuDetailsByProductId(@RequestParam Long productId, @RequestParam Long skuId) {
        log.info("Controller: get sku details with product id {}, sku id {}", productId, skuId);
        return APIResponse.builder()
                .message("Get sku details successfully")
                .data(productService.skuDetails(productId, skuId))
                .code(200)
                .build();
    }

    @GetMapping("products-discount-with-code")
    public APIResponse<?> getProductsByDiscountCode(
            @RequestParam String discountCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        log.info("Controller: get products is apply by code {}", discountCode);
        return APIResponse.builder()
                .message("Get product successfully")
                .data(productService.getProductListByDiscountCode(discountCode, page, size))
                .code(200)
                .build();
    }
}
