package com.yameizitd.gateway.spoiler.controller;

import com.yameizitd.gateway.spoiler.domain.form.PropertyCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.PropertyQueryForm;
import com.yameizitd.gateway.spoiler.domain.GenericResp;
import com.yameizitd.gateway.spoiler.domain.view.PropertyView;
import com.yameizitd.gateway.spoiler.handler.PropertyHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/gateway-backend")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class PropertyController {
    private final PropertyHandler propertyHandler;

    public PropertyController(PropertyHandler propertyHandler) {
        this.propertyHandler = propertyHandler;
    }

    @PostMapping("/property")
    public Mono<GenericResp<Integer>> create(@RequestBody PropertyCreateForm form) {
        return Mono.just(form)
                .publishOn(Schedulers.boundedElastic())
                .map(propertyHandler::create)
                .map(GenericResp::ok);
    }

    @DeleteMapping("/property")
    public Mono<GenericResp<Integer>> remove(@RequestParam("id") Long id) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(propertyHandler::remove)
                .map(GenericResp::ok);
    }

    @PostMapping("/properties/_query")
    public Mono<GenericResp<IPage<PropertyView>>> getPageableByOptions(@RequestBody PropertyQueryForm query) {
        return Mono.just(query)
                .publishOn(Schedulers.boundedElastic())
                .map(propertyHandler::getPageableByOptions)
                .map(GenericResp::ok);
    }
}
