package com.roy.beer.order.service.services.beer;

import com.roy.brewery.model.BeerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient(name = "beer-service")
public interface BeerServiceFeignClient {

    String BEER_PATH_V1 = "/api/v1/beer/{beerId}";
    String BEER_UPC_PATH_V1 = "/api/v1/beerUpc/{upc}";

    @RequestMapping(method = RequestMethod.GET, value = BEER_PATH_V1)
    ResponseEntity<BeerDto> getBeerById(@PathVariable UUID beerId);

    @RequestMapping(method = RequestMethod.GET, value = BEER_UPC_PATH_V1)
    ResponseEntity<BeerDto> getBeerByUpc(@PathVariable String upc);
}
