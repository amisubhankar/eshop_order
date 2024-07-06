package com.eshop.order.controllerAdvice;

import com.eshop.order.exceptions.ProductOutOfStockException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AnyControllerAdvice {
    @ExceptionHandler(ProductOutOfStockException.class)
    public ResponseEntity<String> handleProductOutOfStock(ProductOutOfStockException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
