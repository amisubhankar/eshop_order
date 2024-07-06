package com.eshop.order.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private int quantity;

    @ManyToOne
    private Order order;

    public OrderedProduct(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
