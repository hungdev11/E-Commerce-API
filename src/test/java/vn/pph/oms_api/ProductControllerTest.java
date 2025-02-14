package vn.pph.oms_api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import vn.pph.oms_api.utils.ProductUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {
    @Autowired
    MockMvc mockMvc;

//    @Test
//    void testAddNewProduct() throws Exception {
//        mockMvc.perform(post("/oms/products/")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"name\":\"Test Product\", \"price\":100.0}"))
//                .andExpect(status().isOk());
//    }

//    @Test
//    void testConvertNameToSlug() {
//        String name = "May cao rau cao cap A0193X";
//        String slug = ProductUtils.convertProductNameToSlug(name);
//        String expected = "may-cao-rau-cao-cap-a0193x";
//        Assertions.assertEquals(expected, slug);
//    }
}
