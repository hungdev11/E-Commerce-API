package vn.pph.oms_api.service;

import vn.pph.oms_api.dto.request.product.InventoryAddingRequest;
import vn.pph.oms_api.dto.response.product.InventoryAddingResponse;

public interface InventoryService {
    InventoryAddingResponse addInventory(InventoryAddingRequest request);
}
