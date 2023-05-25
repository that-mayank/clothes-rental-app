package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.service.BannerService;
import com.nineleaps.leaps.service.BannerServiceInterface;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/banner")
@RequiredArgsConstructor
@Api(tags = "Banner Api", description = "Contains api for providing banner images")
public class BannerController {
    private final BannerServiceInterface bannerServiceInterface;
    @GetMapping("/getbannerurl")
    public ResponseEntity<String> getBannerUrl(@RequestParam(value="bannername") String name){
        String url = bannerServiceInterface.getbannerUrl(name);
        return new ResponseEntity<>(url,HttpStatus.OK);
    }
}
