package com.eshop.order.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity(name = "eshop_order")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private float amount;
    @Enumerated(EnumType.ORDINAL)
    private OrderStatus orderStatus;
    private Date bookedAt;
    private Long paymentId;
    private Long user_id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderedProduct> orderedProducts;
}
