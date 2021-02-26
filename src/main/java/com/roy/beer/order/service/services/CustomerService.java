package com.roy.beer.order.service.services;

import com.roy.brewery.model.CustomerPageList;
import org.springframework.data.domain.PageRequest;

public interface CustomerService {
    CustomerPageList listCustomers(PageRequest pageRequest);
}
