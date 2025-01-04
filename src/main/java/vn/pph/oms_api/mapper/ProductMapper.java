package vn.pph.oms_api.mapper;

import org.mapstruct.Mapper;
import vn.pph.oms_api.dto.request.ProductCreationRequest;
import vn.pph.oms_api.dto.response.ProductResponse;
import vn.pph.oms_api.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductCreationRequest request);
    ProductResponse toProductResponse(Product product);
}
