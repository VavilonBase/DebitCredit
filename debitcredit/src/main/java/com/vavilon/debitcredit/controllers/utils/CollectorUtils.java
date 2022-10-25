package com.vavilon.debitcredit.controllers.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;

public class CollectorUtils {
    public static Map<String, String> getErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream().collect(Collectors.toMap(
                fieldError -> fieldError.getField() + "Error",
                FieldError::getDefaultMessage
        ));
    }

    public static FieldError getFieldError(String fieldName) {
        return null;
    }

    public static void printErrors(Map<String, String> errorsMap) {
        for (Map.Entry<String, String> error : errorsMap.entrySet()) {
            System.out.printf("%s: %s\n", error.getKey(), error.getValue());
        }
    }
}
