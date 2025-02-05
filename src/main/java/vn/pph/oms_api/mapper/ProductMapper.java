package vn.pph.oms_api.mapper;

import org.mapstruct.Mapper;
import vn.pph.oms_api.dto.request.product.ProductCreationRequest;
import vn.pph.oms_api.dto.response.product.ProductResponse;
import vn.pph.oms_api.model.sku.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductCreationRequest request);
    ProductResponse toProductResponse(Product product);
}
