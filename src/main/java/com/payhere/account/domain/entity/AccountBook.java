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

import java.sql.Timestamp;
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
@Table(name="account_book")
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE account_book SET deleted_at = now() WHERE id = ?")
public class AccountBook extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_book_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String memo;
    //잔고
    @Column(nullable = false)
    private Integer balance;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

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
    /**지출시 잔고수정**/
    public void updateSpendMoney(Integer originMoney, Integer changedMoney) {
        this.balance += (originMoney - changedMoney);
    }

    public void updateSaveMoney(Integer originMoney, Integer changedMoney) {
        this.balance -= (originMoney - changedMoney);
    }

    /**가계부 기록 삭제 시 가계부 잔고 복원 메서드**/
    public void restoreBalance(Integer money, Act act) {
        if (act.equals(Act.SAVING)) {
            this.balance -= money;
        }else{
            this.balance += money;
        }
    }

    /**가계부 기록 삭제  복원 시 가계부 잔고 복원 메서드**/
    public void restoreBalanceResave(Integer money, Act act) {
        if (act.equals(Act.SAVING)) {
            this.balance += money;
        }else{
            this.balance -= money;
        }
    }




}
