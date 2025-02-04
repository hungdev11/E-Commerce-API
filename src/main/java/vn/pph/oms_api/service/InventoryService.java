package vn.pph.oms_api.service;

import vn.pph.oms_api.dto.request.InventoryAddingRequest;
import vn.pph.oms_api.dto.response.InventoryAddingResponse;

public interface InventoryService {
    InventoryAddingResponse addInventory(InventoryAddingRequest request);
}
