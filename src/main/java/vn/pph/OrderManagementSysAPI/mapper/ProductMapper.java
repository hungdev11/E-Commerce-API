package vn.pph.OrderManagementSysAPI.mapper;

import org.mapstruct.Mapper;
import vn.pph.OrderManagementSysAPI.dto.request.ProductCreationRequest;
import vn.pph.OrderManagementSysAPI.dto.response.ProductResponse;
import vn.pph.OrderManagementSysAPI.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductCreationRequest request);
    ProductResponse toProductResponse(Product product);
}
