package com.yameizitd.gateway.spoiler.controller;

import com.yameizitd.gateway.spoiler.domain.form.InstanceCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.InstanceQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.InstanceUpdateForm;
import com.yameizitd.gateway.spoiler.domain.GenericResp;
import com.yameizitd.gateway.spoiler.domain.view.InstanceView;
import com.yameizitd.gateway.spoiler.handler.InstanceHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/gateway-backend")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class InstanceController {
    private final InstanceHandler instanceHandler;

    public InstanceController(InstanceHandler instanceHandler) {
        this.instanceHandler = instanceHandler;
    }

    @PostMapping("/instance")
    public Mono<GenericResp<Integer>> create(@RequestBody InstanceCreateForm form) {
        return Mono.just(form)
                .publishOn(Schedulers.boundedElastic())
                .map(instanceHandler::create)
                .map(GenericResp::ok);
    }

    @DeleteMapping("/instance")
    public Mono<GenericResp<Integer>> remove(@RequestParam("id") Long id) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(instanceHandler::remove)
                .map(GenericResp::ok);
    }

    @PutMapping("/instance")
    public Mono<GenericResp<Integer>> edit(@RequestBody InstanceUpdateForm form) {
        return Mono.just(form)
                .publishOn(Schedulers.boundedElastic())
                .map(instanceHandler::edit)
                .map(GenericResp::ok);
    }

    @PutMapping("/instance/disable")
    public Mono<GenericResp<Integer>> disable(@RequestParam("id") Long id) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(instanceHandler::disable)
                .map(GenericResp::ok);
    }

    @PutMapping("/instance/enable")
    public Mono<GenericResp<Integer>> enable(@RequestParam("id") Long id) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(instanceHandler::enable)
                .map(GenericResp::ok);
    }

    @PostMapping("/instances/_query")
    public Mono<GenericResp<IPage<InstanceView>>> getPageableByOptions(@RequestBody InstanceQueryForm query) {
        return Mono.just(query)
                .publishOn(Schedulers.boundedElastic())
                .map(instanceHandler::getPageableByOptions)
                .map(GenericResp::ok);
    }
}
