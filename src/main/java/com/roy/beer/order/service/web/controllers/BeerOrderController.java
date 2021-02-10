package com.roy.beer.order.service.web.controllers;

import com.roy.beer.order.service.services.BeerOrderService;
import com.roy.beer.order.service.web.model.BeerOrderDto;
import com.roy.beer.order.service.web.model.BeerOrderPageList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/{customerId}")
@RequiredArgsConstructor
public class BeerOrderController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    @GetMapping("/orders")
    public BeerOrderPageList listOrders(@PathVariable("customerId")UUID customerId,
                                        @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                        @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if(pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if(pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return beerOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public BeerOrderDto placeOrder(@PathVariable("customerId")UUID customerId, @RequestBody BeerOrderDto beerOrderDto) {
        return beerOrderService.placeOrder(customerId, beerOrderDto);
    }

    @GetMapping("/orders/{orderId}")
    public BeerOrderDto getOrder(@PathVariable("customerId")UUID customerId, @PathVariable("orderId") UUID orderId) {
        return beerOrderService.getOrderById(customerId, orderId);
    }

    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickUpOrder(@PathVariable("customerId")UUID customerId, @PathVariable("orderId") UUID orderId) {
        beerOrderService.pickUpOrder(customerId, orderId);
    }
}
