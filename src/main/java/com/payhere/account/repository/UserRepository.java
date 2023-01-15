package com.payhere.account.repository;

import com.payhere.account.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    /*삭제 된 user(회원) 복원*/
    @Modifying
    @Query("update User u set u.deletedAt = null where u.id = :userId ")
    void reSave(@Param("userId") Long userId);

}
