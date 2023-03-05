package gr.trading.scanner.utitlities;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DateTimeUtils {

    public LocalDateTime getNowDay() {
        return LocalDate.now().atStartOfDay();
    }

    public LocalDateTime getNowDayTime() {
        return LocalDateTime.now();
    }
}
