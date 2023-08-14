package com.nineleaps.leaps.model.tokens;

import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.UserDeviceDetail;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
public class RefreshToken implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_device_detail_id")
    private UserDeviceDetail userDeviceDetail;

    @Column(name = "jwt_token")
    private String jwtToken;

    @Column(name = "token_expiry")
    private Date tokenExpiry;

    @Column(name = "is_expired")
    private boolean isExpired;

    @Column(name = "is_revoked")
    private boolean isRevoked;

    // Constructors, getters, setters, etc.
}
