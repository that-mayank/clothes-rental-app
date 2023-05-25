package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.Banner;
import com.nineleaps.leaps.repository.BannerRepository;
import org.springframework.stereotype.Service;

@Service
public class BannerService  implements BannerServiceInterface{
    private final BannerRepository bannerRepository;

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @Override
    public String getbannerUrl(String bannername) {
        Banner banner = bannerRepository.findByBannerName(bannername);
        if(banner == null){
            System.out.println("the object is null");
        }else{
            String bannerurl = banner.getBannerURL();
            return bannerurl;
        }
        return null;


    }
}
