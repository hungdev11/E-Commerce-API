package vn.pph.oms_api.service.Impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.pph.oms_api.dto.request.product.InventoryAddingRequest;
import vn.pph.oms_api.dto.response.product.InventoryAddingResponse;
import vn.pph.oms_api.exception.AppException;
import vn.pph.oms_api.exception.ErrorCode;
import vn.pph.oms_api.model.Inventory;
import vn.pph.oms_api.model.sku.Sku;
import vn.pph.oms_api.repository.InventoryRepository;
import vn.pph.oms_api.repository.ReservationItemRepository;
import vn.pph.oms_api.repository.SkuRepository;
import vn.pph.oms_api.service.InventoryService;
import vn.pph.oms_api.utils.ProductUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InventoryServiceImp implements InventoryService {
    ProductUtils productUtils;
    InventoryRepository inventoryRepository;
    ReservationItemRepository reservationItemRepository;
    SkuRepository skuRepository;

    @Transactional
    @Override
    public InventoryAddingResponse addInventory(InventoryAddingRequest request) {
        Long shopId = request.getShopId();
        Long productId = request.getProductId();
        String skuNumber = request.getSkuNumber();
        int stock = request.getStock();

        productUtils.checkProductSkuShop(productId, shopId, skuNumber);
        log.info("shop, product, sku are compatible");

        Sku sku = skuRepository.findBySkuNo(skuNumber).get();

        Optional<Inventory> inventoryOpt = sku.getInventories().stream()
                .filter(skuI -> skuI.getLocation().equals(request.getLocation()))
                .findFirst();

        Inventory inventory = inventoryOpt.orElseGet(() -> {
            log.info("Creating new inventory for SKU {} at location {}", skuNumber, request.getLocation());
            Inventory inventory1 = Inventory.builder()
                    .location(request.getLocation())
                    .stock(0) // Update later
                    .shopId(shopId)
                    .sku(sku)
                    .build();
            sku.getInventories().add(inventory1);
            return inventory1;
        });

        inventory.setStock(inventory.getStock() + stock);
        inventoryRepository.save(inventory);

        int total = 0;
        for (Inventory i : sku.getInventories()) {
            total += i.getStock();
        }
        sku.setSkuStock(total);
        skuRepository.save(sku);

        log.info("Saved inventory successfully: SKU {}, Location {}, Stock {}",
                skuNumber, inventory.getLocation(), inventory.getStock());

        return InventoryAddingResponse.builder()
                .location(inventory.getLocation())
                .skuNumber(inventory.getSku().getSkuNo())
                .stock(inventory.getStock())
                .build();
    }
}
