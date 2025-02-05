package vn.pph.oms_api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.pph.oms_api.dto.request.product.InventoryAddingRequest;
import vn.pph.oms_api.dto.response.ApiResponse;
import vn.pph.oms_api.service.InventoryService;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryController {
    InventoryService inventoryService;

    @PostMapping("/add")
    public ApiResponse<?> addNewInventory(@RequestBody InventoryAddingRequest request) {
        log.info("Controller: add inventory sku {} location {} stock {}", request.getSkuNumber(), request.getLocation(), request.getStock());
        return ApiResponse.builder()
                .data(inventoryService.addInventory(request))
                .code(201)
                .message("Add new inventory successfully")
                .build();
    }
}
