package ru.javawebinar.topjava.web.formatter;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDatTimeFormatter implements Formatter<LocalDateTime>{

    @Override
    public LocalDateTime parse(String localDateTime, Locale locale) throws ParseException {
        return LocalDateTime.parse(localDateTime);
    }

    @Override
    public String print(LocalDateTime localDateTime, Locale locale) {
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
