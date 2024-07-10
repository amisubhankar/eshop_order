package com.eshop.order.services;

import com.eshop.order.dto.*;
import com.eshop.order.exceptions.ProductOutOfStockException;
import com.eshop.order.models.Order;
import com.eshop.order.models.OrderStatus;
import com.eshop.order.models.OrderedProduct;
import com.eshop.order.repositories.OrderRepository;
import com.eshop.order.util.OrderUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OrderService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ObjectMapper objectMapper;

    float totalCartAmount = 0;

    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto, String header) throws ProductOutOfStockException {
        List<OrderedProduct> orderedProductList = createListForOrderedProducts(orderRequestDto);

        //3. Create Order
        Order order = new Order();
        order.setAmount(totalCartAmount);
        order.setOrderedProducts(orderedProductList);
        order.setBookedAt(new Date());
        order.setUser_id(OrderUtil.getUserIdFromToken(header));
        order.setOrderStatus(OrderStatus.ORDER_INITIATED);

        //setting order obj to ordered products
        for (OrderedProduct orderedProduct : orderedProductList) {
            orderedProduct.setOrder(order);
        }


        //4. Save in the repository
        Order createdOrder = orderRepository.save(order);

        //5. Delete Cart details
        //make the object
        CartDeleteRequestDto obj = new CartDeleteRequestDto();
        obj.setCartIds(orderRequestDto.getCartIds());
        String cartIdsToDelete;

        try {
            cartIdsToDelete = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // send it!
        restTemplate.exchange("http://cart/cart/delete/afterorder?cartIds={cartIds}",
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class,
                                cartIdsToDelete);


        return mapOrderToOrderResponseDto(createdOrder);
    }

    private OrderResponseDto mapOrderToOrderResponseDto(Order createdOrder) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setOrderId(createdOrder.getId());
        orderResponseDto.setAmount(createdOrder.getAmount());
        orderResponseDto.setOrderStatus(createdOrder.getOrderStatus());

        return orderResponseDto;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    private List<OrderedProduct> createListForOrderedProducts(OrderRequestDto orderRequestDto) throws ProductOutOfStockException {

        //1. Check All product availability
        List<CartResponseDto> cartDetailsList = new ArrayList<>();
        Map<Long, Integer> productQuantityMap = new HashMap<>();

        for (Long cartId : orderRequestDto.getCartIds()) {
            CartResponseDto cartDetails = restTemplate.getForObject("http://cart/cart/" + cartId, CartResponseDto.class);
            cartDetailsList.add(cartDetails);
            //getting product_details
            ProductResponseDto productDetails = restTemplate.getForObject("http://products/products/" +
                                                                    cartDetails.getProductId(), ProductResponseDto.class);


            if(cartDetails.getQuantity() > productDetails.getAvailableQuantity()){
                throw new ProductOutOfStockException(productDetails.getName() + " is out of stock !!");
            }

            totalCartAmount += (productDetails.getPrice() * cartDetails.getQuantity());
            productQuantityMap.put(cartDetails.getProductId(), productDetails.getAvailableQuantity());
        }

        //2. All available so -> Make OrderedProduct List and Update Product table (decr qty)
        List<OrderedProduct> orderedProductList = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        for (CartResponseDto cartDetails : cartDetailsList) {
            restTemplate.exchange("http://products/products/update/quantity?prodId={prodId}&quantity={quantity}",
                    HttpMethod.PUT,
                    requestEntity,
                    String.class,
                    cartDetails.getProductId(),
                    productQuantityMap.get(cartDetails.getProductId()) - cartDetails.getQuantity());


            orderedProductList.add(new OrderedProduct(cartDetails.getProductId(), cartDetails.getQuantity()));
        }

        return orderedProductList;
    }

    public OrderResponseDto getOrderDetailsById(Long id) {
        //skipping orderId validation
        Optional<Order> optionalOrder = orderRepository.findById(id);

        return mapOrderToOrderResponseDto(optionalOrder.get());
    }

    public OrderResponseDto updateOrderStatusById(Long id, OrderStatus status) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        //skipping orderId validation
        Order order = optionalOrder.get();
        order.setOrderStatus(status);

        Order updatedOrder = orderRepository.save(order);

        return mapOrderToOrderResponseDto(updatedOrder);
    }

    public String updatePaymentIdByOrderId(Long id, UUID paymentId) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        //skipping orderId validation
        Order order = optionalOrder.get();
        order.setPaymentId(paymentId);

        orderRepository.save(order);

        return "Payment id updated successfully";
    }
}
