package com.yameizitd.gateway.spoiler.controller;

import com.yameizitd.gateway.spoiler.domain.form.ServiceCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.ServiceQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.ServiceUpdateForm;
import com.yameizitd.gateway.spoiler.domain.GenericResp;
import com.yameizitd.gateway.spoiler.domain.view.ServiceView;
import com.yameizitd.gateway.spoiler.handler.ServiceHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/gateway-backend")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ServiceController {
    private final ServiceHandler serviceHandler;

    public ServiceController(ServiceHandler serviceHandler) {
        this.serviceHandler = serviceHandler;
    }

    @PostMapping("/service")
    public Mono<GenericResp<Integer>> create(@RequestBody ServiceCreateForm form) {
        return Mono.just(form)
                .publishOn(Schedulers.boundedElastic())
                .map(serviceHandler::create)
                .map(GenericResp::ok);
    }

    @DeleteMapping("/service")
    public Mono<GenericResp<Integer>> delete(@RequestParam("id") Long id) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(serviceHandler::remove)
                .map(GenericResp::ok);
    }

    @PutMapping("/service")
    public Mono<GenericResp<Integer>> edit(@RequestBody ServiceUpdateForm form) {
        return Mono.just(form)
                .publishOn(Schedulers.boundedElastic())
                .map(serviceHandler::edit)
                .map(GenericResp::ok);
    }

    @PutMapping("/service/disable")
    public Mono<GenericResp<Integer>> disable(@RequestParam("id") Long id) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(serviceHandler::disable)
                .map(GenericResp::ok);
    }

    @PutMapping("/service/enable")
    public Mono<GenericResp<Integer>> enable(@RequestParam("id") Long id) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(serviceHandler::enable)
                .map(GenericResp::ok);
    }

    @PostMapping("/services/_query")
    public Mono<GenericResp<IPage<ServiceView>>> getPageableByOptions(@RequestBody ServiceQueryForm query) {
        return Mono.just(query)
                .publishOn(Schedulers.boundedElastic())
                .map(serviceHandler::getPageableByOptions)
                .map(GenericResp::ok);
    }
}
