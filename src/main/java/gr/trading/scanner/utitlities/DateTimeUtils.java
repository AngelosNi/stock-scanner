package gr.trading.scanner.utitlities;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DateTimeUtils {

    public LocalDateTime getNowDay() {
        return LocalDate.now().minusDays(5).atStartOfDay();
    }

    public LocalDateTime getNowDayTime() {
        return LocalDateTime.now().minusDays(5);
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
