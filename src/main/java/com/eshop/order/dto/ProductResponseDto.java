package com.eshop.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
public class ProductResponseDto{
    private Long id;
    private String name;
    private String description;
    private String image;
    private float price;
    private int availableQuantity;
    private Long categoryId;
}
