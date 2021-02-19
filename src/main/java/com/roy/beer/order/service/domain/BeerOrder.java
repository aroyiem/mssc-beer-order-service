package com.roy.beer.order.service.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BeerOrder extends BaseEntity {

    private String customerRef;
    private BeerOrderStatusEnum orderStatus = BeerOrderStatusEnum.NEW;
    private String orderStatusCallbackUrl;

    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private Set<BeerOrderDetails> beerOrderDetails;

    @ManyToOne
    private Customer customer;

    @Builder
    public BeerOrder(UUID id, Long version,
                     Timestamp createdDate,
                     Timestamp lastModifiedDate,
                     String customerRef, BeerOrderStatusEnum orderStatus,
                     String orderStatusCallbackUrl,
                     Set<BeerOrderDetails> beerOrderDetails,
                     Customer customer) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerRef = customerRef;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
        this.beerOrderDetails = beerOrderDetails;
        this.customer = customer;
    }
}
