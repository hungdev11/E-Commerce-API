package vn.pph.oms_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("E-commerce API"));
    }

    @Bean
    public GroupedOpenApi productApi() {
        return GroupedOpenApi.builder()
                .group("Product API") // Tên nhóm API trong Swagger UI
                .packagesToScan("vn.pph.oms_api.controller") // Quét toàn bộ controller trong package này
                .pathsToMatch("/products/**") // Chỉ lấy API có đường dẫn bắt đầu với /products
                .build();
    }
    @Bean
    public GroupedOpenApi authenticationApi() {
        return GroupedOpenApi.builder()
                .group("Authentication API") // Tên nhóm API trong Swagger UI
                .packagesToScan("vn.pph.oms_api.controller") // Quét toàn bộ controller trong package này
                .pathsToMatch("/authentication/**") // Chỉ lấy API có đường dẫn bắt đầu với /products
                .build();
    }
    @Bean
    public GroupedOpenApi cartApi() {
        return GroupedOpenApi.builder()
                .group("Cart API") // Tên nhóm API trong Swagger UI
                .packagesToScan("vn.pph.oms_api.controller") // Quét toàn bộ controller trong package này
                .pathsToMatch("/cart/**") // Chỉ lấy API có đường dẫn bắt đầu với /products
                .build();
    }
    @Bean
    public GroupedOpenApi attributeApi() {
        return GroupedOpenApi.builder()
                .group("Attribute API") // Tên nhóm API trong Swagger UI
                .packagesToScan("vn.pph.oms_api.controller") // Quét toàn bộ controller trong package này
                .pathsToMatch("/attribute/**") // Chỉ lấy API có đường dẫn bắt đầu với /products
                .build();
    }
    @Bean
    public GroupedOpenApi discountApi() {
        return GroupedOpenApi.builder()
                .group("Discount API") // Tên nhóm API trong Swagger UI
                .packagesToScan("vn.pph.oms_api.controller") // Quét toàn bộ controller trong package này
                .pathsToMatch("/discounts/**") // Chỉ lấy API có đường dẫn bắt đầu với /products
                .build();
    }
    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("Order API") // Tên nhóm API trong Swagger UI
                .packagesToScan("vn.pph.oms_api.controller") // Quét toàn bộ controller trong package này
                .pathsToMatch("/order/**") // Chỉ lấy API có đường dẫn bắt đầu với /products
                .build();
    }
    @Bean
    public GroupedOpenApi inventoryApi() {
        return GroupedOpenApi.builder()
                .group("Inventory API") // Tên nhóm API trong Swagger UI
                .packagesToScan("vn.pph.oms_api.controller") // Quét toàn bộ controller trong package này
                .pathsToMatch("/inventory/**") // Chỉ lấy API có đường dẫn bắt đầu với /products
                .build();
    }

}
