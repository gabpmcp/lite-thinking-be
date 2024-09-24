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
                    throw new IllegalArgumentException("Company with this NIT already exists.");
                }
                yield new ArrayList<Event>() {{ add(new CompanyRegistered(cmd.nit(), cmd.name(), cmd.address(), cmd.phone())); }};
            }
            case UpdateCompany cmd -> {
                if (!getOrDefault(state, "companies", new ArrayList<Company>())
                        .stream().anyMatch(c -> c.nit().equals(cmd.nit()))) {
                    throw new IllegalArgumentException("Company with this NIT does not exist.");
                }
                yield new ArrayList<Event>() {{ add(new CompanyUpdated(cmd.nit(), cmd.name(), cmd.address(), cmd.phone())); }};
            }
            case DeleteCompany cmd -> {
                if (!getOrDefault(state, "companies", new ArrayList<Company>())
                        .stream().anyMatch(c -> c.nit().equals(cmd.nit()))) {
                    throw new IllegalArgumentException("Company with this NIT does not exist.");
                }
                yield new ArrayList<Event>() {{ add(new CompanyDeleted(cmd.nit())); }};
            }
            case AddProduct cmd -> {
                if (!getOrDefault(state, "companies", new ArrayList<Company>())
                        .stream().anyMatch(c -> c.nit().equals(cmd.companyId()))) {
                    throw new IllegalArgumentException("Company does not exist.");
                }
                yield new ArrayList<Event>() {{ add(new ProductAdded(cmd.code(), cmd.name(), cmd.characteristics(), cmd.prices(), cmd.companyId())); }};
            }
            case UpdateProduct cmd -> {
                if (!(getOrDefault(state, "products", new ArrayList<Product>()))
                        .stream().anyMatch(p -> p.code().equals(cmd.code()))) {
                    throw new IllegalArgumentException("Product does not exist.");
                }
                yield new ArrayList<Event>() {{ add(new ProductUpdated(cmd.code(), cmd.name(), cmd.characteristics(), cmd.prices(), cmd.companyId())); }};
            }
            case RemoveProduct cmd -> {
                if (!(getOrDefault(state, "products", new ArrayList<Product>()))
                        .stream().anyMatch(p -> p.code().equals(cmd.code()))) {
                    throw new IllegalArgumentException("Product does not exist.");
                }
                yield new ArrayList<Event>() {{ add(new ProductRemoved(cmd.code())); }};
            }
            case CreateOrder cmd -> {
                yield new ArrayList<Event>() {{ add(new OrderCreated(cmd.id(), cmd.clientId(), cmd.products())); }};
            }
            case UpdateOrder cmd -> {
                if (!(getOrDefault(state, "orders", new ArrayList<Order>()))
                        .stream().anyMatch(o -> o.id().equals(cmd.id()))) {
                    throw new IllegalArgumentException("Order does not exist.");
                }
                yield new ArrayList<Event>() {{ add(new OrderUpdated(cmd.id(), cmd.products())); }};
            }
            case CancelOrder cmd -> {
                if (!(getOrDefault(state, "orders", new ArrayList<Order>()))
                        .stream().anyMatch(o -> o.id().equals(cmd.id()))) {
                    throw new IllegalArgumentException("Order does not exist.");
                }
                yield new ArrayList<Event>() {{ add(new OrderCancelled(cmd.id())); }};
            }
            default -> throw new IllegalArgumentException("Unknown command.");
        };
    }

    public static boolean hasPermission(Command command, Authentication authentication) {
        // Verificar permisos en función del tipo de comando y el rol del usuario
        String role = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst().orElse("");

        if (command instanceof RegisterCompany || command instanceof UpdateCompany) {
            return role.equals("ROLE_ADMIN");
        } else if (command instanceof CreateOrder || command instanceof UpdateOrder) {
            return role.equals("ROLE_USER");
        }
        // Agregar más verificaciones según el tipo de comando y permisos
        return false;
    }
}