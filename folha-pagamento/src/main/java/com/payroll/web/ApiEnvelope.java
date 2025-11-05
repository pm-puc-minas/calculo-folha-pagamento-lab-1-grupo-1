package com.payroll.web;
/*
 * Envelope padrão para respostas da API (sucesso/erro).
 * Inclui metadados e payload, melhora consistência da camada web
 * e ajuda na serialização e documentação.
 * Uniformiza formato de respostas entre endpoints.
 */

public class ApiEnvelope<T> {
    private T data;
    private String message;

    public ApiEnvelope() {}
    public ApiEnvelope(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}