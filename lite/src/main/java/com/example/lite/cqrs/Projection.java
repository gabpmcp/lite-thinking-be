package com.example.lite.cqrs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.lite.cqrs.Domain.Company;
import com.example.lite.cqrs.Domain.Order;
import com.example.lite.cqrs.Domain.Product;

import static com.example.lite.util.Utils.getOrDefault;

public class Projection {
    public static Map<String, Object> project(List<Event> events) {
        Map<String, Object> state = new HashMap<>();
        events.forEach(event -> dispatchEvent(state, event));
        return state;
    }

    private static void dispatchEvent(Map<String, Object> state, Event event) {
        switch (event) {
            case CompanyRegistered e -> projectCompanyRegistered(state, e);
            case CompanyUpdated e -> projectCompanyUpdated(state, e);
            case CompanyDeleted e -> projectCompanyDeleted(state, e);
            case ProductAdded e -> projectProductAdded(state, e);
            case ProductUpdated e -> projectProductUpdated(state, e);
            case ProductRemoved e -> projectProductRemoved(state, e);
            case OrderCreated e -> projectOrderCreated(state, e);
            case OrderUpdated e -> projectOrderUpdated(state, e);
            case OrderCancelled e -> projectOrderCancelled(state, e);
            default -> throw new IllegalStateException("Unexpected event type: " + event.getClass().getSimpleName());
        }
    }

    // Proyecciones simplificadas por cada evento
    private static void projectCompanyRegistered(Map<String, Object> state, CompanyRegistered event) {
        var companies = getOrDefault(state, "companies", new ArrayList<>());
        companies.add(new Company(event.nit(), event.name(), event.address(), event.phone()));
        state.put("companies", companies);
    }

    private static void projectCompanyUpdated(Map<String, Object> state, CompanyUpdated event) {
        var companies = getOrDefault(state, "companies", new ArrayList<Company>());
        if (companies != null) {
            companies.replaceAll(c -> c.nit().equals(event.nit()) ? 
                new Company(event.nit(), event.name(), event.address(), event.phone()) : c);
        }
    }

    private static void projectCompanyDeleted(Map<String, Object> state, CompanyDeleted event) {
        var companies = getOrDefault(state, "companies", new ArrayList<Company>());
        if (companies != null) {
            companies.removeIf(c -> c.nit().equals(event.nit()));
        }
    }

    private static void projectProductAdded(Map<String, Object> state, ProductAdded event) {
        var products = getOrDefault(state, "products", new ArrayList<>());
        products.add(new Product(event.code(), event.name(), event.characteristics(), event.prices(), event.companyId()));
        state.put("products", products);
    }

    private static void projectProductUpdated(Map<String, Object> state, ProductUpdated event) {
        var products = getOrDefault(state, "products", new ArrayList<Product>());
        if (products != null) {
            products.replaceAll(p -> p.code().equals(event.code()) ?
                new Product(event.code(), event.name(), event.characteristics(), event.prices(), event.companyId()) : p);
        }
    }

    private static void projectProductRemoved(Map<String, Object> state, ProductRemoved event) {
        var products = getOrDefault(state, "products", new ArrayList<Product>());
        if (products != null) {
            products.removeIf(p -> p.code().equals(event.code()));
        }
    }

    private static void projectOrderCreated(Map<String, Object> state, OrderCreated event) {
        var orders = getOrDefault(state, "orders", new ArrayList<>());
        orders.add(new Order(event.id(), event.clientId(), event.products(), LocalDateTime.now(), "CREATED"));
        state.put("orders", orders);
    }

    private static void projectOrderUpdated(Map<String, Object> state, OrderUpdated event) {
        var orders = getOrDefault(state, "orders", new ArrayList<Order>());
        if (orders != null) {
            orders.replaceAll(o -> o.id().equals(event.id()) ?
                new Order(event.id(), o.clientId(), event.products(), o.date(), o.status()) : o);
        }
    }

    private static void projectOrderCancelled(Map<String, Object> state, OrderCancelled event) {
        var orders = getOrDefault(state, "orders", new ArrayList<Order>());
        if (orders != null) {
            orders.replaceAll(o -> o.id().equals(event.id()) ?
                new Order(event.id(), o.clientId(), o.products(), o.date(), "CANCELLED") : o);
        }
    }
}
