package com.roy.beer.order.service.repositories;

import com.roy.beer.order.service.domain.BeerOrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerOrderDetailsRepository extends JpaRepository<BeerOrderDetails, UUID> {
}
