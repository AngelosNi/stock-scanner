package gr.trading.scanner.mappers;

import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.TwelveDataResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class TwelveDataBarToOhlcBarMapper {

    public OhlcBar map(TwelveDataResponseDto.Bar bar) {
        OhlcBar ohlcBar = new OhlcBar();

        ohlcBar.setClose(bar.getClose().doubleValue());
        ohlcBar.setHigh(bar.getHigh().doubleValue());
        ohlcBar.setOpen(bar.getOpen().doubleValue());
        ohlcBar.setLow(bar.getLow().doubleValue());
        ohlcBar.setVolume(bar.getVolume());

        DateTimeFormatter formatter;
        LocalDateTime time;
        if (bar.getDateTime().matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            time = LocalDate.parse(bar.getDateTime(), formatter).atStartOfDay();
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            time = LocalDateTime.parse(bar.getDateTime(), formatter);
        }

        ohlcBar.setTime(time);

        return ohlcBar;
    }
}
