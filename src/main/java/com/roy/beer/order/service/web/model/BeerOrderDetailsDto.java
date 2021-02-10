package com.roy.beer.order.service.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BeerOrderDetailsDto extends BaseItem {

    private String upc;
    private String beerName;
    private UUID beerId;
    private Integer orderQuantity = 0;
    private String beerStyle;
    private BigDecimal price;

    @Builder
    public BeerOrderDetailsDto(UUID id,
                               Integer version,
                               OffsetDateTime createdDate,
                               OffsetDateTime lastModifiedDate,
                               String upc, String beerName,
                               UUID beerId,
                               Integer orderQuantity,
                               String beerStyle,
                               BigDecimal price) {
        super(id, version, createdDate, lastModifiedDate);
        this.upc = upc;
        this.beerName = beerName;
        this.beerId = beerId;
        this.orderQuantity = orderQuantity;
        this.beerStyle = beerStyle;
        this.price = price;
    }
}
