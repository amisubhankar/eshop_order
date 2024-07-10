package com.eshop.order.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@JsonSerialize
public class CartDeleteRequestDto implements Serializable {
    private List<Long> cartIds;
}
