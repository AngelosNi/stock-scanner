package gr.trading.scanner.mappers;

import com.ib.client.Bar;
import gr.trading.scanner.model.OhlcBar;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BarToOhlcBarMapper {

    public OhlcBar map(Bar bar) {
        OhlcBar ohlcBar = new OhlcBar();

        ohlcBar.setClose(bar.close());
        ohlcBar.setHigh(bar.high());
        ohlcBar.setOpen(bar.open());
        ohlcBar.setLow(bar.low());
        ohlcBar.setVolume(bar.volume().value());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss z");
        ohlcBar.setTime(LocalDateTime.parse(bar.time(), formatter));

        return ohlcBar;
    }
}
