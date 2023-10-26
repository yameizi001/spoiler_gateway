package com.yameizitd.gateway.spoiler.handler.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.yameizitd.gateway.spoiler.domain.ElementType;
import com.yameizitd.gateway.spoiler.domain.TemplateType;
import com.yameizitd.gateway.spoiler.domain.business.ElementWithPropertyValues;
import com.yameizitd.gateway.spoiler.domain.business.PropertyValues;
import com.yameizitd.gateway.spoiler.domain.business.RichRouteDefinition;
import com.yameizitd.gateway.spoiler.domain.entity.*;
import com.yameizitd.gateway.spoiler.domain.form.*;
import com.yameizitd.gateway.spoiler.domain.view.ElementView;
import com.yameizitd.gateway.spoiler.domain.view.PropertyValuesView;
import com.yameizitd.gateway.spoiler.domain.view.SimpleTemplateView;
import com.yameizitd.gateway.spoiler.domain.view.TemplateDetailView;
import com.yameizitd.gateway.spoiler.eventbus.RefreshEvent;
import com.yameizitd.gateway.spoiler.exception.impl.EntryNotExistException;
import com.yameizitd.gateway.spoiler.exception.impl.IllegalValueException;
import com.yameizitd.gateway.spoiler.exception.impl.RouteInvalidException;
import com.yameizitd.gateway.spoiler.handler.EventHandler;
import com.yameizitd.gateway.spoiler.handler.TemplateHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.*;
import com.yameizitd.gateway.spoiler.mapstruct.ElementMapstruct;
import com.yameizitd.gateway.spoiler.mapstruct.PropertyValuesMapstruct;
import com.yameizitd.gateway.spoiler.mapstruct.RouteMapstruct;
import com.yameizitd.gateway.spoiler.mapstruct.TemplateMapstruct;
import com.yameizitd.gateway.spoiler.route.RouteDefinitionChecker;
import com.yameizitd.gateway.spoiler.util.JacksonUtils;
import com.yameizitd.gateway.spoiler.util.PageUtils;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TemplateHandlerImpl implements TemplateHandler {
    private final TemplateMapper templateMapper;
    private final ElementMapper elementMapper;
    private final PropertyMapper propertyMapper;
    private final TemplateElementPropertyMapper templateElementPropertyMapper;
    private final RouteMapper routeMapper;
    private final TemplateMapstruct templateMapstruct;
    private final ElementMapstruct elementMapstruct;
    private final PropertyValuesMapstruct propertyValuesMapstruct;
    private final RouteMapstruct routeMapstruct;
    private final RouteDefinitionChecker routeDefinitionChecker;
    private final EventHandler eventHandler;

    public TemplateHandlerImpl(TemplateMapper templateMapper,
                               ElementMapper elementMapper,
                               PropertyMapper propertyMapper,
                               TemplateElementPropertyMapper templateElementPropertyMapper,
                               RouteMapper routeMapper,
                               TemplateMapstruct templateMapstruct,
                               ElementMapstruct elementMapstruct,
                               PropertyValuesMapstruct propertyValuesMapstruct,
                               RouteMapstruct routeMapstruct,
                               RouteDefinitionChecker routeDefinitionChecker,
                               EventHandler eventHandler) {
        this.templateMapper = templateMapper;
        this.elementMapper = elementMapper;
        this.propertyMapper = propertyMapper;
        this.templateElementPropertyMapper = templateElementPropertyMapper;
        this.routeMapper = routeMapper;
        this.templateMapstruct = templateMapstruct;
        this.elementMapstruct = elementMapstruct;
        this.propertyValuesMapstruct = propertyValuesMapstruct;
        this.routeMapstruct = routeMapstruct;
        this.routeDefinitionChecker = routeDefinitionChecker;
        this.eventHandler = eventHandler;
    }

    @Transactional
    @Override
    public RouteDefinition checkAndApply(TemplateUpsertForm form) {
        return checkAndApply0(form);
    }

    public RouteDefinition checkAndApply0(TemplateUpsertForm form) {
        RouteDefinition routeDefinition = temporary();
        RichRouteDefinition richRouteDefinition = checkTemplateValid(form);
        richRouteDefinition.populateRouteDefinition(routeDefinition);
        if (!routeDefinitionChecker.check(routeDefinition)) {
            throw new RouteInvalidException("Template fails to be mapped to route temporarily");
        }
        return routeDefinition;
    }

    private RichRouteDefinition checkTemplateValid(TemplateUpsertForm form) {
        RichRouteDefinition richRouteDefinition = new RichRouteDefinition();
        List<ElementWithPropertiesCreateForm> predicates = form.getPredicates();
        for (ElementWithPropertiesCreateForm predicate : predicates) {
            richRouteDefinition.getPredicates().add(checkElementPropertiesValid(predicate));
        }
        List<ElementWithPropertiesCreateForm> filters = form.getFilters();
        for (ElementWithPropertiesCreateForm filter : filters) {
            richRouteDefinition.getFilters().add(checkElementPropertiesValid(filter));
        }
        List<PropertyValuesCreateForm> metadata = form.getMetadata();
        for (PropertyValuesCreateForm meta : metadata) {
            richRouteDefinition.getMetadata().add(checkTemplatePropertiesValid(-1L, meta));
        }
        return richRouteDefinition;
    }

    private ElementWithPropertyValues checkElementPropertiesValid(
            ElementWithPropertiesCreateForm form) {
        Long elementId = form.getId();
        String alias = form.getAlias();
        ElementEntity record = elementMapper.selectByIdForUpdate(elementId);
        if (record == null) {
            throw new EntryNotExistException(String.format("Element '%s' not exist", alias));
        }
        ElementWithPropertyValues elementWithPropertyValues = new ElementWithPropertyValues();
        elementWithPropertyValues.setName(record.getName());
        elementWithPropertyValues.setOrdered(record.getOrdered());
        List<PropertyValuesCreateForm> properties = form.getProperties();
        if (properties != null && !properties.isEmpty()) {
            for (PropertyValuesCreateForm property : properties) {
                elementWithPropertyValues.getPropertyValues().add(checkTemplatePropertiesValid(elementId, property));
            }
        }
        return elementWithPropertyValues;
    }

    private PropertyValues checkTemplatePropertiesValid(Long elementId, PropertyValuesCreateForm form) {
        Long propertyId = form.getId();
        String alias = form.getAlias();
        PropertyEntity record = propertyMapper.selectByIdAndElementIdForUpdate(propertyId, elementId);
        if (record == null) {
            throw new EntryNotExistException(String.format("Property '%s' not exist", alias));
        }
        JsonNode values = form.getValues();
        if (record.getRequired() && (values == null || values.isNull() || values.isEmpty())) {
            throw new IllegalValueException(String.format("Property '%s' should not be null or empty", alias));
        }
        if (!values.isArray()) {
            throw new IllegalValueException(String.format("Property '%s' should be array", alias));
        }
        ArrayNode array = (ArrayNode) values;
        String regex = record.getRegex();
        if (StringUtils.hasLength(regex)) {
            for (JsonNode node : array) {
                String text = node.asText();
                if (StringUtils.hasLength(text) && !text.matches(regex)) {
                    throw new IllegalValueException(String.format("Property '%s' is invalid", alias));
                }
            }
        }
        PropertyValues propertyValues = propertyValuesMapstruct.entity2definition(record);
        propertyValues.setValues(values);
        return propertyValues;
    }

    @Transactional
    @Override
    public RouteDefinition create(TemplateUpsertForm form) {
        // check template valid
        RouteDefinition routeDefinition = checkAndApply(form);
        long id = createWithoutCheck(form);
        routeDefinition.setId(String.valueOf(id));
        return routeDefinition;
    }

    private long createWithoutCheck(TemplateUpsertForm form) {
        TemplateEntity entity = templateMapstruct.upsertForm2entity(form);
        // insert template
        int inserted = templateMapper.upsert(entity);
        if (inserted > 0) {
            Long templateId = entity.getId();
            // insert predicates and properties
            List<ElementWithPropertiesCreateForm> predicates = form.getPredicates();
            insertElementsAndProperties(templateId, predicates);
            // insert filters and properties
            List<ElementWithPropertiesCreateForm> filters = form.getFilters();
            insertElementsAndProperties(templateId, filters);
            // insert metadata
            List<PropertyValuesCreateForm> metadata = form.getMetadata();
            insertProperties(templateId, null, metadata);
        }
        return entity.getId();
    }

    private void insertElementsAndProperties(Long templateId, List<ElementWithPropertiesCreateForm> elements) {
        if (elements != null && !elements.isEmpty()) {
            // insert elements
            List<Long> elementIds = elements.stream()
                    .map(ElementWithPropertiesCreateForm::getId)
                    .toList();
            templateElementPropertyMapper.batchInsertElementsByTemplateId(templateId, elementIds);
            // insert properties of elements
            for (ElementWithPropertiesCreateForm element : elements) {
                Long elementId = element.getId();
                List<PropertyValuesCreateForm> properties = element.getProperties();
                insertProperties(templateId, elementId, properties);
            }
        }
    }

    private void insertProperties(Long templateId, Long elementId, List<PropertyValuesCreateForm> properties) {
        if (properties != null && !properties.isEmpty()) {
            List<PropertyValuesEntity> entities = properties.stream()
                    .map(propertyValuesMapstruct::createForm2entity)
                    .toList();
            if (elementId != null) {
                templateElementPropertyMapper.batchInsertPropertiesByTemplateIdAndElementId(
                        templateId, elementId, entities
                );
            } else {
                templateElementPropertyMapper.batchInsertPropertiesByTemplateId(templateId, entities);
            }
        }
    }

    @Transactional
    @Override
    public int remove(long id) {
        int deleted = templateMapper.delete(id);
        if (deleted > 0) {
            // delete associated records
            templateElementPropertyMapper.deleteElementsByTemplateId(id);
            templateElementPropertyMapper.deleteElementPropertiesByTemplateId(id);
            templateElementPropertyMapper.deletePropertiesByTemplateId(id);
        }
        return deleted;
    }

    @Transactional
    @Override
    public int removeByIdAndType(long id, TemplateType type) {
        int deleted = templateMapper.deleteByIdAndType(id, templateMapstruct.templateType2short(type));
        if (deleted > 0) {
            // delete associated records
            templateElementPropertyMapper.deleteElementsByTemplateId(id);
            templateElementPropertyMapper.deleteElementPropertiesByTemplateId(id);
            templateElementPropertyMapper.deletePropertiesByTemplateId(id);
        }
        return deleted;
    }

    @Transactional
    @Override
    public RouteDefinition edit(TemplateUpsertForm form) {
        Long id = form.getId();
        TemplateEntity record = templateMapper.selectById(id);
        if (record == null) {
            throw new EntryNotExistException("Template not exist");
        }
        form.setCreateTime(record.getCreateTime());
        // remove entity and associated entities first
        int removed = this.remove(id);
        if (removed > 0) {
            // create template
            RouteDefinition routeDefinition = create(form);
            if (routeDefinition != null) {
                // batch update associated routes
                batchEditByDefinition(routeDefinition);
            }
            return routeDefinition;
        }
        throw new EntryNotExistException("Template failed to edit");
    }

    private void batchEditByDefinition(RouteDefinition routeDefinition) {
        String templateId = routeDefinition.getId();
        String predicates = JacksonUtils.list2string(routeDefinition.getPredicates());
        String filters = JacksonUtils.list2string(routeDefinition.getFilters());
        String metadata = JacksonUtils.map2string(routeDefinition.getMetadata());
        List<RouteEntity> entities = new ArrayList<>();
        batchEdit(1L, Long.valueOf(templateId), predicates, filters, metadata, entities);
        eventHandler.publish(RefreshEvent.Operation.SAVE_ROUTES, entities);
    }

    private void batchEdit(long pageNum, Long templateId, String predicates, String filters, String metadata, List<RouteEntity> collector) {
        List<RichRouteEntity> records = routeMapper.selectByOptions(
                new RouteQueryForm(
                        null,
                        null,
                        templateId,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null),
                IPage.of(pageNum, 20L)
        );
        if (records == null || records.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<RouteEntity> entities = records.stream()
                .peek(item -> {
                    item.setPredicates(predicates);
                    item.setFilters(filters);
                    item.setMetadata(metadata);
                    item.setUpdateTime(now);
                })
                .map(routeMapstruct::richEntity2entity)
                .toList();
        routeMapper.batchUpdate(entities);
        collector.addAll(entities);
        batchEdit(++pageNum, templateId, predicates, filters, metadata, collector);
    }

    @Override
    public IPage<SimpleTemplateView> getPageableByOptions(TemplateQueryForm query) {
        IPage<TemplateEntity> page = query.getPage().iPage();
        List<TemplateEntity> records = templateMapper.selectByOptions(
                query,
                templateMapstruct.templateType2short(query.getType()),
                page
        );
        page.setRecords(records);
        return PageUtils.map(page, templateMapstruct::entity2simpleView);
    }

    @Override
    public TemplateDetailView getDetailById(long id) {
        TemplateEntity record = templateMapper.selectById(id);
        if (record == null) {
            throw new EntryNotExistException("Template not exist");
        }
        TemplateDetailView detail = templateMapstruct.entity2detail(record);
        List<ElementEntity> predicateRecords = elementMapper.selectByTemplateIdAndType(
                id, (short) ElementType.PREDICATE.ordinal()
        );
        if (predicateRecords != null && !predicateRecords.isEmpty()) {
            // populate properties of element
            List<ElementView> predicates = predicateRecords.stream()
                    .map(elementMapstruct::entity2view)
                    .peek(view -> {
                        List<PropertyValuesView> properties = templateElementPropertyMapper
                                .selectPropertiesByTemplateIdAndElementId(id, Long.valueOf(view.getId()))
                                .stream()
                                .map(propertyValuesMapstruct::richEntity2view)
                                .toList();
                        view.setProperties(properties);
                    })
                    .toList();
            detail.setPredicates(predicates);
        }
        List<ElementEntity> filterRecords = elementMapper.selectByTemplateIdAndType(
                id, (short) ElementType.FILTER.ordinal()
        );
        if (filterRecords != null && !filterRecords.isEmpty()) {
            // populate properties of element
            List<ElementView> filters = filterRecords.stream()
                    .map(elementMapstruct::entity2view)
                    .peek(view -> {
                        List<PropertyValuesView> properties = templateElementPropertyMapper
                                .selectPropertiesByTemplateIdAndElementId(id, Long.valueOf(view.getId()))
                                .stream()
                                .map(propertyValuesMapstruct::richEntity2view)
                                .toList();
                        view.setProperties(properties);
                    })
                    .toList();
            detail.setFilters(filters);
        }
        List<RichPropertyValuesEntity> metadataRecords = templateElementPropertyMapper.selectPropertiesByTemplateId(id);
        if (metadataRecords != null && !metadataRecords.isEmpty()) {
            List<PropertyValuesView> metadata = metadataRecords.stream()
                    .map(propertyValuesMapstruct::richEntity2view)
                    .toList();
            detail.setMetadata(metadata);
        }
        return detail;
    }

    @Override
    public List<PropertyValuesView> getPropertiesByTemplateIdAndElementId(long templateId, long elementId) {
        List<RichPropertyValuesEntity> records = templateElementPropertyMapper.selectPropertiesByTemplateIdAndElementId(
                templateId, elementId
        );
        return records.stream()
                .map(propertyValuesMapstruct::richEntity2view)
                .toList();
    }
}
