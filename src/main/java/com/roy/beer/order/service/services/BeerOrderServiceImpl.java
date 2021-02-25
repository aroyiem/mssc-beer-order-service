package com.roy.beer.order.service.services;

import com.roy.beer.order.service.domain.BeerOrder;
import com.roy.beer.order.service.domain.BeerOrderStatusEnum;
import com.roy.beer.order.service.domain.Customer;
import com.roy.beer.order.service.repositories.BeerOrderRepository;
import com.roy.beer.order.service.repositories.CustomerRepository;
import com.roy.beer.order.service.web.mappers.BeerOrderMapper;
import com.roy.brewery.model.BeerOrderDto;
import com.roy.brewery.model.BeerOrderPageList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeerOrderServiceImpl implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final BeerOrderManager beerOrderManager;

    @Override
    public BeerOrderPageList listOrders(UUID customerId, Pageable pageable) {
        Customer customer = customerRepository.findById(customerId).orElse(null);

        Page<BeerOrder> beerOrderPage = beerOrderRepository.findAllByCustomer(customer, pageable);

        return new BeerOrderPageList(
                beerOrderPage.stream().map(beerOrder -> beerOrderMapper.beerOrderToBeerOrderDto(beerOrder))
                .collect(Collectors.toList()),
                PageRequest.of(beerOrderPage.getPageable().getPageNumber(), beerOrderPage.getPageable().getPageSize()),
                beerOrderPage.getTotalElements()
        );
    }

    @Override
    public BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(()-> new RuntimeException("Customer not found"));

        BeerOrder beerOrder = beerOrderMapper.beerOrderDtoToBeerOrder(beerOrderDto);
        beerOrder.setId(null); // should not be set by outside client
        beerOrder.setCustomer(customer);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);

        beerOrder.getBeerOrderDetails().forEach(beerOrderDetails -> beerOrderDetails.setBeerOrder(beerOrder));

        BeerOrder savedOrder = beerOrderManager.newBeerOrder(beerOrder);

        log.debug("Saved beer order: " + savedOrder.getId());

        return beerOrderMapper.beerOrderToBeerOrderDto(savedOrder);
    }

    @Override
    public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {
        return beerOrderMapper.beerOrderToBeerOrderDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickUpOrder(UUID customerId, UUID orderId) {
        beerOrderManager.beerOrderPickedUp(orderId);
    }

    private BeerOrder getOrder(UUID customerId, UUID orderId) {
        customerRepository.findById(customerId).orElseThrow(()-> new RuntimeException("Customer not found"));

        BeerOrder beerOrder = beerOrderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Beer Order Not Found"));

        if(beerOrder.getCustomer().getId().equals(customerId)) {
            return beerOrder;
        }
        throw new RuntimeException("Beer Order Not Found");
    }
}
