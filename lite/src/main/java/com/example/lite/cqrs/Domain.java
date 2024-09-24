package com.example.lite.cqrs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface Domain {
    record Company(String nit, String name, String address, String phone) {}
    record Product(String code, String name, String characteristics, Map<String, Double> prices, String companyId) {}
    record Order(Long id, String clientId, List<Product> products, LocalDateTime date, String status) {}
}