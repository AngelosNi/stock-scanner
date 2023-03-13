package gr.trading.scanner.utitlities;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DateTimeUtils {

    public LocalDateTime getNowDay() {
        return LocalDate.now().atStartOfDay();
    }

    public LocalDateTime getNowDayTime() {
        return LocalDateTime.now();
    }

    public List<LocalDate> getInBetweenDates(LocalDate start, LocalDate end) {
        List<LocalDate> inBetweenDates = new ArrayList<>();
        for (LocalDate date = start; date.isBefore(end); date = addDaysSkippingWeekends(date.atStartOfDay(), 1).toLocalDate()) {
            inBetweenDates.add(date);
        }

        return inBetweenDates;
    }

    public LocalDateTime addDaysSkippingWeekends(LocalDateTime date, int days) {
        LocalDateTime result = date;
        int addedDays = 0;
        while (addedDays < days) {
            result = result.plusDays(1);
            if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY || result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                ++addedDays;
            }
        }
        return result;
    }

    public LocalDateTime subtractDaysSkippingWeekends(LocalDateTime date, int days) {
        LocalDateTime result = date;
        int subtractedDays = 0;
        while (subtractedDays < days) {
            result = result.minusDays(1);
            if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY || result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                ++subtractedDays;
            }
        }
        return result;
    }
}
