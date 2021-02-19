package com.roy.beer.order.service.services.listeners;

import com.roy.beer.order.service.config.JMSConfig;
import com.roy.beer.order.service.repositories.BeerOrderRepository;
import com.roy.beer.order.service.services.BeerOrderManager;
import com.roy.brewery.model.events.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidationResultListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JMSConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listen(ValidateOrderResult validateOrderResult) {
        final UUID orderId = validateOrderResult.getOrderId();

        log.debug("Validation result for Order Id: " + orderId);

        beerOrderManager.processValidationResult(orderId, validateOrderResult.getIsValid());
    }
}
