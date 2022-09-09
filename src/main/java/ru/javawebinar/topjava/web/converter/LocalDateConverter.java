package ru.javawebinar.topjava.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class LocalDateConverter implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
    }
}
