package com.roy.beer.order.service.services.testcomponents;

import com.roy.beer.order.service.config.JMSConfig;
import com.roy.brewery.model.events.AllocateOrderRequest;
import com.roy.brewery.model.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderAllocationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JMSConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(Message message) {

        AllocateOrderRequest allocateOrderRequest = (AllocateOrderRequest) message.getPayload();

        allocateOrderRequest.getBeerOrderDto().getBeerOrderDetails().forEach( beerOrderDetailsDto -> {
            beerOrderDetailsDto.setQuantityAllocated(beerOrderDetailsDto.getOrderQuantity());
        });

        jmsTemplate.convertAndSend(JMSConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,
                AllocateOrderResult.builder()
        .beerOrderDto(allocateOrderRequest.getBeerOrderDto())
        .allocationError(false)
        .pendingInventory(false).build());
    }
}
