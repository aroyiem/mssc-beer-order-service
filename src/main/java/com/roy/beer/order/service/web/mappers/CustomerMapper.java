package com.roy.beer.order.service.web.mappers;

import com.roy.beer.order.service.domain.Customer;
import com.roy.brewery.model.CustomerDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface CustomerMapper {

    CustomerDto customerToDto(Customer customer);

    Customer dtoToCustomer(CustomerDto dto);
}
