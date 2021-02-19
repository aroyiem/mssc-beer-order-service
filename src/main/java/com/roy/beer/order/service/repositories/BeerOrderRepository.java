package com.roy.beer.order.service.repositories;

import com.roy.beer.order.service.domain.BeerOrder;
import com.roy.beer.order.service.domain.Customer;
import com.roy.beer.order.service.domain.BeerOrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {

    List<BeerOrder> findAllByOrderStatus(BeerOrderStatusEnum beerOrderStatusEnum);

    Page<BeerOrder> findAllByCustomer(Customer customer, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    BeerOrder findOneById(UUID id);
}
