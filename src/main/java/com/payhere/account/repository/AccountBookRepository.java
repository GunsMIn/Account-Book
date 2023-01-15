package com.payhere.account.repository;

import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountBookRepository extends JpaRepository<AccountBook,Long> {

    /*회원 당 가계부 갯수*/
    @Query("select count(a.id) from AccountBook a where a.user = :user and a.deletedAt is null ")
    Integer countByUser(@Param("user") User user);

    /*회원의 전체 가계부 페이징*/
    Page<AccountBook> findByUser(User user, Pageable pageable);

    /*삭제 된 accountBook(가계부) 복원*/
    @Modifying
    @Query("update AccountBook a set a.deletedAt = null where a.id = :accountBookId ")
    void reSave(@Param("accountBookId") Long accountBookId);
}
