package com.nineleaps.leaps.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="bannerimages")
@Getter@Setter
@NoArgsConstructor
public class Banner {
    @Id
    private String bannerName;
    @Column(name="banner_URL")
    private String bannerURL;

    public String getBannerName() {
        return bannerName;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    public String getBannerURL() {
        return bannerURL;
    }

    public void setBannerURL(String bannerURL) {
        this.bannerURL = bannerURL;
    }

    public Banner(String bannerName, String bannerURL) {
        this.bannerName = bannerName;
        this.bannerURL = bannerURL;
    }



}
