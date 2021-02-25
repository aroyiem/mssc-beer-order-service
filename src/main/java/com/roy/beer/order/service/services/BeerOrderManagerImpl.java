package com.roy.beer.order.service.services;

import com.roy.beer.order.service.domain.BeerOrder;
import com.roy.beer.order.service.domain.BeerOrderEventEnum;
import com.roy.beer.order.service.domain.BeerOrderStatusEnum;
import com.roy.beer.order.service.repositories.BeerOrderRepository;
import com.roy.beer.order.service.sm.BeerOrderStateChangeInterceptor;
import com.roy.brewery.model.BeerOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeerOrderManagerImpl implements BeerOrderManager {

    public static final String ORDER_ID_HEADER = "ORDER_ID_HEADER";
    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineFactory;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderStateChangeInterceptor beerOrderStateChangeInterceptor;

    @Transactional
    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);

        BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);
        sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATE_ORDER);

        return savedBeerOrder;
    }

    @Override
    @Transactional
    public void processValidationResult(UUID orderId, Boolean isValid) {
        log.debug("Process Validation Result for beerOrderId: " + orderId + " Valid? " + isValid);


        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(orderId);

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            if(isValid) {
                sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_PASSED);

                // wait for the status change
                awaitForStatus(orderId, BeerOrderStatusEnum.VALIDATED);

                BeerOrder validatedOrder = beerOrderRepository.findById(orderId).get();

                sendBeerOrderEvent(validatedOrder, BeerOrderEventEnum.ALLOCATE_ORDER);
            } else {
                sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_FAILED);
            }
        }, ()-> log.error("Order Not found. Id: " + orderId));


    }

    @Override
    @Transactional
    public void beerOrderAllocationPassed(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_SUCCESS);
            // wait for the status change
            awaitForStatus(beerOrder.getId(), BeerOrderStatusEnum.ALLOCATED);
            updateAllocatedQuantity(beerOrderDto);
        }, ()-> log.error("Order Not found. Id: " + beerOrderDto.getId()));


    }

    @Override
    @Transactional
    public void beerOrderAllocationPendingInventory(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_NO_INVENTORY);
            // wait for the status change
            awaitForStatus(beerOrder.getId(), BeerOrderStatusEnum.PENDING_INVENTORY);
            updateAllocatedQuantity(beerOrderDto);
        }, ()-> log.error("Order Not found. Id: " + beerOrderDto.getId()));


    }

    @Override
    public void beerOrderAllocationFailed(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_FAILED);
        }, ()-> log.error("Order Not found. Id: " + beerOrderDto.getId()));

    }

    @Override
    public void beerOrderPickedUp(UUID id) {

        beerOrderRepository.findById(id).ifPresentOrElse(beerOrder -> {
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.BEERORDER_PICKED_UP);
        }, () -> log.error("Order Not found. Id: " + id));
    }

    @Override
    public void cancelOrder(UUID id) {

        beerOrderRepository.findById(id).ifPresentOrElse(beerOrder -> {
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.CANCEL_ORDER);
        }, () -> log.error("Order Not found. Id: " + id));
    }

    private void updateAllocatedQuantity(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());

        beerOrderOptional.ifPresentOrElse(allocateOrder -> {
            allocateOrder.getBeerOrderDetails().forEach(beerOrderDetails -> {
                beerOrderDto.getBeerOrderDetails().forEach(beerOrderDetailsDto -> {
                    if(beerOrderDetailsDto.getId().equals(beerOrderDetails.getId())) {
                        beerOrderDetails.setQuantityAllocated(beerOrderDetailsDto.getQuantityAllocated());
                    }
                });
            });

            beerOrderRepository.saveAndFlush(allocateOrder);
        }, () -> log.debug("Order Not Found, Id: " + beerOrderDto.getId()));

    }

    private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventEnum eventEnum) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = build(beerOrder);
        Message msg = MessageBuilder.withPayload(eventEnum)
                .setHeader(ORDER_ID_HEADER, beerOrder.getId().toString())
                .build();

        sm.sendEvent(msg);
    }

    private void awaitForStatus(UUID beerOrderId, BeerOrderStatusEnum beerOrderStatusEnum) {
        AtomicBoolean found = new AtomicBoolean(false);
        AtomicInteger loopCount = new AtomicInteger(0);

        while(!found.get()) {
            if(loopCount.incrementAndGet() > 10) {
                found.set(true);
                log.debug("Loop retries exceeded");
            }

            beerOrderRepository.findById(beerOrderId).ifPresentOrElse(beerOrder -> {
                if(beerOrder.getOrderStatus().equals(beerOrderStatusEnum)) {
                    found.set(true);
                    log.debug("Order found");
                } else {
                    log.debug("Order Status Not Equal. Expected: " + beerOrderStatusEnum.name() + " Found:" + beerOrder.getOrderStatus());
                }

                if(!found.get()) {
                    try {
                        log.debug("Sleeping for retry");
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
            }, () -> log.debug("Order Id not found"));
        }
    }

    private StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> build(BeerOrder beerOrder) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = stateMachineFactory.getStateMachine(beerOrder.getId());

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(beerOrderStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null));
                });
        sm.start();
        return sm;
    }
}
