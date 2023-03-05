package gr.trading.scanner.mappers;

import com.ib.client.Bar;
import gr.trading.scanner.model.OhlcBar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class BarToOhlcBarMapper {

    public OhlcBar map(Bar bar) {
        OhlcBar ohlcBar = new OhlcBar();

        ohlcBar.setClose(bar.close());
        ohlcBar.setHigh(bar.high());
        ohlcBar.setOpen(bar.open());
        ohlcBar.setLow(bar.low());
        ohlcBar.setVolume(bar.volume().value());

        DateTimeFormatter formatter;
        LocalDateTime time;
        if (bar.time().matches("^[0-9]{8}$")) {
            formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            time = LocalDate.parse(bar.time(), formatter).atStartOfDay();
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss z");
            time = LocalDateTime.parse(bar.time(), formatter);
        }

        // Fix wrong timezone returned from provider
        ohlcBar.setTime(time);
        return ohlcBar;
    }
}
