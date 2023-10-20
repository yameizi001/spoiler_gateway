package com.yameizitd.gateway.spoiler.handler.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.yameizitd.gateway.spoiler.domain.ElementType;
import com.yameizitd.gateway.spoiler.domain.business.ElementWithPropertyValues;
import com.yameizitd.gateway.spoiler.domain.business.PropertyValues;
import com.yameizitd.gateway.spoiler.domain.business.RichRouteDefinition;
import com.yameizitd.gateway.spoiler.domain.entity.*;
import com.yameizitd.gateway.spoiler.domain.form.ElementWithPropertiesCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.PropertyValuesCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.TemplateQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.TemplateUpsertForm;
import com.yameizitd.gateway.spoiler.domain.view.ElementView;
import com.yameizitd.gateway.spoiler.domain.view.PropertyValuesView;
import com.yameizitd.gateway.spoiler.domain.view.SimpleTemplateView;
import com.yameizitd.gateway.spoiler.domain.view.TemplateDetailView;
import com.yameizitd.gateway.spoiler.exception.impl.EntryNotExistException;
import com.yameizitd.gateway.spoiler.exception.impl.IllegalValueException;
import com.yameizitd.gateway.spoiler.exception.impl.RouteInvalidException;
import com.yameizitd.gateway.spoiler.handler.RouteHandler;
import com.yameizitd.gateway.spoiler.handler.TemplateHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.ElementMapper;
import com.yameizitd.gateway.spoiler.mapper.PropertyMapper;
import com.yameizitd.gateway.spoiler.mapper.TemplateElementPropertyMapper;
import com.yameizitd.gateway.spoiler.mapper.TemplateMapper;
import com.yameizitd.gateway.spoiler.mapstruct.ElementMapstruct;
import com.yameizitd.gateway.spoiler.mapstruct.PropertyValuesMapstruct;
import com.yameizitd.gateway.spoiler.mapstruct.TemplateMapstruct;
import com.yameizitd.gateway.spoiler.route.RouteDefinitionChecker;
import com.yameizitd.gateway.spoiler.util.PageUtils;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateHandlerImpl implements TemplateHandler {
    private final TemplateMapper templateMapper;
    private final PropertyMapper propertyMapper;
    private final TemplateElementPropertyMapper templateElementPropertyMapper;
    private final ElementMapper elementMapper;
    private final TemplateMapstruct templateMapstruct;
    private final PropertyValuesMapstruct propertyValuesMapstruct;
    private final ElementMapstruct elementMapstruct;
    private final RouteDefinitionChecker routeDefinitionChecker;
    private final RouteHandler routeHandler;

    public TemplateHandlerImpl(TemplateMapper templateMapper,
                               PropertyMapper propertyMapper,
                               TemplateElementPropertyMapper templateElementPropertyMapper,
                               ElementMapper elementMapper,
                               TemplateMapstruct templateMapstruct,
                               PropertyValuesMapstruct propertyValuesMapstruct,
                               ElementMapstruct elementMapstruct,
                               RouteDefinitionChecker routeDefinitionChecker,
                               RouteHandler routeHandler) {
        this.templateMapper = templateMapper;
        this.propertyMapper = propertyMapper;
        this.templateElementPropertyMapper = templateElementPropertyMapper;
        this.elementMapper = elementMapper;
        this.templateMapstruct = templateMapstruct;
        this.propertyValuesMapstruct = propertyValuesMapstruct;
        this.elementMapstruct = elementMapstruct;
        this.routeDefinitionChecker = routeDefinitionChecker;
        this.routeHandler = routeHandler;
    }

    @Transactional
    @Override
    public RouteDefinition apply(TemplateUpsertForm form) {
        return transformAndCheck(form);
    }

    public RouteDefinition transformAndCheck(TemplateUpsertForm form) {
        RouteDefinition routeDefinition = temporary();
        Optional.ofNullable(form.getId())
                .map(String::valueOf)
                .ifPresent(routeDefinition::setId);
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
    public int create(TemplateUpsertForm form) {
        // check template valid
        RouteDefinition routeDefinition = apply(form);
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
        if (!TEMPORARY_ROUTE_DEFINITION_ID.equals(routeDefinition.getId())) {
            routeHandler.batchEditByDefinition(routeDefinition);
        }
        return inserted;
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
    public int edit(TemplateUpsertForm form) {
        Long id = form.getId();
        TemplateEntity record = templateMapper.selectById(id);
        if (record == null) {
            throw new EntryNotExistException("Template not exist");
        }
        form.setCreateTime(record.getCreateTime());
        int removed = this.remove(id);
        if (removed > 0) {
            return this.create(form);
        }
        return 0;
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
            List<ElementView> predicates = predicateRecords.stream()
                    .map(elementMapstruct::entity2view)
                    .toList();
            detail.setPredicates(predicates);
        }
        List<ElementEntity> filterRecords = elementMapper.selectByTemplateIdAndType(
                id, (short) ElementType.FILTER.ordinal()
        );
        if (filterRecords != null && !filterRecords.isEmpty()) {
            List<ElementView> filters = filterRecords.stream()
                    .map(elementMapstruct::entity2view)
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
