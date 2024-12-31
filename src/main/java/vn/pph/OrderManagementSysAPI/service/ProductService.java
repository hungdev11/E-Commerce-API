package vn.pph.OrderManagementSysAPI.service;

import vn.pph.OrderManagementSysAPI.request.ProductCreationRequest;

public interface ProductService {
    long addProduct(ProductCreationRequest product);
}
