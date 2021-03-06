package com.example.huykhoahuy.finalproject.OCR_Task.OCR_Pre_Processing;

public class DateCheckerLen7b extends DateCheckerAbstract {
    @Override
    public boolean checkDate(String rawDate) {
        int day = Integer.parseInt(rawDate.substring(0, 1));
        int month = Integer.parseInt(rawDate.substring(1, 3));
        int year = Integer.parseInt(rawDate.substring(3, 7));
        return checkIfDayExist(day, month, year);
    }

    @Override
    public String parseStringToDate(String rawDate) {
        StringBuilder result = new StringBuilder();

        result.append(rawDate.substring(0, 1));
        result.append("-");
        result.append(rawDate.substring(1, 3));
        result.append("-");
        result.append(rawDate.substring(3, 7));

        return result.toString();
    }
}
