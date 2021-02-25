package com.roy.beer.order.service.bootstrap;

import com.roy.beer.order.service.repositories.CustomerRepository;
import com.roy.beer.order.service.domain.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class BeerOrderBootStrap implements CommandLineRunner {

    public static final String TASTING_ROOM = "Tasting Room";
    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        loadCustomerData();
    }

    @Transactional
    void loadCustomerData() {
        if(customerRepository.findAllByCustomerNameLike(TASTING_ROOM).size() == 0) {
            Customer savedCustomer = customerRepository.save(Customer.builder()
                    .customerName(TASTING_ROOM).apiKey(UUID.randomUUID()).build());
            log.debug("Tasting Room Customer Id: " + savedCustomer.getId().toString());
        }
    }
}
