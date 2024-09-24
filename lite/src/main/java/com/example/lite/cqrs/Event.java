package com.example.lite.cqrs;

import java.util.List;
import java.util.Map;

import com.example.lite.cqrs.Domain.Product;
import com.example.lite.util.Message;

public sealed interface Event extends Message permits CompanyRegistered, CompanyUpdated, CompanyDeleted, ProductAdded, ProductUpdated, ProductRemoved, OrderCreated, OrderUpdated, OrderCancelled, ErrorEvent {}

record CompanyRegistered(String nit, String name, String address, String phone) implements Event {}
record CompanyUpdated(String nit, String name, String address, String phone) implements Event {}
record CompanyDeleted(String nit) implements Event {}
record ProductAdded(String code, String name, String characteristics, Map<String, Double> prices, String companyId) implements Event {}
record ProductUpdated(String code, String name, String characteristics, Map<String, Double> prices, String companyId) implements Event {}
record ProductRemoved(String code) implements Event {}
record OrderCreated(Long id, String clientId, List<Product> products) implements Event {}
record OrderUpdated(Long id, List<Product> products) implements Event {}
record OrderCancelled(Long id) implements Event {}