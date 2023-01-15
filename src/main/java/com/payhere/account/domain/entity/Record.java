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
@Table(name="record")
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE record SET deleted_at = now() WHERE record_id = ?")
public class Record extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @Column(nullable = false)
    private String memo; // 가계부 기록 메모

    @Column(nullable = false)
    private Integer money; // 돈(+,-)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpendType expendType; // 지출 타입

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Act act; // 지출 or 저축

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Day day; // 요일


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
        if (recordDto.getAct().equals(Act.SPENDING.getDescription())) {
            accountBook.updateSpendMoney(originMoney, recordDto.getMoney());
        }else if(recordDto.getAct().equals(Act.SAVING.getDescription())){
            accountBook.updateSaveMoney(originMoney, recordDto.getMoney());
        }
        return this;
    }
}
