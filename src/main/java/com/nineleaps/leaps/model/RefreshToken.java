package com.nineleaps.leaps.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a Refresh Token entity used for token-based authentication.
 * This entity is used to store and manage refresh tokens associated with user emails.
 */
@Entity
@Table(name = "Tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {
    /**
     * The email address associated with the refresh token (Primary Key).
     */
    @Id
    private String email;

    /**
     * The refresh token value stored in the database.
     */
    @Column(name = "refresh_token")
    private String token;
}
