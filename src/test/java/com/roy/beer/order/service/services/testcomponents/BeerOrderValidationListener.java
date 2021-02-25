package com.roy.beer.order.service.services.testcomponents;

import com.roy.beer.order.service.config.JMSConfig;
import com.roy.brewery.model.events.ValidateOrderRequest;
import com.roy.brewery.model.events.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderValidationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JMSConfig.VALIDATE_ORDER_QUEUE)
    public void list(Message msg) {

        ValidateOrderRequest request = (ValidateOrderRequest) msg.getPayload();

        jmsTemplate.convertAndSend(JMSConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                ValidateOrderResult.builder()
                        .isValid(true).orderId(request.getBeerOrderDto().getId())
                        .build());
    }
}