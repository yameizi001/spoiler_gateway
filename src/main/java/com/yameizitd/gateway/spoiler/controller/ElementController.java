package com.yameizitd.gateway.spoiler.controller;

import com.yameizitd.gateway.spoiler.handler.ElementHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway-backend")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ElementController {
    private final ElementHandler elementHandler;

    public ElementController(ElementHandler elementHandler) {
        this.elementHandler = elementHandler;
    }
}
