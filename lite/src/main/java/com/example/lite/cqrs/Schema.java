package com.example.lite.cqrs;

import com.example.lite.util.Failure;
import com.example.lite.util.Message;
import com.example.lite.util.Result;
import com.example.lite.util.Success;

public class Schema {
    public static Result<Message> validateCommand(Command command) {
        return switch (command) {
            case RegisterCompany cmd -> {
                if (cmd.nit() == null || cmd.nit().isEmpty() ||
                    cmd.name() == null || cmd.name().isEmpty() ||
                    cmd.address() == null || cmd.address().isEmpty() ||
                    cmd.phone() == null || cmd.phone().isEmpty()) {
                    yield new Failure<>(new ErrorEvent("Invalid RegisterCompany command."));
                }
                yield new Success<>(command);
            }
            case UpdateCompany cmd -> {
                if (cmd.nit() == null || cmd.nit().isEmpty() ||
                    cmd.name() == null || cmd.name().isEmpty() ||
                    cmd.address() == null || cmd.address().isEmpty() ||
                    cmd.phone() == null || cmd.phone().isEmpty()) {
                    yield new Failure<>(new ErrorEvent("Invalid UpdateCompany command."));
                }
                yield new Success<>(command);
            }
            case DeleteCompany cmd -> {
                if (cmd.nit() == null || cmd.nit().isEmpty()) {
                    yield new Failure<>(new ErrorEvent("Invalid DeleteCompany command."));
                }
                yield new Success<>(command);
            }
            case AddProduct cmd -> {
                if (cmd.code() == null || cmd.code().isEmpty() ||
                    cmd.name() == null || cmd.name().isEmpty() ||
                    cmd.characteristics() == null || cmd.characteristics().isEmpty() ||
                    cmd.prices() == null || cmd.prices().isEmpty() ||
                    cmd.companyId() == null || cmd.companyId().isEmpty()) {
                    yield new Failure<>(new ErrorEvent("Invalid AddProduct command."));
                }
                yield new Success<>(command);
            }
            case UpdateProduct cmd -> {
                if (cmd.code() == null || cmd.code().isEmpty() ||
                    cmd.name() == null || cmd.name().isEmpty() ||
                    cmd.characteristics() == null || cmd.characteristics().isEmpty() ||
                    cmd.prices() == null || cmd.prices().isEmpty() ||
                    cmd.companyId() == null || cmd.companyId().isEmpty()) {
                    yield new Failure<>(new ErrorEvent("Invalid UpdateProduct command."));
                }
                yield new Success<>(command);
            }
            case RemoveProduct cmd -> {
                if (cmd.code() == null || cmd.code().isEmpty()) {
                    yield new Failure<>(new ErrorEvent("Invalid RemoveProduct command."));
                }
                yield new Success<>(new ProductRemoved(cmd.code()));
            }
            case CreateOrder cmd -> {
                if (cmd.id() == null ||
                    cmd.clientId() == null || cmd.clientId().isEmpty() ||
                    cmd.products() == null || cmd.products().isEmpty()) {
                    yield new Failure<>(new ErrorEvent("Invalid CreateOrder command."));
                }
                yield new Success<>(command);
            }
            case UpdateOrder cmd -> {
                if (cmd.id() == null ||
                    cmd.products() == null || cmd.products().isEmpty()) {
                    yield new Failure<>(new ErrorEvent("Invalid UpdateOrder command."));
                }
                yield new Success<>(command);
            }
            case CancelOrder cmd -> {
                if (cmd.id() == null) {
                    yield new Failure<>(new ErrorEvent("Invalid CancelOrder command."));
                }
                yield new Success<>(command);
            }
            default -> new Failure<>(new ErrorEvent("Unknown command."));
        };
    }
}
