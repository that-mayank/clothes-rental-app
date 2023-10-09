package com.nineleaps.leaps.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;


@Entity
@Table(name = "Tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    // Audit Columns
    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @Column(name = "end_at")
    private LocalDateTime endingAt;

    @Id
    private String email;

    @Column(name = "refresh_token")
    private String token;

    public void setAuditColumns( LocalDateTime time) {

        this.createdAt = LocalDateTime.now();
        this.endingAt = time;
    }
}
