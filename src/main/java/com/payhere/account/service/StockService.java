package com.payhere.account.service;

import com.payhere.account.domain.entity.Stock;
import com.payhere.account.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public void decrease(Long id, Long quantity) {
        //get stock
        //재고 감소
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
    }
}
