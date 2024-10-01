package com.example.lite.cqrs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.example.lite.cqrs.Domain.Company;
import com.example.lite.cqrs.Domain.Order;
import com.example.lite.cqrs.Domain.Product;
import static com.example.lite.util.Utils.getOrDefault;

public class Decision {
    public static List<Event> decide(Command command, Map<String, Object> state) {
        return switch (command) {
            case RegisterCompany cmd -> {
                if (getOrDefault(state, "companies", new ArrayList<Company>())
                        .stream().anyMatch(c -> c.nit().equals(cmd.nit()))) {
                    yield List.of(new ErrorEvent("Company with this NIT already exists."));
                }
                yield List.of(new CompanyRegistered(cmd.nit(), cmd.name(), cmd.address(), cmd.phone()));
            }
            case UpdateCompany cmd -> {
                if (getOrDefault(state, "companies", new ArrayList<Company>())
                        .stream().noneMatch(c -> c.nit().equals(cmd.nit()))) {
                    yield List.of(new ErrorEvent("Company with this NIT does not exist."));
                }
                yield List.of(new CompanyUpdated(cmd.nit(), cmd.name(), cmd.address(), cmd.phone()));
            }
            case DeleteCompany cmd -> {
                if (getOrDefault(state, "companies", new ArrayList<Company>())
                        .stream().noneMatch(c -> c.nit().equals(cmd.nit()))) {
                    yield List.of(new ErrorEvent("Company with this NIT does not exist."));
                }
                yield List.of(new CompanyDeleted(cmd.nit()));
            }
            case AddProduct cmd -> {
                if (getOrDefault(state, "companies", new ArrayList<Company>())
                        .stream().noneMatch(c -> c.nit().equals(cmd.companyId()))) {
                    yield List.of(new ErrorEvent("Company does not exist."));
                }
                yield List.of(new ProductAdded(cmd.code(), cmd.name(), cmd.characteristics(), cmd.prices(), cmd.companyId()));
            }
            case UpdateProduct cmd -> {
                if (getOrDefault(state, "products", new ArrayList<Product>())
                        .stream().noneMatch(p -> p.code().equals(cmd.code()))) {
                    yield List.of(new ErrorEvent("Product does not exist."));
                }
                yield List.of(new ProductUpdated(cmd.code(), cmd.name(), cmd.characteristics(), cmd.prices(), cmd.companyId()));
            }
            case RemoveProduct cmd -> {
                if (getOrDefault(state, "products", new ArrayList<Product>())
                        .stream().noneMatch(p -> p.code().equals(cmd.code()))) {
                    yield List.of(new ErrorEvent("Product does not exist."));
                }
                yield List.of(new ProductRemoved(cmd.code()));
            }
            case CreateOrder cmd -> List.of(new OrderCreated(cmd.id(), cmd.clientId(), cmd.products()));
            case UpdateOrder cmd -> {
                if (getOrDefault(state, "orders", new ArrayList<Order>())
                        .stream().noneMatch(o -> o.id().equals(cmd.id()))) {
                    yield List.of(new ErrorEvent("Order does not exist."));
                }
                yield List.of(new OrderUpdated(cmd.id(), cmd.products()));
            }
            case CancelOrder cmd -> {
                if (getOrDefault(state, "orders", new ArrayList<Order>())
                        .stream().noneMatch(o -> o.id().equals(cmd.id()))) {
                    yield List.of(new ErrorEvent("Order does not exist."));
                }
                yield List.of(new OrderCancelled(cmd.id()));
            }
            default -> List.of(new ErrorEvent("Unknown command."));
        };
    }
}