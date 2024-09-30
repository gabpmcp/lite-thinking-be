package com.example.lite.cqrs;

import java.util.List;
import java.util.Map;

import com.example.lite.cqrs.Domain.Product;
import com.example.lite.util.Message;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, // Usa el nombre del tipo para determinar la clase
        include = JsonTypeInfo.As.PROPERTY, // La información del tipo se incluirá como una propiedad en el JSON
        property = "type" // La propiedad que indicará el tipo de evento
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CompanyRegistered.class, name = "CompanyRegistered"),
    @JsonSubTypes.Type(value = CompanyUpdated.class, name = "CompanyUpdated"),
    @JsonSubTypes.Type(value = CompanyDeleted.class, name = "CompanyDeleted"),
    @JsonSubTypes.Type(value = ProductAdded.class, name = "ProductAdded"),
    @JsonSubTypes.Type(value = ProductUpdated.class, name = "ProductUpdated"),
    @JsonSubTypes.Type(value = ProductRemoved.class, name = "ProductRemoved"),
    @JsonSubTypes.Type(value = OrderCreated.class, name = "OrderCreated"),
    @JsonSubTypes.Type(value = OrderUpdated.class, name = "OrderUpdated"),
    @JsonSubTypes.Type(value = OrderCancelled.class, name = "OrderCancelled"),
    @JsonSubTypes.Type(value = ErrorEvent.class, name = "ErrorEvent")
})
public sealed interface Event extends Message permits CompanyRegistered, CompanyUpdated, CompanyDeleted, ProductAdded, ProductUpdated, ProductRemoved, OrderCreated, OrderUpdated, OrderCancelled, ErrorEvent {
}

record CompanyRegistered(String nit, String name, String address, String phone) implements Event {

}

record CompanyUpdated(String nit, String name, String address, String phone) implements Event {

}

record CompanyDeleted(String nit) implements Event {

}

record ProductAdded(String code, String name, String characteristics, Map<String, Double> prices, String companyId) implements Event {

}

record ProductUpdated(String code, String name, String characteristics, Map<String, Double> prices, String companyId) implements Event {

}

record ProductRemoved(String code) implements Event {

}

record OrderCreated(Long id, String clientId, List<Product> products) implements Event {

}

record OrderUpdated(Long id, List<Product> products) implements Event {

}

record OrderCancelled(Long id) implements Event {

}

record ErrorEvent(String error) implements Event {

}
