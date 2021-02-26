package com.roy.beer.order.service.services;

import com.roy.beer.order.service.domain.Customer;
import com.roy.beer.order.service.repositories.CustomerRepository;
import com.roy.beer.order.service.web.mappers.CustomerMapper;
import com.roy.brewery.model.CustomerPageList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerPageList listCustomers(PageRequest pageRequest) {
        Page<Customer> customerPage = customerRepository.findAll(pageRequest);

        return new CustomerPageList(
                customerPage.stream()
                .map(customerMapper::customerToDto)
                .collect(Collectors.toList()),
                PageRequest.of(customerPage.getPageable().getPageNumber(), customerPage.getPageable().getPageSize()),
                customerPage.getTotalElements()
        );
    }
}
