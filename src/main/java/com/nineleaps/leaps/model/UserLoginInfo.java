package com.nineleaps.leaps.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.nineleaps.leaps.LeapsApplication.ACCOUNT_LOCK_DURATION_MINUTES;

@Entity
@Table(name = "user_login_info")
@Getter
@Setter
@NoArgsConstructor
public class UserLoginInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private Integer loginAttempts;

    @Column(name = "last_login_attempt")
    private LocalDateTime lastLoginAttempt;

    private Boolean locked;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    // Constructors and other fields...
    public void initializeLoginInfo(User user) {
        this.user = user;
        this.loginAttempts = 0;
        this.lastLoginAttempt = null;
        this.locked = false;
        this.lockTime = null;
    }



    public boolean isAccountLocked() {
        return this.locked &&
                ChronoUnit.MINUTES.between(this.lockTime, LocalDateTime.now()) <= ACCOUNT_LOCK_DURATION_MINUTES;
    }

    public void lockAccount() {
        this.locked = true;
        this.lockTime = LocalDateTime.now();
    }

    public void resetLoginAttempts() {
        this.loginAttempts = 0;
        this.lastLoginAttempt = null;
        this.locked = false;
        this.lockTime = null;
    }
}
