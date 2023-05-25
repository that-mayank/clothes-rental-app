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
    @Column(name="refresh_tokens")
    private String refresh_Token;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRefresh_Token() {
        return refresh_Token;
    }

    public void setRefresh_Token(String refresh_Token) {
        this.refresh_Token = refresh_Token;
    }

    public RefreshToken(String email, String refresh_Token) {
        this.email = email;
        this.refresh_Token = refresh_Token;
    }
}
