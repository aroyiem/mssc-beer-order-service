package com.roy.beer.order.service.web.mappers;

import com.roy.beer.order.service.domain.BeerOrderDetails;
import com.roy.beer.order.service.services.beer.BeerService;
import com.roy.brewery.model.BeerDto;
import com.roy.brewery.model.BeerOrderDetailsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;

public abstract class BeerOrderDetailsMapperDecorator implements BeerOrderDetailsMapper {

    private BeerService beerService;
    private BeerOrderDetailsMapper beerOrderDetailsMapper;

    @Autowired
    public void setBeerService(BeerService beerService) {
        this.beerService = beerService;
    }

    @Autowired
    @Qualifier("delegate")
    public void setBeerOrderDetailsMapper(BeerOrderDetailsMapper beerOrderDetailsMapper) {
        this.beerOrderDetailsMapper = beerOrderDetailsMapper;
    }

    @Override
    public BeerOrderDetailsDto beerOrderDetailsToDto(BeerOrderDetails beerOrderDetails) {
        BeerOrderDetailsDto beerOrderDetailsDto = beerOrderDetailsMapper.beerOrderDetailsToDto(beerOrderDetails);
        Optional<BeerDto> beerDtoOptional = beerService.getBeerByUpc(beerOrderDetails.getUpc());

        beerDtoOptional.ifPresent(beerDto -> {
            beerOrderDetailsDto.setBeerName(beerDto.getBeerName());
            beerOrderDetailsDto.setBeerStyle(beerDto.getBeerStyle());
            beerOrderDetailsDto.setPrice(beerDto.getPrice());
            beerOrderDetailsDto.setBeerId(beerDto.getId());
        });

        return beerOrderDetailsDto;
    }
}
