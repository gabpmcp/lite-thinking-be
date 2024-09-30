package com.example.lite.cqrs;

import java.util.List;
import java.util.Map;

import com.example.lite.cqrs.Domain.Product;
import com.example.lite.util.Message;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// Habilitar deserialización polimórfica
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RegisterCompany.class, name = "RegisterCompany"),
        @JsonSubTypes.Type(value = UpdateCompany.class, name = "UpdateCompany"),
        @JsonSubTypes.Type(value = DeleteCompany.class, name = "DeleteCompany"),
        @JsonSubTypes.Type(value = AddProduct.class, name = "AddProduct"),
        @JsonSubTypes.Type(value = UpdateProduct.class, name = "UpdateProduct"),
        @JsonSubTypes.Type(value = RemoveProduct.class, name = "RemoveProduct"),
        @JsonSubTypes.Type(value = CreateOrder.class, name = "CreateOrder"),
        @JsonSubTypes.Type(value = UpdateOrder.class, name = "UpdateOrder"),
        @JsonSubTypes.Type(value = CancelOrder.class, name = "CancelOrder")
})
public sealed interface Command extends Message permits RegisterCompany, UpdateCompany, DeleteCompany, AddProduct, UpdateProduct, RemoveProduct, CreateOrder, UpdateOrder, CancelOrder {}

record RegisterCompany(String nit, String name, String address, String phone) implements Command {}
record UpdateCompany(String nit, String name, String address, String phone) implements Command {}
record DeleteCompany(String nit) implements Command {}
record AddProduct(String code, String name, String characteristics, Map<String, Double> prices, String companyId) implements Command {}
record UpdateProduct(String code, String name, String characteristics, Map<String, Double> prices, String companyId) implements Command {}
record RemoveProduct(String code) implements Command {}
record CreateOrder(Long id, String clientId, List<Product> products) implements Command {}
record UpdateOrder(Long id, List<Product> products) implements Command {}
record CancelOrder(Long id) implements Command {}

