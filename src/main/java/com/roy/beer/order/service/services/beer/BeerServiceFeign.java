package com.roy.beer.order.service.services.beer;

import com.roy.brewery.model.BeerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Profile("local-discovery")
@Slf4j
@Service
@RequiredArgsConstructor
public class BeerServiceFeign implements BeerService {

    private final BeerServiceFeignClient beerServiceFeignClient;

    @Override
    public Optional<BeerDto> getBeerById(UUID beerId) {
        ResponseEntity<BeerDto> response = beerServiceFeignClient.getBeerById(beerId);
        return Optional.of(response.getBody());
    }

    @Override
    public Optional<BeerDto> getBeerByUpc(String upc) {
        ResponseEntity<BeerDto> response = beerServiceFeignClient.getBeerByUpc(upc);
        return Optional.of(response.getBody());
    }
}
