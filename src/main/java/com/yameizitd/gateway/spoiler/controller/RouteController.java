package com.yameizitd.gateway.spoiler.controller;

import com.yameizitd.gateway.spoiler.handler.RouteHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway-backend")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RouteController {
    private final RouteHandler routeHandler;

    public RouteController(RouteHandler routeHandler) {
        this.routeHandler = routeHandler;
    }
}
