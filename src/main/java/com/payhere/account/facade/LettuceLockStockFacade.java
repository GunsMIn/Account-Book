package com.payhere.account.facade;

import com.payhere.account.repository.redis.RedisLockRepository;
import com.payhere.account.service.StockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade {

    private RedisLockRepository redisLockRepository;

    private StockService stockService;

    public LettuceLockStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
        this.redisLockRepository = redisLockRepository;
        this.stockService = stockService;
    }

    public void decrease(Long key, Long quantity) throws InterruptedException {
        /**
         * spin lock 방식 이므로 재시도 로직을 직접 짜야 한다.
           spin lock이란 쓰레드가 lock을 사용할 수 있는지 반복적으로 확인하면서 시도하는 방식을 말한다.
         * */
        //lock 획득 실패 시
        while (!redisLockRepository.lock(key)) {
            //redis에 갈 수 있는 부하를 줄여준다.
            Thread.sleep(100);
        }

        //1.재고를 줄여 준 후
        //2.lock 해제
        try {
            stockService.decrease(key, quantity);
        } finally {
            redisLockRepository.unlock(key);
        }
    }
}
