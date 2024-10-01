package com.example.lite.cqrs;

public record ErrorEvent(String error) implements Event { }
