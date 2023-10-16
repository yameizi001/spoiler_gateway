package com.yameizitd.gateway.spoiler.handler.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.yameizitd.gateway.spoiler.domain.ElementType;
import com.yameizitd.gateway.spoiler.domain.entity.*;
import com.yameizitd.gateway.spoiler.domain.form.ElementWithPropertiesCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.PropertyValuesCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.TemplateQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.TemplateUpsertForm;
import com.yameizitd.gateway.spoiler.domain.view.ElementView;
import com.yameizitd.gateway.spoiler.domain.view.PropertyValuesView;
import com.yameizitd.gateway.spoiler.domain.view.SimpleTemplateView;
import com.yameizitd.gateway.spoiler.domain.view.TemplateDetail;
import com.yameizitd.gateway.spoiler.exception.impl.EntryNotExistException;
import com.yameizitd.gateway.spoiler.exception.impl.IllegalValueException;
import com.yameizitd.gateway.spoiler.handler.TemplateHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.ElementMapper;
import com.yameizitd.gateway.spoiler.mapper.PropertyMapper;
import com.yameizitd.gateway.spoiler.mapper.TemplateElementPropertyMapper;
import com.yameizitd.gateway.spoiler.mapper.TemplateMapper;
import com.yameizitd.gateway.spoiler.mapstruct.ElementMapstruct;
import com.yameizitd.gateway.spoiler.mapstruct.PropertyValuesMapstruct;
import com.yameizitd.gateway.spoiler.mapstruct.TemplateMapstruct;
import com.yameizitd.gateway.spoiler.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class TemplateHandlerImpl implements TemplateHandler {
    private final TemplateMapper templateMapper;
    private final PropertyMapper propertyMapper;
    private final TemplateElementPropertyMapper templateElementPropertyMapper;
    private final ElementMapper elementMapper;
    private final TemplateMapstruct templateMapstruct;
    private final PropertyValuesMapstruct propertyValuesMapstruct;
    private final ElementMapstruct elementMapstruct;

    public TemplateHandlerImpl(TemplateMapper templateMapper,
                               PropertyMapper propertyMapper,
                               TemplateElementPropertyMapper templateElementPropertyMapper,
                               ElementMapper elementMapper,
                               TemplateMapstruct templateMapstruct,
                               PropertyValuesMapstruct propertyValuesMapstruct,
                               ElementMapstruct elementMapstruct) {
        this.templateMapper = templateMapper;
        this.propertyMapper = propertyMapper;
        this.templateElementPropertyMapper = templateElementPropertyMapper;
        this.elementMapper = elementMapper;
        this.templateMapstruct = templateMapstruct;
        this.propertyValuesMapstruct = propertyValuesMapstruct;
        this.elementMapstruct = elementMapstruct;
    }

    @Transactional
    @Override
    public int create(TemplateUpsertForm form) {
        // check template valid
        checkTemplateValid(form);
        TemplateEntity entity = templateMapstruct.upsertForm2entity(form);
        // insert template
        int inserted = templateMapper.insert(entity);
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
        return inserted;
    }

    private void checkTemplateValid(TemplateUpsertForm form) {
        List<ElementWithPropertiesCreateForm> predicates = form.getPredicates();
        for (ElementWithPropertiesCreateForm predicate : predicates) {
            checkElementPropertiesValid(predicate);
        }
        List<ElementWithPropertiesCreateForm> filters = form.getFilters();
        for (ElementWithPropertiesCreateForm filter : filters) {
            checkElementPropertiesValid(filter);
        }
        List<PropertyValuesCreateForm> metadata = form.getMetadata();
        for (PropertyValuesCreateForm meta : metadata) {
            checkTemplatePropertiesValid(-1L, meta);
        }
    }

    private void checkElementPropertiesValid(ElementWithPropertiesCreateForm form) {
        Long elementId = form.getId();
        String alias = form.getAlias();
        ElementEntity record = elementMapper.selectByIdForUpdate(elementId);
        if (record == null) {
            throw new EntryNotExistException(String.format("Element '%s' not exist", alias));
        }
        List<PropertyValuesCreateForm> properties = form.getProperties();
        if (properties != null && !properties.isEmpty()) {
            for (PropertyValuesCreateForm property : properties) {
                checkTemplatePropertiesValid(elementId, property);
            }
        }
    }

    private void checkTemplatePropertiesValid(Long elementId, PropertyValuesCreateForm form) {
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
        int removed = this.remove(form.getId());
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
    public TemplateDetail getDetailById(long id) {
        TemplateEntity record = templateMapper.selectById(id);
        if (record == null) {
            throw new EntryNotExistException("Template not exist");
        }
        TemplateDetail detail = templateMapstruct.entity2detail(record);
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
