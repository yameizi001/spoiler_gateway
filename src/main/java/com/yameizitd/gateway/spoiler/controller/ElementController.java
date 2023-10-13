package com.yameizitd.gateway.spoiler.controller;

import com.yameizitd.gateway.spoiler.domain.form.ElementCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.ElementQueryForm;
import com.yameizitd.gateway.spoiler.domain.view.ElementView;
import com.yameizitd.gateway.spoiler.domain.view.GenericResp;
import com.yameizitd.gateway.spoiler.handler.ElementHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/gateway-backend")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ElementController {
    private final ElementHandler elementHandler;

    public ElementController(ElementHandler elementHandler) {
        this.elementHandler = elementHandler;
    }

    @PostMapping("/element")
    public Mono<GenericResp<Integer>> create(@RequestBody ElementCreateForm form) {
        return Mono.just(form)
                .publishOn(Schedulers.boundedElastic())
                .map(elementHandler::create)
                .map(GenericResp::ok);
    }

    @DeleteMapping("/element")
    public Mono<GenericResp<Integer>> remove(@RequestParam("id") Long id) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(elementHandler::remove)
                .map(GenericResp::ok);
    }

    @PostMapping("/elements/_query")
    public Mono<GenericResp<IPage<ElementView>>> getPageableByOptions(@RequestBody ElementQueryForm query) {
        return Mono.just(query)
                .publishOn(Schedulers.boundedElastic())
                .map(elementHandler::getPageableByOptions)
                .map(GenericResp::ok);
    }
}
