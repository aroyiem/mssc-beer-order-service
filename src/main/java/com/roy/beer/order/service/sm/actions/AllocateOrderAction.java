package com.roy.beer.order.service.sm.actions;

import com.roy.beer.order.service.config.JMSConfig;
import com.roy.beer.order.service.domain.BeerOrder;
import com.roy.beer.order.service.domain.BeerOrderEventEnum;
import com.roy.beer.order.service.domain.BeerOrderStatusEnum;
import com.roy.beer.order.service.repositories.BeerOrderRepository;
import com.roy.beer.order.service.services.BeerOrderManagerImpl;
import com.roy.beer.order.service.web.mappers.BeerOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@RequiredArgsConstructor
@Slf4j
public class AllocateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final JmsTemplate jmsTemplate;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        String beerOrderId = (String) stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER);

        BeerOrder beerOrder = beerOrderRepository.findOneById(UUID.fromString(beerOrderId));

        jmsTemplate.convertAndSend(JMSConfig.ALLOCATE_ORDER_QUEUE, beerOrderMapper.beerOrderToBeerOrderDto(beerOrder));

        log.debug("Sent Allocation Request for order id: " + beerOrderId);
    }
}
