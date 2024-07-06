package com.eshop.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDto {
    private Long user_id;
    private List<Long> cartIds;
}
