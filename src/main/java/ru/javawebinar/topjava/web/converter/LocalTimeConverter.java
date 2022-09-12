package ru.javawebinar.topjava.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalTime;

@Component
public class LocalTimeConverter implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(String time) {
        return DateTimeUtil.parseLocalTime(time);
    }
}
