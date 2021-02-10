package com.roy.beer.order.service.web.mappers;

import com.roy.beer.order.service.domain.BeerOrderDetails;
import com.roy.beer.order.service.web.model.BeerOrderDetailsDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface BeerOrderDetailsMapper {

    BeerOrderDetailsDto beerOrderDetailsToDto(BeerOrderDetails beerOrderDetails);

    BeerOrderDetails beerOrdeDetailsDtoToBeerOrder(BeerOrderDetailsDto dto);
}
