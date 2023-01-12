package com.payhere.account.repository;


import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.Record;
import com.payhere.account.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record,Long> {

    @Modifying
    @Query("update Record r set r.deletedAt = null where r.id = :recordId ")
    void reSave(@Param("recordId") Long recordId);

    Page<Record> findByAccountBook(AccountBook accountBook,Pageable pageable);


}
