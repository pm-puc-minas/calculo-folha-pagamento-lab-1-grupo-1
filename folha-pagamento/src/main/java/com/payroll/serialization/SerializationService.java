package com.payroll.serialization;

import java.util.List;
import java.util.Optional;

public interface SerializationService<T> {
    String serialize(T obj);
    Optional<T> deserialize(String json);
    String serializeList(List<T> list);
    List<T> deserializeList(String json);
}