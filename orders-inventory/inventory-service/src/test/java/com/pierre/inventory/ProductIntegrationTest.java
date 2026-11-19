package com.pierre.inventory;

import com.pierre.inventory.repository.ProductRepository;
import com.pierre.inventory.repository.StockDeductionRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;
    private Long createdProductId;

    @Autowired
    private StockDeductionRepository stockDeductionRepository;

    @Test
    void createProductAndRetrievesIt() throws Exception{
        ResultActions response = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Keyboard",
                            "price": 120.50,
                            "stock": 10
                        }
                        """
                ));

        MvcResult result = response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(header().exists("Location"))
                .andReturn();

        String location = trackCreatedProduct(result);

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(content().json(
                    result.getResponse().getContentAsString()
                ));
    }

    @Test
    void rejectBlankName() throws Exception{
         mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "name":" ",
                        "price": 120.5,
                        "stock": 10
                        }
                        """
                ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.errors")
                        .value(Matchers.hasItem(
                                "name: Product name must be not blank"
                        )));

    }

    @Test
    void rejectNullValues() throws Exception{
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Keyboard",
                            "price": null,
                            "stock": 10
                        }
                        """
                ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.errors")
                        .value(Matchers.hasItem(
                                "price: Product price must be provided"
                        )));

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "name": "Keyboard",
                            "price": 120.50,
                            "stock": null
                        }
                        """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.errors")
                        .value(Matchers.hasItem(
                                "stock: Product stock must be provided"
                        )));
    }

    @Test
    void rejectNegativeValues() throws Exception{
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "name": "Keyboard",
                            "price": -1,
                            "stock": 10
                        }
                        """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.errors")
                        .value(Matchers.hasItem(
                                "price: Product price must be zero or greater"
                        )));

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "name": "Keyboard",
                            "price": 120.50,
                            "stock": -2
                        }
                        """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.errors")
                        .value(Matchers.hasItem(
                                "stock: Product stock must be zero or greater"
                        )));
    }

    @Test
    void updatePrice() throws Exception{
        ResultActions response = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Keyboard",
                            "price": 120.50,
                            "stock": 10
                        }
                        """
                ));

        MvcResult result = response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(120.5))
                .andReturn();

        String location = trackCreatedProduct(result);

        mockMvc.perform(patch(location + "/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"price": 150.0 }
                        """))
                .andExpect(status().isOk());

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(150.0));
    }

    @Test
    void updateActive() throws Exception{
        ResultActions response = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Keyboard",
                            "price": 120.50,
                            "stock": 10
                        }
                        """
                ));

        MvcResult result = response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true))
                .andReturn();

        String location = trackCreatedProduct(result);

        mockMvc.perform(patch(location + "/active")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"active": false }
                        """))
                .andExpect(status().isOk());

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void deductStockAndCheckItsResult() throws Exception{
        ResultActions response = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Keyboard",
                            "price": 120.50,
                            "stock": 10
                        }
                        """
                ));

        MvcResult result = response
                .andExpect(status().isCreated())
                .andReturn();

        String location = trackCreatedProduct(result);

        mockMvc.perform(post(location + "/stock/deductions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "orderId": 1,
                            "quantity": 2
                        }
                        """
                ))
                .andExpect(status().isOk());

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(8));

        mockMvc.perform(post(location + "/stock/deductions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "orderId": 1,
                            "quantity": 2
                        }
                        """
                        ))
                .andExpect(status().isOk());

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(8));
    }

    @Test
    void rejectsDeductionWithDifferentQuantity() throws Exception{
        ResultActions response = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Keyboard",
                            "price": 120.50,
                            "stock": 10
                        }
                        """
                ));

        MvcResult result = response
                .andExpect(status().isCreated())
                .andReturn();

        String location = trackCreatedProduct(result);

        mockMvc.perform(post(location + "/stock/deductions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "orderId": 1,
                        "quantity": 2
                        }
                        """
                ))
                .andExpect(status().isOk());

        mockMvc.perform(post(location + "/stock/deductions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "orderId": 1,
                        "quantity": 3
                        }
                        """
                ))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("IDEMPOTENCY_KEY_MISMATCH"));

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(8));
    }

    @AfterEach
    void cleanUp(){
        if(createdProductId == null) return;

        stockDeductionRepository.findById(1L)
                .filter(deduction -> deduction.getProductId().equals(createdProductId))
                .ifPresent(stockDeductionRepository::delete);

        productRepository.deleteById(createdProductId);
    }

    private String trackCreatedProduct(MvcResult result) {
        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);
        createdProductId = Long.parseLong(
                location.substring(location.lastIndexOf('/') + 1)
        );
        return location;
    }
}
