package com.eshop.order.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartResponseDto {
    private Long id;
    private Long userId;
    private Long productId;
    private int quantity;
    private float amount;
}
