package com.ignacio.SpringEcom.model.dto;

public record OrderItemRequest(
        int productId,
        int quantity
) {}
