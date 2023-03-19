package gr.trading.scanner.utitlities;

import gr.trading.scanner.model.Interval;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
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

    public List<LocalDateTime> getInBetweenTimes(LocalDateTime start, LocalDateTime end, Interval interval) {
        List<LocalDateTime> inBetweenDates = new ArrayList<>();
        for (LocalDateTime time = start; time.isBefore(end); time = addIntervalsSkippingOffHours(time, 1, interval)) {
            inBetweenDates.add(time);
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

    private LocalDateTime addIntervalsSkippingOffHours(LocalDateTime dateTime, int periods, Interval interval) {
        switch (interval) {
            case D1:
                return addDaysSkippingWeekends(dateTime, periods);
            case M5:
                return dateTime.plusMinutes(5L * periods);
            default:
                throw new InvalidParameterException();
        }
    }
}
