package com.pierre.orders;

import com.pierre.orders.client.InventoryClient;
import com.pierre.orders.entity.Order;
import com.pierre.orders.enums.OrderStatus;
import com.pierre.orders.repository.OrderRepository;
import com.pierre.orders.retry.OrderRetryScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.ResourceAccessException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryClient inventoryClient;

    private Long createdOrderId;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderRetryScheduler orderRetryScheduler;

    @Test
    void createOrderAndRetrievesIt() throws Exception{
        MvcResult result = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "productId": 1,
                        "quantity": 2
                        }
                        """
                ))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.status").value("READY"))
                .andExpect(header().exists("Location"))
                .andReturn();

        String location = trackCreatedOrder(result);

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"));

        verify(inventoryClient).deductStock(createdOrderId, 1L, 2);
    }

    @Test
    void pendingOrderBecomesReadyWhenInventoryRecovers() throws Exception{
        doThrow(new ResourceAccessException("Inventory unavailable"))
                .when(inventoryClient)
                .deductStock(anyLong(), eq(1L), eq(2));

        MvcResult result = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                        "productId": 1,
                        "quantity": 2
                        }
                        """
                        ))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(header().exists("Location"))
                .andReturn();

        String location = trackCreatedOrder(result);

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        doNothing().when(inventoryClient).deductStock(anyLong(), eq(1L), eq(2));

        mockMvc.perform(post(location + "/retry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"));

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"));
    }

    @Test
    void schedulerRetriesPendingOrderWhenInventoryRecovers() throws Exception{
        doThrow(new ResourceAccessException("Inventory unavailable"))
                .when(inventoryClient)
                .deductStock(anyLong(), eq(1L), eq(2));

        MvcResult result = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                        "productId": 1,
                        "quantity": 2
                        }
                        """
                        ))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(header().exists("Location"))
                .andReturn();

        String location = trackCreatedOrder(result);

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        Order order = orderRepository.findById(createdOrderId).orElseThrow();

        order.setNextRetryAt(Instant.now().minusSeconds(1));
        orderRepository.saveAndFlush(order);

        doNothing().when(inventoryClient).deductStock(anyLong(), eq(1L), eq(2));

        orderRetryScheduler.retryPendingOrders();

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"));
    }

    @Test
    void schedulerSkipsOrdersWhoseRetryTimeIsInTheFuture() throws Exception{
        doThrow(new ResourceAccessException("Inventory unavailable"))
                .when(inventoryClient)
                .deductStock(anyLong(), eq(1L), eq(2));

        MvcResult result = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                        "productId": 1,
                        "quantity": 2
                        }
                        """
                        ))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(header().exists("Location"))
                .andReturn();

        String location = trackCreatedOrder(result);

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        Order order = orderRepository.findById(createdOrderId).orElseThrow();
        order.setNextRetryAt(Instant.now().plusSeconds(3600));
        orderRepository.saveAndFlush(order);

        doNothing().when(inventoryClient).deductStock(anyLong(), eq(1L), eq(2));

        orderRetryScheduler.retryPendingOrders();

        verify(inventoryClient, times(1))
                .deductStock(createdOrderId, 1L, 2);

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void schedulerSkipsOrdersNeedingReview() throws Exception{
        doThrow(new ResourceAccessException("Inventory unavailable"))
                .when(inventoryClient)
                .deductStock(anyLong(), eq(1L), eq(2));

        MvcResult result = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                        "productId": 1,
                        "quantity": 2
                        }
                        """
                        ))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(header().exists("Location"))
                .andReturn();

        String location = trackCreatedOrder(result);

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        Order order = orderRepository.findById(createdOrderId).orElseThrow();
        order.changeStatus(OrderStatus.NEEDS_REVIEW);
        order.setNextRetryAt(Instant.now().minusSeconds(1));
        orderRepository.saveAndFlush(order);

        doNothing().when(inventoryClient).deductStock(anyLong(), eq(1L), eq(2));

        orderRetryScheduler.retryPendingOrders();

        verify(inventoryClient, times(1))
                .deductStock(createdOrderId, 1L, 2);

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NEEDS_REVIEW"));
    }

    @AfterEach
    void cleanUp(){
        if(createdOrderId == null){
            return;
        }
        orderRepository.deleteById(createdOrderId);
    }

    private String trackCreatedOrder(MvcResult result) {
        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);
        createdOrderId = Long.parseLong(
                location.substring(location.lastIndexOf('/') + 1)
        );
        return location;
    }
}
