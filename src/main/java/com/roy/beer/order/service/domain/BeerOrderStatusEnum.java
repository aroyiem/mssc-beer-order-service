package com.roy.beer.order.service.domain;

public enum BeerOrderStatusEnum {
    NEW,
    VALIDATION_PENDING,
    VALIDATED,
    VALIDATION_EXCEPTION,
    ALLOCATED,
    ALLOCATION_PENDING,
    ALLOCATION_EXCEPTION,
    PENDING_INVENTORY,
    PICKED_UP,
    DELIVERED,
    DELIVERY_EXCEPTION
}
