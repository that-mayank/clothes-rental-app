package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.OrderServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.YearMonth;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final OrderServiceInterface orderService;
    private final Helper helper;

    @ApiOperation(value = "Gives details about how many orders the owner has got and the total earnings")
    @GetMapping("/owner-view")
    public ResponseEntity<Map<String, Object>> dashboard(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Map<String, Object> result = orderService.dashboard(user);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Gives details about how many orders the owner has got")
    @GetMapping("/analytics")
    public ResponseEntity<Map<YearMonth, Map<String, Object>>> onClickDashboard(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Map<YearMonth, Map<String, Object>> body = orderService.onClickDasboard(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
