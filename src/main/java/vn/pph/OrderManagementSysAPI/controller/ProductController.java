package vn.pph.OrderManagementSysAPI.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vn.pph.OrderManagementSysAPI.dto.request.ProductCreationRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.pph.OrderManagementSysAPI.dto.response.ApiResponse;
import vn.pph.OrderManagementSysAPI.dto.response.PageResponse;
import vn.pph.OrderManagementSysAPI.dto.response.ProductResponse;
import vn.pph.OrderManagementSysAPI.service.ProductService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductController {
    ProductService productService;
    @PostMapping("/")
    public ApiResponse<Long> addNewProduct(@RequestBody ProductCreationRequest request) {
        log.info("Controller: add product");
        return ApiResponse.<Long>builder()
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5")int size,
            @RequestParam(defaultValue = "id")String sortBy,
            @RequestParam(defaultValue = "asc")String direction) {
        log.info("Controller: get product list with size {}, page {}, by {}, direction {}", size, page, sortBy, direction);
        return productService.getProductList(page, size, sortBy, direction);
    }
}
