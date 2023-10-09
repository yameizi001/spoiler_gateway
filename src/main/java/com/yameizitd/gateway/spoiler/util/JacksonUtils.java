package com.yameizitd.gateway.spoiler.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.yameizitd.gateway.spoiler.exception.impl.TypeConvertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.KotlinDetector;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Slf4j
public final class JacksonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectMapper newInstance() {
        ObjectMapper mapper = new ObjectMapper();
        StdTypeResolverBuilder resolverBuilder = new TypeResolverBuilder(
                ObjectMapper.DefaultTyping.EVERYTHING,
                mapper.getPolymorphicTypeValidator()
        );
        resolverBuilder = resolverBuilder.init(JsonTypeInfo.Id.CLASS, null);
        resolverBuilder = resolverBuilder.inclusion(JsonTypeInfo.As.PROPERTY);
        mapper.setDefaultTyping(resolverBuilder);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    public static JsonNode string2jsonNode(String json) {
        if (!StringUtils.hasLength(json))
            return null;
        try {
            return mapper.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("Cannot convert string to json node", e);
            throw new TypeConvertException("Cannot convert string to json node", e);
        }
    }

    public static String jsonNode2string(JsonNode json) {
        if (json == null)
            return null;
        try {
            return mapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            log.error("Cannot convert json node to json string", e);
            throw new TypeConvertException("Cannot convert json node to json string", e);
        }
    }

    public static Map jsonNode2map(JsonNode json) {
        if (json == null)
            return null;
        try {
            return mapper.convertValue(json, new TypeReference<>() {
            });
        } catch (IllegalArgumentException e) {
            log.error("Cannot convert json node to map", e);
            throw new TypeConvertException("Cannot convert json node to map", e);
        }
    }

    public static List jsonNode2list(JsonNode json) {
        if (json == null)
            return null;
        try {
            return mapper.convertValue(json, new TypeReference<>() {
            });
        } catch (IllegalArgumentException e) {
            log.error("Cannot convert json node to list", e);
            throw new TypeConvertException("Cannot convert json node to list", e);
        }
    }

    public static <T> T map2obj(Map map, Class<T> clazz) {
        try {
            return mapper.convertValue(map, clazz);
        } catch (IllegalArgumentException e) {
            String info = String.format("Cannot convert map to class: %s", clazz.getName());
            log.error(info, e);
            throw new TypeConvertException(info, e);
        }
    }

    private static class TypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {
        @Serial
        private static final long serialVersionUID = -8050305287763525296L;

        public TypeResolverBuilder(ObjectMapper.DefaultTyping typing, PolymorphicTypeValidator ptv) {
            super(typing, ptv);
        }

        @Override
        public StdTypeResolverBuilder defaultImpl(Class<?> defaultImpl) {
            return this;
        }

        @Override
        public boolean useForType(JavaType type) {
            if (type.isJavaLangObject()) {
                return true;
            }
            type = resolveArrayOrWrapper(type);
            if (type.isEnumType() || ClassUtils.isPrimitiveOrWrapper(type.getRawClass())) {
                return false;
            }
            if (type.isFinal() && !KotlinDetector.isKotlinType(type.getRawClass()) &&
                    type.getRawClass().getPackage().getName().startsWith("java")) {
                return false;
            }
            // [databind#88] Should not apply to JSON tree models:
            return !TreeNode.class.isAssignableFrom(type.getRawClass());
        }

        private JavaType resolveArrayOrWrapper(JavaType type) {
            while (type.isArrayType()) {
                type = type.getContentType();
                if (type.isReferenceType()) {
                    type = resolveArrayOrWrapper(type);
                }
            }
            while (type.isReferenceType()) {
                type = type.getReferencedType();
                if (type.isArrayType()) {
                    type = resolveArrayOrWrapper(type);
                }
            }
            return type;
        }
    }
}
