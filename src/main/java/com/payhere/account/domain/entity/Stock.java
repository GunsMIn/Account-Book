package com.payhere.account.domain.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Table(name="stock")
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_code")
    private Long productCode;

    private Long quantity;

    @Version
    private Long version;

    /**/
    public void decrease(Long quantity) {
        if (this.quantity - quantity < 0) {
            throw new RuntimeException("재고 수랑은 0보다 작을 수 없습니다.");
        }
        this.quantity -= quantity;
    }

    public Stock(Long productCode, Long quantity) {
        this.productCode = productCode;
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }
}
