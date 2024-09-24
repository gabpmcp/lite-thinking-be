package com.example.lite.cqrs;

import java.util.List;
import java.util.Map;

import com.example.lite.cqrs.Domain.Product;
import com.example.lite.util.Message;

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

