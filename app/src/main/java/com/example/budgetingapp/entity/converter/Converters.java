package com.example.budgetingapp.entity.converter;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class Converters {
    @TypeConverter
    public static LocalDate stringToLocalDate(String s) {
        return LocalDate.parse(s);
    }

    @TypeConverter
    public static String localDateToString(LocalDate date) {
        return date.toString();
    }
}
