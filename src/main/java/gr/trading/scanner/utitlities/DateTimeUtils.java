package gr.trading.scanner.utitlities;

import gr.trading.scanner.model.Interval;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class DateTimeUtils {

    public LocalDateTime getNowDayAtSessionStart() {
        return LocalDateTime.now().atZone(ZoneId.of("Europe/Athens"))
                .withZoneSameInstant(ZoneId.of("America/New_York"))
                .toLocalDate()
                .atStartOfDay();
    }

    public LocalDateTime getNowDayTime() {
        return LocalDateTime.now().atZone(ZoneId.of("Europe/Athens"))
                .withZoneSameInstant(ZoneId.of("America/New_York"))
                .toLocalDateTime();
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

    public LocalDateTime getLastWorkingHoursDateTime(Interval interval) {
        LocalDateTime now = getNowDayTime();
        if (interval == Interval.D1) {
            now = now.toLocalDate().atStartOfDay();
        }
        return subtractIntervalsSkippingOffHours(now, 1, interval);
    }

    private LocalDateTime add5MinutesSkippingOffHours(LocalDateTime dateTime, int periods) {
        LocalDateTime result = dateTime;
        int addedMinutes = 0;
        while (addedMinutes < periods) {
            result = result.plusMinutes(5);
            if (result.isAfter(result.toLocalDate().atTime(9, 29, 0))
                    && result.isBefore(result.toLocalDate().atTime(15, 56, 0))
                    && result.getDayOfWeek() != DayOfWeek.SATURDAY
                    && result.getDayOfWeek() != DayOfWeek.SUNDAY) {
                addedMinutes += 5;
            }
        }
        return result;
    }

    private LocalDateTime addIntervalsSkippingOffHours(LocalDateTime dateTime, int periods, Interval interval) {
        switch (interval) {
            case D1:
                return addDaysSkippingWeekends(dateTime.toLocalDate().atStartOfDay(), periods);
            case M5:
                return add5MinutesSkippingOffHours(LocalDateTime.of(dateTime.toLocalDate(), LocalTime.of(dateTime.getHour(), dateTime.plusMinutes(5 - (dateTime.getMinute()) % 5).getMinute())), 5 * periods);  // Rounded to the nearest next 5 minuter
            default:
                throw new InvalidParameterException();
        }
    }

    private LocalDateTime subtract5MinutesSkippingOffHours(LocalDateTime dateTime, int periods) {
        LocalDateTime result = dateTime;
        int subtractedMinutes = 0;
        while (subtractedMinutes < periods) {
            result = result.minusMinutes(5);
            if (result.isAfter(result.toLocalDate().atTime(9, 29, 0))
                    && result.isBefore(result.toLocalDate().atTime(15, 56, 0))
                    && result.getDayOfWeek() != DayOfWeek.SATURDAY
                    && result.getDayOfWeek() != DayOfWeek.SUNDAY) {
                subtractedMinutes += 5;
            }
        }
        return result;
    }

    private LocalDateTime subtractIntervalsSkippingOffHours(LocalDateTime dateTime, int periods, Interval interval) {
        switch (interval) {
            case D1:
                return subtractDaysSkippingWeekends(dateTime.toLocalDate().atStartOfDay(), periods);
            case M5:
                return subtract5MinutesSkippingOffHours(LocalDateTime.of(dateTime.toLocalDate(), LocalTime.of(dateTime.getHour(), dateTime.minusMinutes(dateTime.getMinute() % 5).getMinute())), 5 * periods);  // Rounded to the nearest previous 5 minuter
            default:
                throw new InvalidParameterException();
        }
    }
}
