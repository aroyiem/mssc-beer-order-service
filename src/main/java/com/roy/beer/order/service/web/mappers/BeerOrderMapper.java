package com.roy.beer.order.service.web.mappers;

import com.roy.beer.order.service.domain.BeerOrder;
import com.roy.beer.order.service.web.model.BeerOrderDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface BeerOrderMapper {

    BeerOrderDto beerOrderToBeerOrderDto(BeerOrder beerOrder);

    BeerOrder beerOrderDtoToBeerOrder(BeerOrderDto dto);
}
