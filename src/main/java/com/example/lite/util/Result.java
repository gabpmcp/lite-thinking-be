package com.example.lite.util;

public sealed interface Result<T> permits Success, Failure {}
