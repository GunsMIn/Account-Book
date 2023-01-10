package com.payhere.account.repository;

import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountBookRepository extends JpaRepository<AccountBook,Long> {

  /*  Optional<AccountBook> findBy*/
}
