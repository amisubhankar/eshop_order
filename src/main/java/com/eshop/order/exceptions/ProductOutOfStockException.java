package com.eshop.order.exceptions;

public class ProductOutOfStockException extends Throwable {
    public ProductOutOfStockException(String s) {
        super(s);
    }
}
