package com.yameizitd.gateway.spoiler.controller;

import com.yameizitd.gateway.spoiler.domain.GenericResp;
import com.yameizitd.gateway.spoiler.domain.form.TemplateQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.TemplateUpsertForm;
import com.yameizitd.gateway.spoiler.domain.view.PropertyValuesView;
import com.yameizitd.gateway.spoiler.domain.view.SimpleTemplateView;
import com.yameizitd.gateway.spoiler.domain.view.TemplateDetailView;
import com.yameizitd.gateway.spoiler.handler.TemplateHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequestMapping("/gateway-backend")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class TemplateController {
    private final TemplateHandler templateHandler;

    public TemplateController(TemplateHandler templateHandler) {
        this.templateHandler = templateHandler;
    }

    @PostMapping("/template")
    public Mono<GenericResp<RouteDefinition>> create(@RequestBody TemplateUpsertForm form) {
        return Mono.just(form)
                .publishOn(Schedulers.boundedElastic())
                .map(templateHandler::create)
                .map(GenericResp::ok);
    }

    @DeleteMapping("/template")
    public Mono<GenericResp<Integer>> remove(@RequestParam("id") Long id) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(templateHandler::remove)
                .map(GenericResp::ok);
    }

    @PutMapping("/template")
    public Mono<GenericResp<RouteDefinition>> edit(@RequestBody TemplateUpsertForm form) {
        return Mono.just(form)
                .publishOn(Schedulers.boundedElastic())
                .map(templateHandler::edit)
                .map(GenericResp::ok);
    }

    @PostMapping("/templates/_query")
    public Mono<GenericResp<IPage<SimpleTemplateView>>> getPageableByOptions(@RequestBody TemplateQueryForm query) {
        return Mono.just(query)
                .publishOn(Schedulers.boundedElastic())
                .map(templateHandler::getPageableByOptions)
                .map(GenericResp::ok);
    }

    @GetMapping("/template")
    public Mono<GenericResp<TemplateDetailView>> getDetailById(@RequestParam("id") Long id) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(templateHandler::getDetailById)
                .map(GenericResp::ok);
    }

    @GetMapping("/template/element/properties")
    public Mono<GenericResp<List<PropertyValuesView>>> getPropertiesByTemplateIdAndElementId(
            @RequestParam("templateId") Long templateId,
            @RequestParam("elementId") Long elementId) {
        return Mono.just(new Object())
                .publishOn(Schedulers.boundedElastic())
                .map(o -> templateHandler.getPropertiesByTemplateIdAndElementId(templateId, elementId))
                .map(GenericResp::ok);
    }
}
