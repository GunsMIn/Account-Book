package com.payhere.account.domain.entity;

import com.payhere.account.domain.dto.record.RecordDto;
import com.payhere.account.domain.dto.record.RecordUpdateDto;
import com.payhere.account.domain.entity.type.Act;
import com.payhere.account.domain.entity.type.Day;
import com.payhere.account.domain.entity.type.ExpendType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.naming.ldap.ExtendedRequest;
import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
@ToString
@Slf4j
@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE Record SET deleted_at = now() WHERE id = ?")
public class Record extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String memo;

    private Integer money;

    @Enumerated(EnumType.STRING)
    private ExpendType expendType;

    @Enumerated(EnumType.STRING)
    private Act act;

    @Enumerated(EnumType.STRING)
    private Day day;
    //추후 돈 타입 만들기!
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY )
    @JoinColumn(name = "account_book_id")
    private AccountBook accountBook;


    /**dirty check 수정 메서드(변경_감지)**/
    public Record update(Integer originMoney, RecordUpdateDto recordDto , AccountBook accountBook) {
        this.money = recordDto.getMoney();
        this.memo = recordDto.getMemo();
        this.expendType = ExpendType.getEnum(recordDto.getExpendType());
        this.act = Act.getEnum(recordDto.getAct());
        this.day = Day.getEnum(recordDto.getDay());
        /*SPENDING(지출)일 때와 SAVING("저축")일 때의 구분*/
        if (recordDto.getExpendType().equals(Act.SPENDING.getDescription())) {
            accountBook.updateSpendMoney(originMoney, recordDto.getMoney());
        }else{
            accountBook.updateSaveMoney(originMoney, recordDto.getMoney());
        }
        return this;
    }
}
