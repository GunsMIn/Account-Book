package com.payhere.account.domain.entity;

import com.payhere.account.domain.entity.type.UserRole;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static javax.persistence.CascadeType.REMOVE;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user")
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE user SET deleted_at = now() WHERE user_id = ?")
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity implements UserDetails  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;//이름

    @Column(nullable = false)
    private String email;//이메일

    @Column(nullable = false)
    private String password;//비밀번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToMany(mappedBy = "user",cascade = REMOVE, orphanRemoval = true)
    private List<AccountBook> accountBooks = new ArrayList<>();




    public User(String userName, String email, String password) {
        this.name = userName;
        this.email = email;
        this.password = password;
    }

    /**user의 권한 변경 dirty check**/
    public User(UserRole role) {
        this.role = role;
    }
    public void changeRole(UserRole role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.getRole().name()));
    }

    /**주의**/
    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
