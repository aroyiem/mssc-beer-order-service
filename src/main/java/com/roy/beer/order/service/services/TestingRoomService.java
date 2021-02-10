package com.roy.beer.order.service.services;

import com.roy.beer.order.service.bootstrap.BeerOrderBootStrap;
import com.roy.beer.order.service.domain.Customer;
import com.roy.beer.order.service.repositories.CustomerRepository;
import com.roy.beer.order.service.web.model.BeerOrderDetailsDto;
import com.roy.beer.order.service.web.model.BeerOrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class TestingRoomService {

    private final CustomerRepository customerRepository;
    private final BeerOrderService beerOrderService;
    private final List<String> beerUpcs = new ArrayList<>(3);

    public TestingRoomService(CustomerRepository customerRepository, BeerOrderService beerOrderService) {
        this.customerRepository = customerRepository;
        this.beerOrderService = beerOrderService;

        beerUpcs.add(BeerOrderBootStrap.BEER_1_UPC);
        beerUpcs.add(BeerOrderBootStrap.BEER_2_UPC);
        beerUpcs.add(BeerOrderBootStrap.BEER_3_UPC);
    }

    @Transactional
    @Scheduled(fixedRate = 2000) //run every 2 seconds
    public void placeTastingRoomOrder() {

        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(BeerOrderBootStrap.TASTING_ROOM);

        if (customerList.size() == 1) { //should be just one
            doPlaceOrder(customerList.get(0));
        } else {
            log.error("Too many or too few tasting room customers found");
        }
    }

    private void doPlaceOrder(Customer customer) {
        String beerToOrder = getRandomBeerUpc();

        BeerOrderDetailsDto beerOrderLine = BeerOrderDetailsDto.builder()
                .upc(beerToOrder)
                .orderQuantity(new Random().nextInt(6)) //todo externalize value to property
                .build();

        List<BeerOrderDetailsDto> beerOrderDetails = new ArrayList<>();
        beerOrderDetails.add(beerOrderLine);

        BeerOrderDto beerOrder = BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef(UUID.randomUUID().toString())
                .beerOrderDetails(beerOrderDetails)
                .build();

        BeerOrderDto savedOrder = beerOrderService.placeOrder(customer.getId(), beerOrder);

    }

    private String getRandomBeerUpc() {
        return beerUpcs.get(new Random().nextInt(beerUpcs.size() - 0));
    }
}
