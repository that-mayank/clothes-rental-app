package com.nineleaps.leaps.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {
    @Id
    private String email;

    @Column(name = "refresh_token")
    private String token;
}
