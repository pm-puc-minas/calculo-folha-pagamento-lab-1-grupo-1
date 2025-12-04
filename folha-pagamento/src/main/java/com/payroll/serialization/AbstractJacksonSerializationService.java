package com.payroll.serialization;
/*
 * Implementação base de serialização usando Jackson.
 * Centraliza configuração/padronização de JSON (mapper)
 * e auxilia subclasses com utilitários de serialização.
 * Fundação comum para serviços de serialização do projeto.
 */


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class AbstractJacksonSerializationService<T> implements SerializationService<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected final ObjectMapper objectMapper;
    protected final Class<T> type;

    protected AbstractJacksonSerializationService(ObjectMapper objectMapper, Class<T> type) {
        this.objectMapper = objectMapper;
        this.type = type;
    }

    @Override
    public String serialize(T obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("Failed to serialize {}: {}", type.getSimpleName(), e.getMessage());
            return "";
        }
    }

    @Override
    public Optional<T> deserialize(String json) {
        try {
            T value = objectMapper.readValue(json, type);
            return Optional.ofNullable(value);
        } catch (Exception e) {
            logger.error("Failed to deserialize {}: {}", type.getSimpleName(), e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public String serializeList(List<T> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            logger.error("Failed to serialize list of {}: {}", type.getSimpleName(), e.getMessage());
            return "[]";
        }
    }

    @Override
    public List<T> deserializeList(String json) {
        try {
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
            return objectMapper.readValue(json, listType);
        } catch (Exception e) {
            logger.error("Failed to deserialize list of {}: {}", type.getSimpleName(), e.getMessage());
            return Collections.emptyList();
        }
    }
}