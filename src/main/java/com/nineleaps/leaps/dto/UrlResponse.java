package com.nineleaps.leaps.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UrlResponse {
    private List<String> urls;
}
