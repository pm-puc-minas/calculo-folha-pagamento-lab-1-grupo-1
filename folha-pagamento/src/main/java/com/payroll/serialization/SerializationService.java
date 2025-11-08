package com.payroll.serialization;
/*
 * Contrato para serviços de serialização.
 * Define operações comuns (ex.: toJson, formatação/normalização),
 * permitindo implementações específicas por domínio.
 * Padroniza conversões de objetos para saída (JSON/strings) no backend.
 */

import java.util.List;
import java.util.Optional;

public interface SerializationService<T> {
    String serialize(T obj);
    Optional<T> deserialize(String json);
    String serializeList(List<T> list);
    List<T> deserializeList(String json);
}