package com.yameizitd.gateway.spoiler.controller;

import com.yameizitd.gateway.spoiler.domain.GenericResp;
import com.yameizitd.gateway.spoiler.domain.form.RouteCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteUpdateForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteWithTemplateUpsertForm;
import com.yameizitd.gateway.spoiler.domain.view.RouteView;
import com.yameizitd.gateway.spoiler.handler.RouteHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/gateway-backend")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RouteController {
    private final RouteHandler routeHandler;

    public RouteController(RouteHandler routeHandler) {
        this.routeHandler = routeHandler;
    }

    @PostMapping("/route")
    public Mono<GenericResp<Integer>> create(@RequestBody RouteCreateForm form) {
        return Mono.just(form)
                .publishOn(Schedulers.boundedElastic())
                .map(routeHandler::create)
                .map(GenericResp::ok);
    }

    @PostMapping("/route/template")
    public Mono<GenericResp<Integer>> createFromTemplate(@RequestBody RouteWithTemplateUpsertForm form) {
        return Mono.just(form)
                .publishOn(Schedulers.boundedElastic())
                .map(routeHandler::createFromTemplate)
                .map(GenericResp::ok);
    }

    @DeleteMapping("/route")
    public Mono<GenericResp<Integer>> remove(@RequestParam("id") Long id) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(routeHandler::remove)
                .map(GenericResp::ok);
    }

    @PutMapping("/route/disable")
    public Mono<GenericResp<Integer>> disable(@RequestParam("id") Long id) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(routeHandler::disable)
                .map(GenericResp::ok);
    }

    @PutMapping("/route/enable")
    public Mono<GenericResp<Integer>> enable(@RequestParam("id") Long id) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(routeHandler::enable)
                .map(GenericResp::ok);
    }

    @PutMapping("/route")
    public Mono<GenericResp<Integer>> edit(@RequestBody RouteUpdateForm form) {
        return Mono.just(form)
                .publishOn(Schedulers.boundedElastic())
                .map(routeHandler::edit)
                .map(GenericResp::ok);
    }

    @PutMapping("/route/template")
    public Mono<GenericResp<Integer>> editByTemplate(@RequestBody RouteWithTemplateUpsertForm form) {
        return Mono.just(form)
                .publishOn(Schedulers.boundedElastic())
                .map(routeHandler::editByTemplate)
                .map(GenericResp::ok);
    }

    @PostMapping("/routes/_query")
    public Mono<GenericResp<IPage<RouteView>>> getPageableByOptions(@RequestBody RouteQueryForm query) {
        return Mono.just(query)
                .publishOn(Schedulers.boundedElastic())
                .map(routeHandler::getPageableByOptions)
                .map(GenericResp::ok);
    }
}
