package com.roy.beer.order.service.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BeerOrderDetails extends BaseEntity {

    @ManyToOne
    private BeerOrder beerOrder;

    private UUID beerId;
    private String upc;
    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;

    @Builder
    public BeerOrderDetails(UUID id,
                            Long version,
                            Timestamp createdDate,
                            Timestamp lastModifiedDate,
                            BeerOrder beerOrder,
                            UUID beerId,
                            String upc,
                            Integer orderQuantity,
                            Integer quantityAllocated) {
        super(id, version, createdDate, lastModifiedDate);
        this.beerOrder = beerOrder;
        this.beerId = beerId;
        this.upc = upc;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }
}
