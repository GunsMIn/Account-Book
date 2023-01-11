package com.payhere.account.domain.entity;

import com.payhere.account.domain.dto.record.RecordUpdateDto;
import com.payhere.account.domain.entity.type.Act;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.*;
@Slf4j
@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE AccountBook SET deleted_at = now() WHERE id = ?")
public class AccountBook extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String memo;
    //잔고
    private Integer balance;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "accountBook",cascade = REMOVE, orphanRemoval = true)
    private List<Record> records = new ArrayList<>();


    /**dirty check 수정 메서드(변경감지) **/
    public AccountBook update(String title, String memo,Integer balance) {
        this.title = title;
        this.memo = memo;
        this.balance = balance;
        return this;
    }

    /**잔고 지출 저축 비지니스 메서드**/
    public void addMoney(Integer money) {
        this.balance += money;
    }

    public void minusMoney(Integer money) {
        this.balance -= money;
    }

    /**가계부 기록 수정 시 잔고 변경 비지니스 메서드**/
    public void updateSpendMoney(Integer originMoney, Integer changedMoney) {
        log.info("원래 ㅣ{}" , originMoney);
        log.info("바뀐금액 ㅣ{}", changedMoney);
        // 10000        5000   = +5000
        this.balance += (originMoney - changedMoney);
        log.info("현재 balance :{}",balance);
    }

    public void updateSaveMoney(Integer originMoney, Integer changedMoney) {
        log.info("원래 ㅣ{}" , originMoney);
        log.info("바뀐금액 ㅣ{}", changedMoney);
        // 100000        10000         5000   -> 110000 / 10500
        this.balance -= (originMoney - changedMoney);
        log.info("현재 balance :{}",balance);
    }

    /**가계부 기록 삭제 시 가계부 잔고 복원 메서드**/
    public void restore(Integer money, Act act) {
        log.info("돈 :{}",money);
        if (act.equals(Act.SAVING)) {
            this.balance -= money;
        }else{
            this.balance += money;
        }
    }


}
