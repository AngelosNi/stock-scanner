package gr.trading.scanner.mappers;

import gr.trading.scanner.model.TwelveDataResponseDto;
import gr.trading.scanner.model.entities.DailyDataEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class TwelveDataBarToDataEntityMapper {

    public DailyDataEntity map(TwelveDataResponseDto.Bar bar, String symbol) {
        DailyDataEntity entity = new DailyDataEntity();

        entity.setClosePrice(bar.getClose().doubleValue());
        entity.setHighPrice(bar.getHigh().doubleValue());
        entity.setOpenPrice(bar.getOpen().doubleValue());
        entity.setLowPrice(bar.getLow().doubleValue());
        entity.setVolume(bar.getVolume());

        DateTimeFormatter formatter;
        LocalDateTime time;
        if (bar.getDateTime().matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            time = LocalDate.parse(bar.getDateTime(), formatter).atStartOfDay();
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            time = LocalDateTime.parse(bar.getDateTime(), formatter);
        }

        entity.setSymbolDateId(new DailyDataEntity.SymbolDateId(symbol, time.toLocalDate()));

        return entity;
    }
}
