package com.example.lite.util;

public record Success<T>(T value) implements Result<T> {}

