package vn.pph.OrderManagementSysAPI.controller;

import vn.pph.OrderManagementSysAPI.request.ProductCreationRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.pph.OrderManagementSysAPI.service.ProductService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductController {
    ProductService productService;
    @PostMapping("/")
    public long addNewProduct(@RequestBody ProductCreationRequest request) {
        log.info("Controller: add product");
        return productService.addProduct(request);
    }
}
