package com.example.lite.util;

public record Failure<T>(T error) implements Result<T> {}
