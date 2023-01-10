package com.payhere.account.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE AccountBook SET deleted_at = now() WHERE id = ?")
public class AccountBook extends BaseEntity {
    /**user와 다 대 1**/
    /**
     * Record와 1 대 다
     **/
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



    /**dirty check 수정 메서드(변경감지) **/
    public AccountBook update(String title, String memo,Integer balance) {
        this.title = title;
        this.memo = memo;
        this.balance = balance;
        return this;
    }

}
