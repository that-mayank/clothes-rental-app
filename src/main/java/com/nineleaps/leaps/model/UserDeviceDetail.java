package com.nineleaps.leaps.model;

import com.nineleaps.leaps.model.tokens.AccessToken;
import com.nineleaps.leaps.model.tokens.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_device_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_token")
    private String deviceToken;

    @Column(name = "unique_device_id")
    private String uniqueDeviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "userDeviceDetail", cascade = CascadeType.ALL)
    private AccessToken accessToken;

    @OneToOne(mappedBy = "userDeviceDetail", cascade = CascadeType.ALL)
    private RefreshToken refreshToken;

    public UserDeviceDetail(String uniqueDeviceId, User user) {
        this.uniqueDeviceId = uniqueDeviceId;
        this.user = user;
    }

    public void updateDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
