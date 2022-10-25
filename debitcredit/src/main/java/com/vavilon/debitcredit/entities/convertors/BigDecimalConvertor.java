package com.vavilon.debitcredit.entities.convertors;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BigDecimalConvertor implements Converter<String, BigDecimal> {

    @Override
    public BigDecimal convert(String source) {
        return new BigDecimal(source.replace(',', '.'));
    }
}
