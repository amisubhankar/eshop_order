package com.eshop.order.controller;

import com.eshop.order.dto.OrderRequestDto;
import com.eshop.order.dto.OrderResponseDto;
import com.eshop.order.exceptions.ProductOutOfStockException;
import com.eshop.order.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto)
            throws ProductOutOfStockException {

        return ResponseEntity.ok().body(orderService.createOrder(orderRequestDto));

    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderDetailsById(@PathVariable Long id){
        return ResponseEntity.ok().body(orderService.getOrderDetailsById(id));

    }
}
