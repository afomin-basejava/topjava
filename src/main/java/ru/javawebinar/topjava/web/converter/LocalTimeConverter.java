package ru.javawebinar.topjava.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class LocalTimeConverter implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ISO_TIME);
    }
}
