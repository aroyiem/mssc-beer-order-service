package com.roy.beer.order.service.services;

import com.roy.beer.order.service.web.model.BeerOrderDto;
import com.roy.beer.order.service.web.model.BeerOrderPageList;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BeerOrderService {

    BeerOrderPageList listOrders(UUID customerId, Pageable pageable);

    BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto);

    BeerOrderDto getOrderById(UUID customerId, UUID orderId);

    void pickUpOrder(UUID customerId, UUID orderId);
}
